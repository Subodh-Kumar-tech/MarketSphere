
package com.flipkart.marketsphere.service.impl;

import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flipkart.marketsphere.dto.PaymentRequest;
import com.flipkart.marketsphere.dto.PaymentResponse;
import com.flipkart.marketsphere.entity.Order;
import com.flipkart.marketsphere.entity.OrderStatus;
import com.flipkart.marketsphere.entity.Payment;
import com.flipkart.marketsphere.entity.PaymentStatus;
import com.flipkart.marketsphere.exception.InvalidOrderStatusException;
import com.flipkart.marketsphere.repository.OrderRepository;
import com.flipkart.marketsphere.repository.PaymentRepository;
import com.flipkart.marketsphere.service.PaymentService;

@Service
public class PaymentServiceImpl implements PaymentService {

	private final PaymentRepository paymentRepository;
	private final OrderRepository orderRepository;

	public PaymentServiceImpl(PaymentRepository paymentRepository, OrderRepository orderRepository) {

		this.paymentRepository = paymentRepository;
		this.orderRepository = orderRepository;
	}

	// Current authenticated user's email
	private String getCurrentUserEmail() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			throw new AccessDeniedException("User is not authenticated");
		}

		return authentication.getName();
	}

	// Check whether current user is ADMIN
	private boolean isAdmin() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		return authentication != null && authentication.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
	}

	// Validate order ownership
	private void validateOrderOwnership(Order order) {

		if (isAdmin()) {
			return;
		}

		String currentUserEmail = getCurrentUserEmail();

		if (!order.getUser().getEmail().equalsIgnoreCase(currentUserEmail)) {
			throw new AccessDeniedException("You cannot access payment of another customer's order");
		}
	}

	@Override
	@Transactional
	public PaymentResponse processPayment(Long orderId, PaymentRequest request) {

		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

		// Customer can pay only for their own order
		validateOrderOwnership(order);

		// Payment cannot be processed for cancelled orders
		if (order.getStatus() == OrderStatus.CANCELLED) {
			throw new InvalidOrderStatusException("Payment cannot be processed for cancelled order");
		}

		// Prevent duplicate payment records
		if (paymentRepository.existsByOrderId(orderId)) {
			throw new InvalidOrderStatusException("Payment record already exists for this order");
		}

		// Simulated payment success
		Payment payment = Payment.builder()
				.transactionId("TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase()).order(order)
				.amount(order.getTotalAmount()).status(PaymentStatus.SUCCESS).paymentMethod(request.getPaymentMethod())
				.build();

		Payment savedPayment = paymentRepository.save(payment);

		return convertToPaymentResponse(savedPayment);
	}

	@Override
	@Transactional(readOnly = true)
	public PaymentResponse getPaymentByOrderId(Long orderId) {

		// First verify that order exists
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

		// Customer can view only their own payment
		validateOrderOwnership(order);

		Payment payment = paymentRepository.findByOrderId(orderId)
				.orElseThrow(() -> new RuntimeException("Payment not found for order ID: " + orderId));

		return convertToPaymentResponse(payment);
	}

	private PaymentResponse convertToPaymentResponse(Payment payment) {

		return PaymentResponse.builder().paymentId(payment.getId()).transactionId(payment.getTransactionId())
				.orderId(payment.getOrder().getId()).orderNumber(payment.getOrder().getOrderNumber())
				.amount(payment.getAmount()).status(payment.getStatus()).paymentMethod(payment.getPaymentMethod())
				.paymentDate(payment.getPaymentDate()).message("Payment " + payment.getStatus()).build();
	}
}
