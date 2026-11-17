
package com.flipkart.marketsphere.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flipkart.marketsphere.dto.CheckoutRequest;
import com.flipkart.marketsphere.dto.OrderItemResponse;
import com.flipkart.marketsphere.dto.OrderResponse;
import com.flipkart.marketsphere.dto.VendorOrderItemResponse;
import com.flipkart.marketsphere.entity.Cart;
import com.flipkart.marketsphere.entity.CartItem;
import com.flipkart.marketsphere.entity.Order;
import com.flipkart.marketsphere.entity.OrderItem;
import com.flipkart.marketsphere.entity.OrderStatus;
import com.flipkart.marketsphere.entity.Product;
import com.flipkart.marketsphere.entity.ProductStatus;
import com.flipkart.marketsphere.entity.Vendor;
import com.flipkart.marketsphere.exception.CartEmptyException;
import com.flipkart.marketsphere.exception.CartNotFoundException;
import com.flipkart.marketsphere.exception.InvalidOrderStatusException;
import com.flipkart.marketsphere.exception.ProductNotFoundException;
import com.flipkart.marketsphere.repository.CartItemRepository;
import com.flipkart.marketsphere.repository.CartRepository;
import com.flipkart.marketsphere.repository.OrderItemRepository;
import com.flipkart.marketsphere.repository.OrderRepository;
import com.flipkart.marketsphere.repository.ProductRepository;
import com.flipkart.marketsphere.repository.UserRepository;
import com.flipkart.marketsphere.repository.VendorRepository;
import com.flipkart.marketsphere.service.OrderService;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

	private final OrderRepository orderRepository;
	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final ProductRepository productRepository;
	private final OrderItemRepository orderItemRepository;
	private final VendorRepository vendorRepository;
	private final UserRepository userRepository;

	public OrderServiceImpl(OrderRepository orderRepository, CartRepository cartRepository,
			CartItemRepository cartItemRepository, ProductRepository productRepository,
			OrderItemRepository orderItemRepository, VendorRepository vendorRepository, UserRepository userRepository) {

		this.orderRepository = orderRepository;
		this.cartRepository = cartRepository;
		this.cartItemRepository = cartItemRepository;
		this.productRepository = productRepository;
		this.orderItemRepository = orderItemRepository;
		this.vendorRepository = vendorRepository;
		this.userRepository = userRepository;
	}

	// ==================================================
	// SECURITY HELPERS
	// ==================================================

	private String getCurrentUserEmail() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			throw new AccessDeniedException("User is not authenticated");
		}

		return authentication.getName();
	}

	private boolean isAdmin() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null) {
			return false;
		}

		return authentication.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
	}

	private void validateCartOwnership(Cart cart) {

		if (isAdmin()) {
			return;
		}

		if (!cart.getUser().getEmail().equalsIgnoreCase(getCurrentUserEmail())) {

			throw new AccessDeniedException("You cannot place orders using another customer's cart");
		}
	}

	private void validateOrderOwnership(Order order) {

		if (isAdmin()) {
			return;
		}

		if (!order.getUser().getEmail().equalsIgnoreCase(getCurrentUserEmail())) {

			throw new AccessDeniedException("You are not allowed to access another customer's order");
		}
	}

	private void validateVendorOwnership(Vendor vendor) {

		if (isAdmin()) {
			return;
		}

		if (!vendor.getUser().getEmail().equalsIgnoreCase(getCurrentUserEmail())) {

			throw new AccessDeniedException("You are not allowed to access another vendor's orders");
		}
	}

	// ==================================================
	// PLACE ORDER
	// ==================================================

	@Override
	public OrderResponse placeOrder(Long userId, CheckoutRequest request) {

		Cart cart = cartRepository.findByUserId(userId)
				.orElseThrow(() -> new CartNotFoundException("Cart not found for user ID: " + userId));

		validateCartOwnership(cart);

		List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());

		if (cartItems.isEmpty()) {
			throw new CartEmptyException("Cannot place order. Cart is empty.");
		}

		// Validate all products and stock before creating order
		for (CartItem cartItem : cartItems) {

			Product product = productRepository.findById(cartItem.getProduct().getId())
					.orElseThrow(() -> new ProductNotFoundException("Product not found"));

			if (product.getStatus() != ProductStatus.ACTIVE) {
				throw new RuntimeException("Product is not available: " + product.getName());
			}

			if (product.getStockQuantity() < cartItem.getQuantity()) {
				throw new RuntimeException("Insufficient stock for product: " + product.getName());
			}
		}

		BigDecimal totalAmount = cartItems.stream()
				.map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		Order order = Order.builder().orderNumber("MS-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase())
				.user(cart.getUser()).totalAmount(totalAmount).status(OrderStatus.PLACED)
				.shippingAddress(request.getShippingAddress()).orderItems(new ArrayList<>()).build();

		for (CartItem cartItem : cartItems) {

			Product product = productRepository.findById(cartItem.getProduct().getId())
					.orElseThrow(() -> new ProductNotFoundException("Product not found"));

			BigDecimal subtotal = cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));

			OrderItem orderItem = OrderItem.builder().order(order).product(product).vendor(product.getVendor())
					.productName(product.getName()).price(cartItem.getPrice()).quantity(cartItem.getQuantity())
					.subtotal(subtotal).build();

			order.getOrderItems().add(orderItem);

			// Deduct stock
			int remainingStock = product.getStockQuantity() - cartItem.getQuantity();

			product.setStockQuantity(remainingStock);

			if (remainingStock == 0) {
				product.setStatus(ProductStatus.OUT_OF_STOCK);
			}

			productRepository.save(product);
		}

		Order savedOrder = orderRepository.save(order);

		// Clear customer's cart
		cartItemRepository.deleteByCartId(cart.getId());

		return convertToOrderResponse(savedOrder);
	}

	// ==================================================
	// CONVERT ORDER TO RESPONSE
	// ==================================================

	private OrderResponse convertToOrderResponse(Order order) {

		List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
				.map(item -> OrderItemResponse.builder().orderItemId(item.getId()).productId(item.getProduct().getId())
						.productName(item.getProductName()).vendorId(item.getVendor().getId())
						.storeName(item.getVendor().getStoreName()).price(item.getPrice()).quantity(item.getQuantity())
						.subtotal(item.getSubtotal()).build())
				.toList();

		return OrderResponse.builder().orderId(order.getId()).orderNumber(order.getOrderNumber())
				.userId(order.getUser().getId()).totalAmount(order.getTotalAmount()).status(order.getStatus())
				.shippingAddress(order.getShippingAddress()).orderDate(order.getOrderDate()).orderItems(itemResponses)
				.message("Order fetched successfully").build();
	}

	// ==================================================
	// GET ORDERS BY USER ID
	// ==================================================

//	@Override
//	@Transactional(readOnly = true)
//	public List<OrderResponse> getOrdersByUserId(Long userId) {
//
//		List<Order> orders = orderRepository.findByUserId(userId);
//
//		// Validate ownership of every order
//		if (!isAdmin()) {
//			for (Order order : orders) {
//				validateOrderOwnership(order);
//			}
//
//			// Also prevent access to another user ID when no orders exist
//			// by checking the authenticated user's email against orders.
//			if (orders.isEmpty()) {
//				// No records to expose; empty history is safe.
//				// User ID ownership can also be validated through UserRepository.
//			}
//		}
//
//		return orders.stream().map(this::convertToOrderResponse).toList();
//	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderResponse> getOrdersByUserId(Long userId) {

		// Admin can access any customer's order history
		if (!isAdmin()) {

			// First verify that the requested user exists
			var user = userRepository.findById(userId)
					.orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

			// Verify authenticated customer ownership
			if (!user.getEmail().equalsIgnoreCase(getCurrentUserEmail())) {
				throw new AccessDeniedException("You cannot access another customer's order history");
			}
		}

		// Retrieve order history
		List<Order> orders = orderRepository.findByUserId(userId);

		return orders.stream().map(this::convertToOrderResponse).toList();
	}

	// ==================================================
	// GET ORDER BY ID
	// ==================================================

	@Override
	@Transactional(readOnly = true)
	public OrderResponse getOrderById(Long orderId) {

		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

		validateOrderOwnership(order);

		return convertToOrderResponse(order);
	}

	// ==================================================
	// UPDATE ORDER STATUS - ADMIN
	// ==================================================

	@Override
	public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {

		if (!isAdmin()) {
			throw new AccessDeniedException("Only administrators can update order status");
		}

		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

		OrderStatus currentStatus = order.getStatus();

		if (currentStatus == OrderStatus.CANCELLED || currentStatus == OrderStatus.DELIVERED) {

			throw new InvalidOrderStatusException("Cannot update order in status: " + currentStatus);
		}

		boolean validTransition = (currentStatus == OrderStatus.PLACED && newStatus == OrderStatus.CONFIRMED)
				|| (currentStatus == OrderStatus.CONFIRMED && newStatus == OrderStatus.PROCESSING)
				|| (currentStatus == OrderStatus.PROCESSING && newStatus == OrderStatus.SHIPPED)
				|| (currentStatus == OrderStatus.SHIPPED && newStatus == OrderStatus.DELIVERED);

		if (!validTransition) {
			throw new InvalidOrderStatusException(
					"Invalid order status transition: " + currentStatus + " -> " + newStatus);
		}

		order.setStatus(newStatus);

		Order updatedOrder = orderRepository.save(order);

		return convertToOrderResponse(updatedOrder);
	}

	// ==================================================
	// CANCEL ORDER
	// ==================================================

	@Override
	public OrderResponse cancelOrder(Long orderId) {

		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

		validateOrderOwnership(order);

		if (order.getStatus() != OrderStatus.PLACED && order.getStatus() != OrderStatus.CONFIRMED
				&& order.getStatus() != OrderStatus.PROCESSING) {

			throw new InvalidOrderStatusException("Order cannot be cancelled in status: " + order.getStatus());
		}

		// Restore product stock
		for (OrderItem item : order.getOrderItems()) {

			Product product = item.getProduct();

			product.setStockQuantity(product.getStockQuantity() + item.getQuantity());

			product.setStatus(ProductStatus.ACTIVE);

			productRepository.save(product);
		}

		order.setStatus(OrderStatus.CANCELLED);

		Order cancelledOrder = orderRepository.save(order);

		return convertToOrderResponse(cancelledOrder);
	}

	// ==================================================
	// GET VENDOR ORDER ITEMS
	// ==================================================

	@Override
	@Transactional(readOnly = true)
	public List<VendorOrderItemResponse> getVendorOrderItems(Long vendorId) {

		Vendor vendor = vendorRepository.findById(vendorId)
				.orElseThrow(() -> new RuntimeException("Vendor not found with ID: " + vendorId));

		validateVendorOwnership(vendor);

		List<OrderItem> orderItems = orderItemRepository.findByVendorId(vendorId);

		return orderItems.stream().map(item -> VendorOrderItemResponse.builder().orderItemId(item.getId())
				.orderId(item.getOrder().getId()).orderNumber(item.getOrder().getOrderNumber())
				.orderDate(item.getOrder().getOrderDate()).orderStatus(item.getOrder().getStatus())
				.productId(item.getProduct().getId()).productName(item.getProductName()).quantity(item.getQuantity())
				.price(item.getPrice()).subtotal(item.getSubtotal()).customerId(item.getOrder().getUser().getId())
				.shippingAddress(item.getOrder().getShippingAddress()).build()).toList();
	}
}
