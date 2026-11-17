
package com.flipkart.marketsphere.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flipkart.marketsphere.dto.AdminDashboardResponse;
import com.flipkart.marketsphere.dto.ApiResponse;
import com.flipkart.marketsphere.service.AdminDashboardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/admin/dashboard")
@Tag(name = "Admin Dashboard", description = "APIs for administrator dashboard statistics")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

	private final AdminDashboardService adminDashboardService;

	public AdminDashboardController(AdminDashboardService adminDashboardService) {
		this.adminDashboardService = adminDashboardService;
	}

	@GetMapping("/statistics")
	@Operation(summary = "Get dashboard statistics", description = "Retrieves total users, vendors, pending vendors, products, orders, successful payments and total revenue")
	public ResponseEntity<ApiResponse<AdminDashboardResponse>> getStatistics() {

		AdminDashboardResponse response = adminDashboardService.getDashboardStatistics();

		return ResponseEntity.ok(ApiResponse.success("Dashboard statistics fetched successfully", response));
	}
}
