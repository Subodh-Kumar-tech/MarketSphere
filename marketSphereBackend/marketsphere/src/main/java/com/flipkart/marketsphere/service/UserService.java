package com.flipkart.marketsphere.service;

import java.util.List;

import com.flipkart.marketsphere.dto.LoginRequest;
import com.flipkart.marketsphere.dto.LoginResponse;
import com.flipkart.marketsphere.entity.User;

public interface UserService {

	User registerUser(User user);

	List<User> getAllUsers();

	User getUserById(Long id);

	User updateUser(Long id, User user);

	void deleteUser(Long id);

	LoginResponse loginUser(LoginRequest request);
}
