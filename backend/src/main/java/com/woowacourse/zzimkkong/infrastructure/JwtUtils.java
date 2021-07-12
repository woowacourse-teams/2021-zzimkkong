package com.woowacourse.zzimkkong.infrastructure;

import com.woowacourse.zzimkkong.exception.InvalidTokenException;
import com.woowacourse.zzimkkong.exception.TokenExpiredException;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
public class JwtUtils {
    private final String secretKey;
    private final long validityInMilliseconds;
    private final JwtParser jwtParser;

    public JwtUtils(@Value("${jwt.token.secret-key}") String secretKey,
                    @Value("${jwt.token.expire-length}") long validityInMilliseconds) {
        this.secretKey = secretKey;
        this.validityInMilliseconds = validityInMilliseconds;
        this.jwtParser = Jwts.parser().setSigningKey(secretKey);
    }

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

    public void validateToken(String token) {
        try {
            Date expirationDate = jwtParser.parseClaimsJws(token).getBody().getExpiration();
            validateExpiration(expirationDate);
        } catch (JwtException e) {
            throw new InvalidTokenException();
        }
    }

    private void validateExpiration(Date expirationDate) {
        Date now = new Date();
        boolean isExpired = expirationDate.before(now);

        if (isExpired) {
            throw new TokenExpiredException();
        }
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
