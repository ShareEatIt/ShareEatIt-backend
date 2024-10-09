package com.carpBread.shareEatIt.domain.auth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component

public class JWTUtils {

    private Key key;
    private Key refreshKey;

    @Autowired
    public JWTUtils(@Value("${spring.jwt.secret}") String secretKey,
                    @Value("${spring.jwt.refresh-secret}")String rKey){
        byte[] decode = Decoders.BASE64.decode(secretKey);
        byte[] decode_refresh = Decoders.BASE64.decode(rKey);

        key= Keys.hmacShaKeyFor(decode);
        refreshKey=Keys.hmacShaKeyFor(decode_refresh);
    }

    public String createToken(String email, String nickname){
        Claims claims = Jwts.claims();

        claims.put("nickname",nickname);
        claims.put("email",email);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+60*60*6*1000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

    }

    public String getEmail(String token){
        return Jwts.parserBuilder().setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("email", String.class);

    }

    public boolean isExpired(String token) {
        return Jwts.parserBuilder().setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());

    }
}
