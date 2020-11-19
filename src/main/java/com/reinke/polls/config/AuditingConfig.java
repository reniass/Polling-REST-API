package com.reinke.polls.config;

import com.reinke.polls.model.User;
import com.reinke.polls.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class AuditingConfig {

    @Bean
    public AuditorAware auditorAware() {
        return new SpringSecurityAuditAwareImpl();
    }
}


class SpringSecurityAuditAwareImpl implements AuditorAware {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {

            return Optional.empty();
        }

//        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
//
//        return Optional.ofNullable(userPrincipal.getId());
        String username = (String) authentication.getPrincipal();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found with username:" + username));

        return Optional.ofNullable(user.getId());
    }
}