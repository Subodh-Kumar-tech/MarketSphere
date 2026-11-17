
package com.flipkart.marketsphere.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flipkart.marketsphere.dto.AddToCartRequest;
import com.flipkart.marketsphere.dto.CartItemResponse;
import com.flipkart.marketsphere.dto.CartResponse;
import com.flipkart.marketsphere.dto.UpdateCartItemRequest;
import com.flipkart.marketsphere.entity.Cart;
import com.flipkart.marketsphere.entity.CartItem;
import com.flipkart.marketsphere.entity.Product;
import com.flipkart.marketsphere.entity.ProductStatus;
import com.flipkart.marketsphere.exception.CartNotFoundException;
import com.flipkart.marketsphere.exception.InsufficientStockException;
import com.flipkart.marketsphere.exception.ProductNotFoundException;
import com.flipkart.marketsphere.repository.CartItemRepository;
import com.flipkart.marketsphere.repository.CartRepository;
import com.flipkart.marketsphere.repository.ProductRepository;
import com.flipkart.marketsphere.service.CartService;

@Service
@Transactional
public class CartServiceImpl implements CartService {

	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final ProductRepository productRepository;

	public CartServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository,
			ProductRepository productRepository) {

		this.cartRepository = cartRepository;
		this.cartItemRepository = cartItemRepository;
		this.productRepository = productRepository;
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

	// Validate that the cart belongs to the authenticated customer
	private void validateCartOwnership(Cart cart) {

		String authenticatedEmail = getCurrentUserEmail();

		String cartOwnerEmail = cart.getUser().getEmail();

		if (!cartOwnerEmail.equalsIgnoreCase(authenticatedEmail)) {
			throw new AccessDeniedException("You are not allowed to access another customer's cart");
		}
	}

	// ==================================================
	// GET CART BY USER ID
	// ==================================================

	@Override
	@Transactional(readOnly = true)
	public CartResponse getCartByUserId(Long userId) {

		Cart cart = cartRepository.findByUserId(userId)
				.orElseThrow(() -> new CartNotFoundException("Cart not found for user ID: " + userId));

		// Security: verify cart ownership
		validateCartOwnership(cart);

		List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());

		List<CartItemResponse> itemResponses = cartItems.stream().map(this::convertToCartItemResponse).toList();

		int totalItems = cartItems.stream().mapToInt(CartItem::getQuantity).sum();

		BigDecimal totalPrice = cartItems.stream()
				.map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		return CartResponse.builder().cartId(cart.getId()).userId(cart.getUser().getId()).items(itemResponses)
				.totalItems(totalItems).totalPrice(totalPrice).message("Cart fetched successfully").build();
	}

	// ==================================================
	// ADD PRODUCT TO CART
	// ==================================================

	@Override
	public CartResponse addProductToCart(Long userId, AddToCartRequest request) {

		Cart cart = cartRepository.findByUserId(userId)
				.orElseThrow(() -> new CartNotFoundException("Cart not found for user ID: " + userId));

		// Security: verify cart ownership
		validateCartOwnership(cart);

		Product product = productRepository.findById(request.getProductId()).orElseThrow(
				() -> new ProductNotFoundException("Product not found with ID: " + request.getProductId()));

		if (product.getStatus() != ProductStatus.ACTIVE) {
			throw new InsufficientStockException("Product is not available for purchase");
		}

		CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId()).orElse(null);

		int existingQuantity = cartItem == null ? 0 : cartItem.getQuantity();

		int newQuantity = existingQuantity + request.getQuantity();

		if (newQuantity > product.getStockQuantity()) {
			throw new InsufficientStockException(
					"Insufficient stock. Available quantity: " + product.getStockQuantity());
		}

		if (cartItem == null) {

			cartItem = CartItem.builder().cart(cart).product(product).quantity(request.getQuantity())
					.price(product.getPrice()).build();

		} else {
			cartItem.setQuantity(newQuantity);
		}

		cartItemRepository.save(cartItem);

		return getCartByUserId(userId);
	}

	// ==================================================
	// CONVERT CART ITEM TO RESPONSE
	// ==================================================

	private CartItemResponse convertToCartItemResponse(CartItem cartItem) {

		BigDecimal subtotal = cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));

		return CartItemResponse.builder().itemId(cartItem.getId()).productId(cartItem.getProduct().getId())
				.productName(cartItem.getProduct().getName()).imageUrl(cartItem.getProduct().getImageUrl())
				.quantity(cartItem.getQuantity()).unitPrice(cartItem.getPrice()).subtotal(subtotal).build();
	}

	// ==================================================
	// UPDATE CART ITEM QUANTITY
	// ==================================================

	@Override
	public CartResponse updateCartItemQuantity(Long userId, Long productId, UpdateCartItemRequest request) {

		Cart cart = cartRepository.findByUserId(userId)
				.orElseThrow(() -> new CartNotFoundException("Cart not found for user ID: " + userId));

		// Security: verify cart ownership
		validateCartOwnership(cart);

		CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
				.orElseThrow(() -> new CartNotFoundException("Product not found in cart: " + productId));

		Product product = cartItem.getProduct();

		if (product.getStatus() != ProductStatus.ACTIVE) {
			throw new InsufficientStockException("Product is not available for purchase");
		}

		if (request.getQuantity() > product.getStockQuantity()) {
			throw new InsufficientStockException(
					"Insufficient stock. Available quantity: " + product.getStockQuantity());
		}

		cartItem.setQuantity(request.getQuantity());

		cartItemRepository.save(cartItem);

		return getCartByUserId(userId);
	}

	// ==================================================
	// REMOVE CART ITEM
	// ==================================================

	@Override
	public void removeCartItem(Long userId, Long productId) {

		Cart cart = cartRepository.findByUserId(userId)
				.orElseThrow(() -> new CartNotFoundException("Cart not found for user ID: " + userId));

		// Security: verify cart ownership
		validateCartOwnership(cart);

		CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
				.orElseThrow(() -> new CartNotFoundException("Product not found in cart: " + productId));

		cartItemRepository.delete(cartItem);
	}

	// ==================================================
	// CLEAR CART
	// ==================================================

	@Override
	public void clearCart(Long userId) {

		Cart cart = cartRepository.findByUserId(userId)
				.orElseThrow(() -> new CartNotFoundException("Cart not found for user ID: " + userId));

		// Security: verify cart ownership
		validateCartOwnership(cart);

		cartItemRepository.deleteByCartId(cart.getId());
	}
}
