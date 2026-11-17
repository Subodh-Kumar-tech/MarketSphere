
package com.flipkart.marketsphere.dto;

import com.flipkart.marketsphere.entity.OrderStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderStatusRequest {

	@NotNull(message = "Order status is required")
	private OrderStatus status;

	public OrderStatus getStatus() {
		return status;
	}
}
