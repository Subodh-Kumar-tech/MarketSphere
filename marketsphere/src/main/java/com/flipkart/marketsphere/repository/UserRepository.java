package com.flipkart.marketsphere.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flipkart.marketsphere.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	boolean existsByEmail(String email);

	Optional<User> findById(Long id);

	Optional<User> findByEmail(String email);

	long count();

}
