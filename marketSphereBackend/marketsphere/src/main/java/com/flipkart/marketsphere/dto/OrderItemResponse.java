
package com.flipkart.marketsphere.dto;

import java.math.BigDecimal;

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
public class OrderItemResponse {

	private Long orderItemId;

	private Long productId;

	private String productName;

	private Long vendorId;

	private String storeName;

	private BigDecimal price;

	private Integer quantity;

	private BigDecimal subtotal;
}
