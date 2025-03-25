package com.example.ouathUseFlutter.oauth.Service;

import com.example.ouathUseFlutter.User.entity.Users;
import com.example.ouathUseFlutter.User.repository.UserRepository;
import com.example.ouathUseFlutter.config.apiClient.KakaoApiClient;
import com.example.ouathUseFlutter.oauth.entity.RefreshToken;
import com.example.ouathUseFlutter.oauth.repository.RefreshTokenRepository;
import com.example.ouathUseFlutter.config.security.utill.JwtUtil;
import com.example.ouathUseFlutter.oauth.dto.OauthDtoConverter;
import com.example.ouathUseFlutter.oauth.entity.KakaoUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OauthService {

    private final KakaoApiClient kakaoApiClient;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    public Map<String, String> kakaoLogin(String kakaoAccessToken) {

        // 1. Kakao 사용자 정보 요청
        System.out.println("[OAuth] Received Kakao access token: " + kakaoAccessToken);
        KakaoUserInfo kakaoUser = kakaoApiClient.getUserInfo(kakaoAccessToken);
        System.out.println("[OAuth] Kakao user info received: " + kakaoUser);

        // dto -> user 객체 변환
        Users user = OauthDtoConverter.kakaoUserInfoToUser(kakaoUser);
        System.out.println("[OAuth] Converted to User entity: " + user);

        // 2. 사용자 등록 or 조회
        Users ishasUser = userRepository.findByEmail(user.getEmail())
                .orElseGet(() -> {
                    Users savedUser = userRepository.save(user);
                    System.out.println("[OAuth] New user registered: " + savedUser);
                    return savedUser;
                });

        System.out.println("[OAuth] Final user ID: " + ishasUser.getId());

        // 3. JWT 발급
        String accessToken = jwtUtil.generateAccessToken(ishasUser.getEmail(), ishasUser.getId());
        String refreshToken = jwtUtil.generateRefreshToken(ishasUser.getEmail());

        System.out.println("[JWT] Access token generated: " + accessToken);
        System.out.println("[JWT] Refresh token generated: " + refreshToken);

        // 4. RefreshToken 저장
        RefreshToken existingToken = refreshTokenRepository.findById(ishasUser.getId())
                .orElseGet(() -> {
                    System.out.println("[Token] No existing refresh token found. Creating new.");
                    return new RefreshToken(ishasUser.getId(), refreshToken);
                });

        refreshTokenRepository.save(existingToken.updateToken(refreshToken));
        System.out.println("[Token] Refresh token saved: " + existingToken.getRefreshToken());

        // 5. 응답 반환
        return Map.of(
                "accessToken", accessToken,
                "refreshToken", refreshToken
        );
    }




    public Map<String, String> reissue(String refreshToken) {
        // 1. 유효한 RefreshToken인지 확인
        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 리프레시 토큰");
        }

        // 2. RefreshToken에서 사용자 정보 추출
        String username = jwtUtil.getUsernameFromRefreshToken(refreshToken);
        Users user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("해당 유저 없음"));

        // 3. DB에 저장된 RefreshToken과 일치 여부 확인
        RefreshToken saved = refreshTokenRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("저장된 RefreshToken 없음"));

        if (!saved.getRefreshToken().equals(refreshToken)) {
            throw new RuntimeException("RefreshToken이 일치하지 않음");
        }

        // 4. AccessToken 발급
        String newAccessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getId());

        // 5. RefreshToken 남은 기간 확인
        Date expiration = jwtUtil.getExpirationFromRefreshToken(refreshToken);
        long remaining = expiration.getTime() - System.currentTimeMillis();

        String newRefreshToken = refreshToken;

        // 남은 시간 3일 이하 → RefreshToken도 갱신
        if (remaining < TimeUnit.DAYS.toMillis(3)) {
            newRefreshToken = jwtUtil.generateRefreshToken(user.getEmail());
            saved = saved.updateToken(newRefreshToken);
            refreshTokenRepository.save(saved);
        }

        return Map.of(
                "accessToken", newAccessToken,
                "refreshToken", newRefreshToken
        );
    }


}
