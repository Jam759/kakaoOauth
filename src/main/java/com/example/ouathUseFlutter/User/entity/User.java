package com.example.ouathUseFlutter.User.entity;

import com.example.ouathUseFlutter.User.entity.enums.Oauth;
import com.example.ouathUseFlutter.User.entity.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id; //단순 증가값

    private String email;     // 이메일 (nullable) 폼 로그인 구현시 id로 사용

    private String nickname;  // 닉네임

    @Enumerated(EnumType.STRING)
    private UserRole role;    // 예: ROLE_USER

    @Enumerated(EnumType.STRING)
    private Oauth oauth;    // 예: ROLE_USER


    public static User fromKakaoInfo(String nickname, String email) {
        return User.builder()
                .nickname(nickname)
                .email(email)
                .role(UserRole.ROLE_ADMIN)
                .oauth(Oauth.OAUTH_KAKAO)
                .build();
    }

}
