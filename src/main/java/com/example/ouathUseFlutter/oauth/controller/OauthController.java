package com.example.ouathUseFlutter.oauth.controller;

import com.example.ouathUseFlutter.oauth.Service.OauthService;
import com.example.ouathUseFlutter.oauth.dto.request.KakaoLoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class OauthController {

    private final OauthService oauthService;

    @PostMapping("/kakao/first")
    public ResponseEntity<?> kakaoLogin(@RequestBody KakaoLoginRequest request) {
        return ResponseEntity.ok(oauthService.kakaoLogin(request.getAccessToken()));
    }

    @PostMapping("/kakao/access")
    public ResponseEntity<?> reissue(@RequestHeader("Authorization") String bearerToken) {
        String refreshToken = bearerToken.replace("Bearer ", "");
        return ResponseEntity.ok(oauthService.reissue(refreshToken));
    }

}
