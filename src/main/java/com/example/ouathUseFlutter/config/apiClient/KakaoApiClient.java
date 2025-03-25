package com.example.ouathUseFlutter.config.apiClient;

import com.example.ouathUseFlutter.oauth.entity.KakaoUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
public class KakaoApiClient {

    private final WebClient kakaoWebClient;

    public KakaoUserInfo getUserInfo(String kakaoAccessToken) {
        return kakaoWebClient.get()
                .uri("/v2/user/me")
                .header("Authorization", "Bearer " + kakaoAccessToken)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                response ->  Mono.error(new RuntimeException("Kakao AccessToken이 유효하지 않습니다.")))
                .onStatus(HttpStatusCode::is5xxServerError,
                        clientResponse -> Mono.error(new RuntimeException("🚨 Kakao 서버 오류입니다.")))
                .bodyToMono(KakaoUserInfo.class)
                .block(); // 동기 처리 (로그인 시점에만 사용되므로 OK)
    }
}
