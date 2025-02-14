package com.example.server.util.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 曾佳宝
 */
public class TokenGenerator {

    /**
     * 根据传入的用户名和用户ID生成token
     * * 这里使用的是HS256加密算法
     *
     * @param username 用户名
     * @param type     用户类型
     * @param password 密码
     * @return token
     */
    public static String generateToken(String username, String password, int type) {

        Date date = new Date(System.currentTimeMillis() + TokenConstant.getExpiredTime());
        Algorithm algorithm = Algorithm.HMAC256(TokenConstant.getSecretKey());
        Map<String, Object> header = new HashMap<>(2);
        header.put("typ", "JWT");
        header.put("alg", "HS256");

        return JWT.create()
                .withHeader(header)
                .withClaim("type", type)
                .withClaim("username", username)
                .withClaim("password", password)
                .withExpiresAt(date)
                .sign(algorithm);
    }


}
