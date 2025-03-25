package com.example.ouathUseFlutter.config.security.filter;

import com.example.ouathUseFlutter.config.security.utill.JwtUtil;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. 요청 헤더에서 Authorization 추출
        String authHeader = request.getHeader("Authorization");
        System.out.println("[JWT] Authorization header: " + authHeader);

        // 2. 헤더가 없거나 형식이 잘못됐으면 다음 필터로 넘김
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("[JWT] Missing or invalid Authorization header");
            filterChain.doFilter(request, response);
            return;
        }

        // 3. "Bearer " 다음의 실제 JWT만 추출
        String token = authHeader.substring(7);
        System.out.println("[JWT] Extracted token: " + token);

        // 4. 토큰 타입 추출 (access / refresh / null)
        String tokenType = getTokenType(token);
        System.out.println("[JWT] Token type: " + tokenType);

        // 5. Kakao Token 또는 알 수 없는 토큰 → 무시하고 필터 통과
        if (tokenType == null) {
            System.out.println("[JWT] Unrecognized or invalid token, skipping authentication");
            filterChain.doFilter(request, response);
            return;
        }

        // 6. Refresh Token은 인증에 사용하지 않음
        if ("refresh".equals(tokenType)) {
            System.out.println("[JWT] Refresh token detected, skipping authentication");
            filterChain.doFilter(request, response);
            return;
        }

        // 7. Access Token 유효성 검사 후 인증 처리
        if ("access".equals(tokenType) && jwtUtil.validateAccessToken(token)) {
            String username = jwtUtil.getUsernameFromAccessToken(token);
            System.out.println("[JWT] Access token valid for user: " + username);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("[Security] Authentication set for user: " + authentication.getName());
        } else {
            System.out.println("[JWT] Invalid access token");
        }

        // 8. 다음 필터로 요청 넘기기
        filterChain.doFilter(request, response);
    }




    // JwtUtil 내부 claim에서 tokenType 추출하는 메서드 구현
    private String getTokenType(String token) {
        try {
            // accessKey로 먼저 시도
            String type = Jwts.parserBuilder()
                    .setSigningKey(jwtUtil.getAccessKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("tokenType", String.class);
            return type;
        } catch (Exception e1) {
            try {
                // refreshKey로 시도 (혹시나 refreshToken일 경우)
                String type = Jwts.parserBuilder()
                        .setSigningKey(jwtUtil.getRefreshKey())
                        .build()
                        .parseClaimsJws(token)
                        .getBody()
                        .get("tokenType", String.class);
                return type;
            } catch (Exception e2) {
                return null; // Kakao 토큰이거나 완전한 invalid 토큰
            }
        }
    }
}
