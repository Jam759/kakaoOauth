package com.example.ouathUseFlutter.oauth.controller;

import com.example.ouathUseFlutter.oauth.Service.OauthService;
import com.example.ouathUseFlutter.oauth.dto.request.KakaoLoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class OauthController {

    private final OauthService oauthService;

    @PostMapping("/kakao")
    public ResponseEntity<?> kakaoLogin(@RequestBody KakaoLoginRequest request) {
        return ResponseEntity.ok(oauthService.login(request.getAccessToken()));
    }
}
