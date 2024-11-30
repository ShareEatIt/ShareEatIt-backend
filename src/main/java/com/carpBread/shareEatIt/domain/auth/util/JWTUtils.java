package com.carpBread.shareEatIt.domain.auth.util;

import com.carpBread.shareEatIt.global.exception.AppException;
import com.carpBread.shareEatIt.global.exception.ErrorCode;
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

    @Autowired
    public JWTUtils(@Value("${spring.jwt.secret}") String secretKey){
        byte[] decode = Decoders.BASE64.decode(secretKey);

        key= Keys.hmacShaKeyFor(decode);
    }

    public String createToken(String email, String nickname,long time){
        Claims claims = Jwts.claims();

        claims.put("nickname",nickname);
        claims.put("email",email);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+time))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

    }


    public String getEmail(String token){
        try {

            String email = Jwts.parserBuilder().setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("email", String.class);

            return email;
        }catch (Exception e){
            System.out.println("리프레시 토큰 관련 log");
            System.out.println(e.getMessage());
            throw new AppException(ErrorCode.UNAUTHORIZED_JWT,"유효하지 않은 JWT입니다","/login/oauth2/code/kakao");
        }

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
