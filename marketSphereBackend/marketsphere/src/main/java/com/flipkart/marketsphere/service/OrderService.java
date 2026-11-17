
package com.flipkart.marketsphere.service;

import java.util.List;

import com.flipkart.marketsphere.dto.CheckoutRequest;
import com.flipkart.marketsphere.dto.OrderResponse;
import com.flipkart.marketsphere.dto.VendorOrderItemResponse;
import com.flipkart.marketsphere.entity.OrderStatus;

public interface OrderService {

	OrderResponse placeOrder(Long userId, CheckoutRequest request);

	List<OrderResponse> getOrdersByUserId(Long userId);

	OrderResponse getOrderById(Long orderId);

	OrderResponse updateOrderStatus(Long orderId, OrderStatus status);

	OrderResponse cancelOrder(Long orderId);

	List<VendorOrderItemResponse> getVendorOrderItems(Long vendorId);

}
