
package com.flipkart.marketsphere.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flipkart.marketsphere.dto.ApiResponse;
import com.flipkart.marketsphere.dto.UserRegistrationRequest;
import com.flipkart.marketsphere.dto.UserResponse;
import com.flipkart.marketsphere.entity.User;
import com.flipkart.marketsphere.mapper.UserMapper;
import com.flipkart.marketsphere.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "APIs for user registration and user management")
public class UserController {

	private final UserService userService;
	private final UserMapper userMapper;

	public UserController(UserService userService, UserMapper userMapper) {

		this.userService = userService;
		this.userMapper = userMapper;
	}

	// Register User
	@PostMapping
	@PreAuthorize("permitAll()")
	@Operation(summary = "Register a new user")
	public ResponseEntity<ApiResponse<UserResponse>> registerUser(@Valid @RequestBody UserRegistrationRequest request) {

		User user = new User();

		user.setName(request.getName());
		user.setEmail(request.getEmail());
		user.setPhone(request.getPhone());
		user.setPassword(request.getPassword());

		User savedUser = userService.registerUser(user);

		UserResponse response = userMapper.toUserResponse(savedUser);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success("User registered successfully", response));
	}

	// Get All Users - ADMIN
	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Get all users")
	public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {

		List<UserResponse> users = userService.getAllUsers().stream().map(userMapper::toUserResponse).toList();

		return ResponseEntity.ok(ApiResponse.success("Users fetched successfully", users));
	}

	// Get User By ID
	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('CUSTOMER','VENDOR','ADMIN')")
	@Operation(summary = "Get user by ID")
	public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {

		User user = userService.getUserById(id);

		UserResponse response = userMapper.toUserResponse(user);

		return ResponseEntity.ok(ApiResponse.success("User fetched successfully", response));
	}

	// Update User
	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('CUSTOMER','VENDOR','ADMIN')")
	@Operation(summary = "Update user")
	public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable Long id, @RequestBody User user) {

		User updatedUser = userService.updateUser(id, user);

		UserResponse response = userMapper.toUserResponse(updatedUser);

		return ResponseEntity.ok(ApiResponse.success("User updated successfully", response));
	}

	// Delete User
	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyRole('CUSTOMER','VENDOR','ADMIN')")
	@Operation(summary = "Delete user")
	public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {

		userService.deleteUser(id);

		return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
	}
}
