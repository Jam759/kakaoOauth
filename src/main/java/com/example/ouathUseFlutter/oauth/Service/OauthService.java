package com.example.ouathUseFlutter.oauth.Service;

import com.example.ouathUseFlutter.User.entity.User;
import com.example.ouathUseFlutter.User.repository.UserRepository;
import com.example.ouathUseFlutter.common.apiClient.KakaoApiClient;
import com.example.ouathUseFlutter.config.security.utill.JwtUtil;
import com.example.ouathUseFlutter.oauth.entity.KakaoUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OauthService {

    private final KakaoApiClient kakaoApiClient;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public Map<String, String> login(String kakaoAccessToken) {
        // 1. Kakao 사용자 정보 요청
        KakaoUserInfo kakaoUser = kakaoApiClient.getUserInfo(kakaoAccessToken);

        //여기 dto -> user

        // 2. 사용자 등록 or 조회
        User user = userRepository.findByuserEmail(u)
                .orElseGet(userRepository.save()); //userService.findOrCreateUser(kakaoUser);

        // 3. JWT 발급
        String accessToken = jwtUtil.generateAccessToken(user.getKakaoId(), user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getKakaoId());

        // 4. RefreshToken 저장
        refreshTokenService.save(user.getId(), refreshToken);

        // 5. 응답 반환
        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        );
    }

}
