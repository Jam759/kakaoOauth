package com.example.ouathUseFlutter.common.apiClient;

import com.example.ouathUseFlutter.oauth.entity.KakaoUserInfo;
import org.springframework.stereotype.Component;

@Component
public class KakaoApiClient {

    public KakaoUserInfo getUserInfo(String kakaoAccessToken) {
        WebClient webClient = WebClient.create("https://kapi.kakao.com");

        return webClient.get()
                .uri("/v2/user/me")
                .header("Authorization", "Bearer " + kakaoAccessToken)
                .retrieve()
                .bodyToMono(KakaoUserInfo.class)
                .block();
    }
}
