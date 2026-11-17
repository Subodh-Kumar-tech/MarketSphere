
package com.flipkart.marketsphere.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flipkart.marketsphere.dto.ApiResponse;
import com.flipkart.marketsphere.dto.VendorOrderItemResponse;
import com.flipkart.marketsphere.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/vendors")
@Tag(name = "Vendor Orders", description = "APIs for vendors to manage and view their orders")
public class VendorOrderController {

	private final OrderService orderService;

	public VendorOrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@GetMapping("/{vendorId}/orders")
	@Operation(summary = "Get vendor orders", description = "Retrieve all order items associated with a specific vendor")
	public ResponseEntity<ApiResponse<List<VendorOrderItemResponse>>> getVendorOrders(@PathVariable Long vendorId) {

		List<VendorOrderItemResponse> response = orderService.getVendorOrderItems(vendorId);

		return ResponseEntity.ok(ApiResponse.success("Vendor orders fetched successfully", response));
	}
}
