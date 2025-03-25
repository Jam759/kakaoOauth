package com.example.ouathUseFlutter.User.entity.enums;

public enum UserRole {
    ROLE_USER,
    ROLE_ADMIN;

    public String getRoleName() {
        return this.name();
    }
}
