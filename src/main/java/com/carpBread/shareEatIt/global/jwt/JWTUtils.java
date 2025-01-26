package com.carpBread.shareEatIt.global.jwt;

import com.carpBread.shareEatIt.domain.auth.LoginProvider;
import com.carpBread.shareEatIt.global.exception.CustomException;
import com.carpBread.shareEatIt.global.exception.CustomExceptionStatus;
import com.carpBread.shareEatIt.global.exception.Domain;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

/* JWT 생성, 검증, payload 추출 등의 작업 수행 */
@Component
public class JWTUtils {

    @Value("${spring.jwt.issuer}")
    private String issuer;

    @Value("${spring.jwt.audience}")
    private String audience;

    // accessToken 토큰 만료 시간 - 2h
    private final long accessTokenExpiredTime=1000*60*60*2l;

    // refreshToken 만료 시간 - 30d
    private final long refreshTokenExpiredTime=1000*60*60*24*30;

    private Key key;

    @Autowired
    public JWTUtils(@Value("${spring.jwt.secret}") String secretKey){
        byte[] decode = Decoders.BASE64.decode(secretKey);

        key= Keys.hmacShaKeyFor(decode);
    }

    public String createToken(String email, String nickname){
        Claims claims = Jwts.claims();

        claims.put("nickname",nickname);
        claims.put("email",email);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*60*12L))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

    }

    /* ACCESS TOKEN 만들기 */
    public String createAccessToken(String sub, LoginProvider provider){
        // 현재 시간
        long currentTime = System.currentTimeMillis();

        // payload 만들기
        Claims claims = Jwts.claims();

        // 토큰 발급자 issuer
        claims.put("iss", issuer);
        // 토큰 대상자 audience
        claims.put("aud", audience);
        // 토큰 대상자 식별자 subject
        claims.put("sub",sub);
        // 식별자 종류
        claims.put("provider",provider.name());
        // 토큰 만료 시간 expired datetime
        claims.put("exp", new Date(currentTime+accessTokenExpiredTime));
        // 토큰 발급 시간 issued at
        claims.put("iat", new Date(currentTime));
        // jwt 고유 식별자(redis에서 사용) jwt identifier
        claims.put("jti", generateJti());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(currentTime+accessTokenExpiredTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /* REFRESH TOKEN 만들기 */
    public String createRefreshToken(String sub, LoginProvider provider){
        // 현재 시간
        long currentTime = System.currentTimeMillis();

        // payload 만들기
        Claims claims = Jwts.claims();
        // 토큰 대상자 식별자 subject
        claims.put("sub",sub);
        // 식별자 종류
        claims.put("provider",provider.name());
        // jwt 고유 식별자(redis에서 사용) jwt identifier
        claims.put("jti", generateJti());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(currentTime))
                .setExpiration(new Date(currentTime+refreshTokenExpiredTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /* 사용자가 로그인한 방식 PROVIDER 추출 */
    public String getProvider(String token){
        try {

            String type = Jwts.parserBuilder().setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("provider", String.class);

            return type;
        }catch (Exception e){
            throw new CustomException(CustomExceptionStatus.UNAUTHORIZED_JWT,
                    "유효하지 않은 JWT입니다",
                    JWTUtils.class.getName(),
                    null,
                    Domain.AUTH);
        }
    }

    /* 사용자 고유 SUB 추출 */
    public String getSub(String token){
        try {

            String sub = Jwts.parserBuilder().setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("sub", String.class);

            return sub;
        }catch (Exception e){
            throw new CustomException(CustomExceptionStatus.UNAUTHORIZED_JWT,
                    "유효하지 않은 JWT입니다",
                    JWTUtils.class.getName(),
                    null,
                    Domain.AUTH);
        }
    }

    /* JWT의 JTI 추출 */
    public String getJti(String token){
        // jwt parser
        JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(key)
                .build();

        return parser
                .parseClaimsJws(token)
                .getBody()
                .get("jti", String.class);

    }

    /* JWT의 유효기간 만료 여부  */
    public boolean isExpired(String token) throws Exception{
        boolean isExpiredToken;
            isExpiredToken = Jwts.parserBuilder().setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration()
                    .before(new Date());

        return isExpiredToken;

    }

    /* JWT 고유 ID */
    private String generateJti(){
        return UUID.randomUUID().toString();
    }
}
