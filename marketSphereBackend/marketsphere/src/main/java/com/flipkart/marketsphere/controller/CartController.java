
package com.flipkart.marketsphere.controller;

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

import com.flipkart.marketsphere.dto.AddToCartRequest;
import com.flipkart.marketsphere.dto.ApiResponse;
import com.flipkart.marketsphere.dto.CartResponse;
import com.flipkart.marketsphere.dto.UpdateCartItemRequest;
import com.flipkart.marketsphere.service.CartService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cart")
@Tag(name = "Shopping Cart", description = "APIs for managing customer shopping carts")
@PreAuthorize("hasRole('CUSTOMER')")
public class CartController {

	private final CartService cartService;

	public CartController(CartService cartService) {
		this.cartService = cartService;
	}

	// ==================================================
	// GET CUSTOMER CART
	// ==================================================

	@GetMapping("/{userId}")
	@Operation(summary = "Get customer cart", description = "Retrieves the shopping cart and its items using the customer user ID")
	public ResponseEntity<ApiResponse<CartResponse>> getCartByUserId(@PathVariable Long userId) {

		CartResponse response = cartService.getCartByUserId(userId);

		return ResponseEntity.ok(ApiResponse.success("Cart fetched successfully", response));
	}

	// ==================================================
	// ADD PRODUCT TO CART
	// ==================================================

	@PostMapping("/{userId}/items")
	@Operation(summary = "Add product to cart", description = "Adds a product with the specified quantity to the customer's shopping cart")
	public ResponseEntity<ApiResponse<CartResponse>> addProductToCart(@PathVariable Long userId,
			@Valid @RequestBody AddToCartRequest request) {

		CartResponse response = cartService.addProductToCart(userId, request);

		return ResponseEntity.ok(ApiResponse.success("Product added to cart successfully", response));
	}

	// ==================================================
	// UPDATE CART ITEM QUANTITY
	// ==================================================

	@PutMapping("/{userId}/items/{productId}")
	@Operation(summary = "Update cart item quantity", description = "Updates the quantity of a particular product in the customer's cart")
	public ResponseEntity<ApiResponse<CartResponse>> updateCartItemQuantity(@PathVariable Long userId,
			@PathVariable Long productId, @Valid @RequestBody UpdateCartItemRequest request) {

		CartResponse response = cartService.updateCartItemQuantity(userId, productId, request);

		return ResponseEntity.ok(ApiResponse.success("Cart item quantity updated successfully", response));
	}

	// ==================================================
	// REMOVE CART ITEM
	// ==================================================

	@DeleteMapping("/{userId}/items/{productId}")
	@Operation(summary = "Remove product from cart", description = "Removes a specific product from the customer's shopping cart")
	public ResponseEntity<ApiResponse<Void>> removeCartItem(@PathVariable Long userId, @PathVariable Long productId) {

		cartService.removeCartItem(userId, productId);

		return ResponseEntity.ok(ApiResponse.success("Product removed from cart successfully", null));
	}

	// ==================================================
	// CLEAR CART
	// ==================================================

	@DeleteMapping("/{userId}/clear")
	@Operation(summary = "Clear shopping cart", description = "Removes all products from the customer's shopping cart")
	public ResponseEntity<ApiResponse<Void>> clearCart(@PathVariable Long userId) {

		cartService.clearCart(userId);

		return ResponseEntity.ok(ApiResponse.success("Cart cleared successfully", null));
	}
}
