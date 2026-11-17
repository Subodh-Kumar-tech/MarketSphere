
package com.flipkart.marketsphere.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.flipkart.marketsphere.entity.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

	private Long orderId;

	private String orderNumber;

	private Long userId;

	private BigDecimal totalAmount;

	private OrderStatus status;

	private String shippingAddress;

	private LocalDateTime orderDate;

	private List<OrderItemResponse> orderItems;

	private String message;
}
