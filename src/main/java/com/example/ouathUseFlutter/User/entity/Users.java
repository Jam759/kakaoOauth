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
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //단순 증가값

    @Column( unique = true )
    private String email;     // 이메일 (nullable) 폼 로그인 구현시 id로 사용

    private String nickname;  // 닉네임

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private UserRole role;    // 예: ROLE_USER

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Oauth oauth;    // 예: OAUTH_KAKAO

    private String oauthId;

    private String birthYear; // 생년

    private String birthDay;  // 월일


    @Builder
    public Users(String nickname, String email, UserRole userRole, Oauth oauth, String oauthId, String birthYear, String birthDay ) {
        this.nickname = nickname;
        this.email = email;
        this.role = userRole;
        this.oauth = oauth;
        this.oauthId = oauthId;
        this.birthYear = birthYear;
        this.birthDay = birthDay;
    }

}
