
package com.flipkart.marketsphere.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flipkart.marketsphere.dto.ApiResponse;
import com.flipkart.marketsphere.dto.PaymentRequest;
import com.flipkart.marketsphere.dto.PaymentResponse;
import com.flipkart.marketsphere.service.PaymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payments", description = "APIs for payment processing and payment details")
public class PaymentController {

	private final PaymentService paymentService;

	public PaymentController(PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	// Process Payment
	@PostMapping("/process/{orderId}")
	@PreAuthorize("hasRole('CUSTOMER')")
	@Operation(summary = "Process payment", description = "Processes a payment for an order using the selected payment method")
	public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(@PathVariable Long orderId,
			@Valid @RequestBody PaymentRequest request) {

		PaymentResponse response = paymentService.processPayment(orderId, request);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success("Payment processed successfully", response));
	}

	// Get Payment By Order ID
	@GetMapping("/order/{orderId}")
	@PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
	@Operation(summary = "Get payment by order ID", description = "Retrieves payment details associated with a particular order")
	public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByOrderId(@PathVariable Long orderId) {

		PaymentResponse response = paymentService.getPaymentByOrderId(orderId);

		return ResponseEntity.ok(ApiResponse.success("Payment details fetched successfully", response));
	}
}
