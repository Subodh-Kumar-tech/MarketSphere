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
public class CartItemResponse {

	private Long itemId;

	private Long productId;

	private String productName;

	private String imageUrl;

	private Integer quantity;

	private BigDecimal unitPrice;

	private BigDecimal subtotal;
}