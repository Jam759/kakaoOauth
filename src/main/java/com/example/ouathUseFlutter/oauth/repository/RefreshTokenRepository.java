package com.example.ouathUseFlutter.oauth.repository;

import com.example.ouathUseFlutter.oauth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
}
