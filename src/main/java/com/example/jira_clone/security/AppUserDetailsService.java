package com.example.jira_clone.security;

import com.example.jira_clone.entity.User;
import com.example.jira_clone.repository.UserRepository;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public AppUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        boolean active = "ACTIVE".equalsIgnoreCase(user.getStatus());
        List<SimpleGrantedAuthority> authorities = user.getUserRoles().stream()
            .map(userRole -> userRole.getRole().getCode())
            .filter(code -> code != null && !code.isBlank())
            .map(code -> new SimpleGrantedAuthority("ROLE_" + code))
            .distinct()
            .toList();

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                active,
                true,
                true,
            true,
            authorities.isEmpty() ? List.of(new SimpleGrantedAuthority("ROLE_USER")) : authorities);
    }
}