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

        // 2. 헤더가 없거나 형식이 잘못됐으면 인증 실패 처리
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("[JWT] Missing or invalid Authorization header");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Missing or invalid Authorization header\"}");
            return;
        }

        // 3. "Bearer " 다음의 실제 JWT만 추출
        String token = authHeader.substring(7);
        System.out.println("[JWT] Extracted token: " + token);

        // 4. 토큰 타입 추출 (access / refresh / null)
        String tokenType = getTokenType(token);
        System.out.println("[JWT] Token type: " + tokenType);

        // 5. Access Token이 아닌 경우 → 인증 실패 처리
        if (!"access".equals(tokenType)) {
            System.out.println("[JWT] Non-access token detected (refresh or unknown)");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Only access tokens are allowed for authentication\"}");
            return;
        }

        // 6. Access Token 유효성 검사
        if (jwtUtil.validateAccessToken(token)) {
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

            // ✅ 인증 성공 시 필터 체인 계속 진행
            filterChain.doFilter(request, response);
        } else {
            // ❌ Access 토큰이지만 유효하지 않은 경우
            System.out.println("[JWT] Invalid access token - returning 401");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid or expired access token\"}");
        }
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
