# kakaoOauth
플러터 + spring
에서 spring 부분

✅ 전체 인증 흐름 다이어그램 (1. 로그인 시)
[User]    
   │ 1. 로그인 버튼 클릭
   ▼
[Flutter App]
   │ 2. Kakao SDK로 로그인
   ▼
[Kakao SDK]
   │ 3. Kakao 서버에 로그인 요청
   ▼
[Kakao Server]
   │ 4. 카카오 AccessToken 발급
   ▼
[Flutter App]
   │ 5. 카카오 AccessToken 획득
   ▼
[Flutter App]
   │ 6. POST /auth/kakao + AccessToken 전송
   ▼
[Spring Boot (KakaoAuthController)]
   │ 7. Kakao API에 사용자 정보 요청 (/v2/user/me)
   ▼
[Kakao Server]
   │ 8. 사용자 정보 응답 (id, nickname, email 등)
   ▼
[Spring Boot]
   │ 9. 사용자 DB 조회 or 신규 회원가입
   │10. JWT(Access + Refresh) 발급
   ▼
[Spring Boot]
   │11. JWT 응답 (JSON)
   ▼
[Flutter App]
   │12. JWT 저장 (Secure Storage)

   
✅ 이후 API 요청 흐름 (2. 인증된 요청 시)
[Flutter App]
   │ 1. accessToken 헤더에 포함
   ▼
[Spring Boot (JwtAuthenticationFilter)]
   │ 2. JWT 유효성 검사 + 사용자 인증
   ▼
[SecurityContext]
   │ 3. 인증된 사용자 정보 등록
   ▼
[Controller (@AuthenticationPrincipal)]
   │ 4. 인증된 사용자 접근 허용

