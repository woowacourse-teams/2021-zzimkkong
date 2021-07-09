package com.woowacourse.zzimkkong.infrastructure;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class JwtUtils {
    @Value("${jwt.token.secret-key}")
    private String secretKey;
    @Value("${jwt.token.expire-length}")
    private long validityInMilliseconds;

    public String createToken(Map<String, Object> payload) {
        Claims claims = Jwts.claims(payload);
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public static PayloadBuilder payloadBuilder() {
        return new PayloadBuilder();
    }

    public static class PayloadBuilder {
        private final Claims claims;

        private PayloadBuilder() {
            this.claims = Jwts.claims();
        }

        public PayloadBuilder setSubject(String subject) {
            claims.setSubject(subject);
            return this;
        }

        public Map<String, Object> build() {
            return claims;
        }
    }
}
