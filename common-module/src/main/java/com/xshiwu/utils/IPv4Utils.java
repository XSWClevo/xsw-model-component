package com.xshiwu.utils;

import cn.hutool.core.util.StrUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IPv4Utils {
    /**
     * 传入字符串返回可解析的ip地址
     */
    public static String getIpAddress(String str) {
        String regex = "\\b(?:[0-9]{1,3}\\.){3}[0-9]{1,3}\\b";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        StringBuilder ipAddress = new StringBuilder(StrUtil.EMPTY);
        while (matcher.find()) {
            ipAddress.append(matcher.group());
        }
        return ipAddress.toString();
    }

    /**
     * 匿名地址转本地地址，本地测试用
     */
    public static String anonymityAddressToLocal(String metadata) {
        return metadata.replace("0:0:0:0:0:0:0:1", "127.0.0.1");
    }
}
