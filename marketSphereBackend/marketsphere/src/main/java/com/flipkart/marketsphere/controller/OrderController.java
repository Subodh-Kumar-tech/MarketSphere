
package com.flipkart.marketsphere.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flipkart.marketsphere.dto.ApiResponse;
import com.flipkart.marketsphere.dto.CheckoutRequest;
import com.flipkart.marketsphere.dto.OrderResponse;
import com.flipkart.marketsphere.dto.OrderStatusRequest;
import com.flipkart.marketsphere.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "APIs for checkout, order management and order tracking")
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	// ==================================================
	// PLACE ORDER - CUSTOMER
	// ==================================================

	@PostMapping("/checkout/{userId}")
	@PreAuthorize("hasRole('CUSTOMER')")
	@Operation(summary = "Place order", description = "Creates an order from the customer's cart, deducts stock and clears the cart")
	public ResponseEntity<ApiResponse<OrderResponse>> placeOrder(@PathVariable Long userId,
			@Valid @RequestBody CheckoutRequest request) {

		OrderResponse response = orderService.placeOrder(userId, request);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success("Order placed successfully", response));
	}

	// ==================================================
	// GET ORDERS BY USER - CUSTOMER / ADMIN
	// ==================================================

	@GetMapping("/user/{userId}")
	@PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
	@Operation(summary = "Get customer order history", description = "Retrieves all orders placed by a particular customer")
	public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrdersByUserId(@PathVariable Long userId) {

		List<OrderResponse> orders = orderService.getOrdersByUserId(userId);

		return ResponseEntity.ok(ApiResponse.success("Order history fetched successfully", orders));
	}

	// ==================================================
	// GET ORDER BY ID - CUSTOMER / ADMIN
	// ==================================================

	@GetMapping("/{orderId}")
	@PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
	@Operation(summary = "Get order by ID", description = "Retrieves detailed information about a specific order")
	public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(@PathVariable Long orderId) {

		OrderResponse response = orderService.getOrderById(orderId);

		return ResponseEntity.ok(ApiResponse.success("Order fetched successfully", response));
	}

	// ==================================================
	// UPDATE ORDER STATUS - ADMIN
	// ==================================================

	@PutMapping("/{orderId}/status")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Update order status", description = "Updates the status of an order according to the allowed order lifecycle")
	public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(@PathVariable Long orderId,
			@Valid @RequestBody OrderStatusRequest request) {

		OrderResponse response = orderService.updateOrderStatus(orderId, request.getStatus());

		return ResponseEntity.ok(ApiResponse.success("Order status updated successfully", response));
	}

	// ==================================================
	// CANCEL ORDER - CUSTOMER / ADMIN
	// ==================================================

	@PutMapping("/{orderId}/cancel")
	@PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
	@Operation(summary = "Cancel order", description = "Cancels an eligible order and restores product stock")
	public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(@PathVariable Long orderId) {

		OrderResponse response = orderService.cancelOrder(orderId);

		return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully", response));
	}
}
