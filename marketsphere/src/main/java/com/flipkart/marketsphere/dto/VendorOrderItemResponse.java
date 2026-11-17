
package com.flipkart.marketsphere.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
public class VendorOrderItemResponse {

	private Long orderItemId;

	private Long orderId;
	private String orderNumber;
	private LocalDateTime orderDate;
	private OrderStatus orderStatus;

	private Long productId;
	private String productName;

	private Integer quantity;
	private BigDecimal price;
	private BigDecimal subtotal;

	private Long customerId;
	private String shippingAddress;
}
