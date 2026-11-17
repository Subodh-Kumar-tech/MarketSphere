package com.flipkart.marketsphere.service;

import com.flipkart.marketsphere.dto.AddToCartRequest;
import com.flipkart.marketsphere.dto.CartResponse;
import com.flipkart.marketsphere.dto.UpdateCartItemRequest;

public interface CartService {

	CartResponse getCartByUserId(Long userId);

	CartResponse addProductToCart(Long userId, AddToCartRequest request);

	CartResponse updateCartItemQuantity(Long userId, Long productId, UpdateCartItemRequest request);

	void removeCartItem(Long userId, Long productId);

	void clearCart(Long userId);

}