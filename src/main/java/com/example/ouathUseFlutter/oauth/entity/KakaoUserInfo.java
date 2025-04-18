package com.example.ouathUseFlutter.oauth.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoUserInfo {

    private Long id; // 카카오 고유 ID
    private KakaoAccount kakao_account;

    @Getter
    @Setter
    public static class KakaoAccount {

        private String email;
        private String birthyear; // 생년
        private String birthday;  // 월일
        private Profile profile;

        @Getter
        @Setter
        public static class Profile {
            private String nickname;
        }
    }

    // 편의 메서드들
    public String getKakaoId() {
        return id != null ? String.valueOf(id) : null;
    }

    public String getNickname() {
        if (kakao_account != null && kakao_account.getProfile() != null) {
            return kakao_account.getProfile().getNickname();
        }
        return null;
    }

    public String getEmail() {
        return kakao_account != null ? kakao_account.getEmail() : null;
    }

    public String getBirthYear() { return kakao_account.getBirthyear(); }

    public String getBirthDay() { return kakao_account.getBirthday(); }
}

