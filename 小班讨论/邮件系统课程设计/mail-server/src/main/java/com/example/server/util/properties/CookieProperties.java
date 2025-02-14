package com.example.server.util.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author 曾佳宝
 */
@Component
public class CookieProperties {

    @Value("${cookie.domain}")
    public String domain;

    @Value("${cookie.path}")
    public String path;

    @Value("${cookie.maxAge}")
    public int maxAge;
}
