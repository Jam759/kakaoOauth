package com.example.ouathUseFlutter.oauth.dto;

import com.example.ouathUseFlutter.User.entity.Users;
import com.example.ouathUseFlutter.User.entity.enums.Oauth;
import com.example.ouathUseFlutter.User.entity.enums.UserRole;
import com.example.ouathUseFlutter.oauth.entity.KakaoUserInfo;

public class OauthDtoConverter {

    public static Users kakaoUserInfoToUser(KakaoUserInfo kakaoUserInfo){
        return Users.builder()
                .nickname(kakaoUserInfo.getNickname())
                .email(kakaoUserInfo.getEmail())
                .oauth(Oauth.OAUTH_KAKAO)
                .userRole(UserRole.ROLE_USER)
                .oauthId(kakaoUserInfo.getKakaoId())
                .birthYear(kakaoUserInfo.getBirthYear())
                .birthDay(kakaoUserInfo.getBirthDay())
                .build();
    }

}
