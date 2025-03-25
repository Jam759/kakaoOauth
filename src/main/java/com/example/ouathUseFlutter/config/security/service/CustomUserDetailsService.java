package com.example.ouathUseFlutter.config.security.service;


import com.example.ouathUseFlutter.User.entity.User;
import com.example.ouathUseFlutter.User.repository.UserRepository;
import com.example.ouathUseFlutter.oauth.entity.UserPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // 의존성 주입 (예시로 JPA 사용)
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user =userRepository.findByuserEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("user not found : " + email));


        return new UserPrincipal(user);
    }
}
