
package com.flipkart.marketsphere.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flipkart.marketsphere.dto.AdminUserResponse;
import com.flipkart.marketsphere.dto.ApiResponse;
import com.flipkart.marketsphere.repository.UserRepository;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

	private final UserRepository userRepository;

	public AdminUserController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<AdminUserResponse>>> getAllUsers() {

		List<AdminUserResponse> users = userRepository.findAll().stream()
				.map(user -> new AdminUserResponse(user.getId(), user.getName(), user.getEmail(),
						String.valueOf(user.getRole())))
				.toList();

		return ResponseEntity.ok(ApiResponse.success("Users fetched successfully", users));
	}
}
