/*
package com.example.ouathUseFlutter.config.security.filter;

\
import com.example.ouathUseFlutter.config.security.utill.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.time.Duration;

public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public JwtLoginFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil){
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException{
        try{
            LoginRequest loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(), loginRequest.getPassword()
            );
            return authenticationManager.authenticate(authToken);
        }catch (IOException e){
            throw new RuntimeException("Invalid login request", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult){
        // UserDetails 타입으로 다운캐스팅
        User user = (User) authResult.getPrincipal();

        Long userId = user.getId();
        String username = user.getUsername();
        String accessToken = jwtUtil.generateAccessToken(username,userId);
        String refreshToken = jwtUtil.generateRefreshToken(username);

        // AccessToken: 헤더에 추가
        response.setHeader("Authorization", "Bearer " + accessToken);

        // RefreshToken: HttpOnly 쿠키로 추가
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true) // JS에서 접근 불가 (보안)
                .secure(false) // HTTPS에서만 동작 (개발 중엔 false도 가능)
                .path("/") // 모든 경로에서 쿠키 사용 가능
                .maxAge(Duration.ofDays(14)) // 2주
                .sameSite("Strict") // CSRF 방지
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

//        Map<String, String> tokenMap = new HashMap<>();
//        tokenMap.put("accessToken", accessToken);
//        tokenMap.put("refreshToken", refreshToken);
//
//
//
//        response.setContentType("application/json");
//        response.getWriter().write(new ObjectMapper().writeValueAsString(tokenMap));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"message\": \"로그인 실패: " + failed.getMessage() + "\"}");
    }


}
*/
