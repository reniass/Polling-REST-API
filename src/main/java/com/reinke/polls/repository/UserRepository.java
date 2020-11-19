package com.reinke.polls.repository;

import com.reinke.polls.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Override
    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<User> findByUsernameOrEmail(String username, String password);

    boolean existsByEmail(String username);

    List<User> findByIdIn(List<Long> users_id);
}
