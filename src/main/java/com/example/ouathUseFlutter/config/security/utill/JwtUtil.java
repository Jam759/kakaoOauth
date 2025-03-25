package com.example.ouathUseFlutter.config.security.utill;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@Getter
public class JwtUtil {

    @Value("${jwt.secret-key}")
    private String secretKey;
    @Value("${jwt.refresh-secret-key}")
    private String refreshSecretKey;
    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;
    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private Key accessKey;
    private Key refreshKey;

    public JwtUtil(){

    }

    @PostConstruct
    public void init(){
        this.accessKey = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.refreshKey = Keys.hmacShaKeyFor(refreshSecretKey.getBytes());
    }

    public String generateAccessToken(String username,Long userId) {
        return Jwts.builder()
                .setSubject(username)
                .claim("tokenType","access")
                .claim("userId",userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(accessKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String username){
        return Jwts.builder()
                .setSubject(username)
                .claim("tokenType","refresh")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(refreshKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromAccessToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(accessKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getUsernameFromRefreshToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(refreshKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Access Token 유효성 검사
    public boolean validateAccessToken(String token) {
        return validateToken(token, "access");
    }

    // Refresh Token 유효성 검사
    public boolean validateRefreshToken(String token) {
        return validateToken(token, "refresh");
    }

    public boolean validateToken(String token,String tokenType) {
        try{
            Key key = "access".equals(tokenType) ? accessKey : refreshKey;
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            //토큰 유형 확인
            String validTokenType = claims.get("tokenType",String.class);



            // ✅ 토큰 타입이 요청한 것과 일치할 경우만 유효
            return tokenType.equals(validTokenType);

        }catch (JwtException | IllegalArgumentException e){
            return false;
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public Date getExpirationFromRefreshToken(String refreshToken){
        return Jwts.parserBuilder()
                .setSigningKey(refreshKey)
                .build()
                .parseClaimsJws(refreshToken)
                .getBody()
                .getExpiration();
    }



}
