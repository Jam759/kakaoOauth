package com.example.ouathUseFlutter.User.repository;

import com.example.ouathUseFlutter.User.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByuserEmail(String email);
}
