package com.xshiwu;

import com.binarywang.spring.starter.wxjava.mp.config.WxMpAutoConfiguration;
import com.binarywang.spring.starter.wxjava.mp.config.WxMpServiceAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        WxMpAutoConfiguration.class,
        RedisAutoConfiguration.class,
        WebMvcAutoConfiguration.class
})
public class ImApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImApplication.class, args);
    }

}
