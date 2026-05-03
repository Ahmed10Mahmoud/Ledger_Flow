package com.ahmed.demo.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<UserEntity,Long> {
    Optional<User> findByEmail(String email);
}
