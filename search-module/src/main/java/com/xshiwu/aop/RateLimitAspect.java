package com.xshiwu.aop;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.xshiwu.annotation.RateLimiter;
import com.xshiwu.common.ErrorCode;
import com.xshiwu.constant.RedisKeyConstant;
import com.xshiwu.exception.BusinessException;
import com.xshiwu.exception.ThrowUtils;
import com.xshiwu.model.enums.LimitType;
import com.xshiwu.utils.IPv4Utils;
import com.xshiwu.utils.RedisUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Collections;

@Aspect
@Component
public class RateLimitAspect {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitAspect.class);

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    RedisScript<Long> redisScript; //实现类为DefaultRedisScript<Long> limitScript()

    @Pointcut("@annotation(com.xshiwu.annotation.RateLimiter)")
    public void pointCut() {
    }

    @Before("pointCut()")
    public void beforeRateLimit(JoinPoint jp) {
        //获取RateLimiter注解上的值
        MethodSignature methodSignature = (MethodSignature) jp.getSignature();
        RateLimiter rateLimiter = AnnotationUtils.findAnnotation(methodSignature.getMethod(), RateLimiter.class);
        ThrowUtils.throwIf(rateLimiter == null, ErrorCode.SYSTEM_ERROR);
        int time = rateLimiter.time();
        int count = rateLimiter.count();
        //构建redis中的key值
        String rateKey = getRateLimitKey(rateLimiter, methodSignature);
        System.out.println("redis中key值：" + rateKey);
        Long current = redisTemplate.execute(redisScript, Collections.singletonList(rateKey), time, count);
        if (current == null || current.intValue() >= count) {
            logger.info("当前接口达到最大限流次数");
            // 记录次IP、记作黑名单IP
            String ipAddress = IPv4Utils.getIpAddress(IPv4Utils.anonymityAddressToLocal(rateKey));
            long expirationTime = 60;
            RedisUtils.set(RedisKeyConstant.BAN_IP_PREFIX + ipAddress, ipAddress, expirationTime);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, String.format("IP已被封禁，请%d分钟后尝试", expirationTime / 60));
        }
        logger.info("一段时间内允许的请求次数:{},当前请求次数:{},缓存的key为:{}", count, current, rateKey);
    }

    /**
     * redis中key两种类型格式为：
     * 1.  rate_limit:com.xxx.controller.HelloController-hello
     * 2.  rate_limit:127.0.0.1-com.xxx.controller.HelloController-hello
     */
    private String getRateLimitKey(RateLimiter rateLimiter, MethodSignature methodSignature) {
        StringBuffer key = new StringBuffer(rateLimiter.key());
        if (rateLimiter.limitType() == LimitType.IP) {//如果参数类型为IP
            //获取客户端ip
            String clientIP = ServletUtil.getClientIP(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
            Object ipAddress = RedisUtils.get(RedisKeyConstant.BAN_IP_PREFIX + IPv4Utils.anonymityAddressToLocal(clientIP));
            if (ObjectUtil.isNotEmpty(ipAddress)) {
                throw new BusinessException(ErrorCode.IP_BAN_ERROR);
            }
            logger.info("客户端IP为：{}", clientIP);
            key.append(clientIP).append("-");
        }
        Method method = methodSignature.getMethod();
        //获取全类名
        String className = method.getDeclaringClass().getName();
        //获取方法名
        String methodName = method.getName();
        key.append(className)
                .append("-")
                .append(methodName);
        logger.info("全类名+方法名 {} - {}", className, methodName);
        return key.toString();
    }
}
