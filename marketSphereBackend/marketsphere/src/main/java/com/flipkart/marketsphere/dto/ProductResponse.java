
package com.flipkart.marketsphere.dto;

import java.math.BigDecimal;

import com.flipkart.marketsphere.entity.ProductStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductResponse {

	private Long id;

	private String name;

	private String description;

	private BigDecimal price;

	private Integer stockQuantity;

	private String sku;

	private String imageUrl;

	private ProductStatus status;

	private Long vendorId;

	private String storeName;

	private Long categoryId;

	private String categoryName;

	private String message;
}
