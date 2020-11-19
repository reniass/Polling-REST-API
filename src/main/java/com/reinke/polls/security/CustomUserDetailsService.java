package com.reinke.polls.security;

import com.reinke.polls.model.User;
import com.reinke.polls.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail).
                orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " +usernameOrEmail));

        return UserPrincipal.create(user);
    }

    public UserPrincipal loadUserById(Long id) {
        User user = userRepository.findById(id).
                orElseThrow(() -> new UsernameNotFoundException("User not found with id=" + id));

        return UserPrincipal.create(user);
    }
}
