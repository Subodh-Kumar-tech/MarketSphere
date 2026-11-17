
package com.flipkart.marketsphere.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequest {

	@NotBlank(message = "Product name is required")
	@Size(min = 3, max = 150, message = "Product name must be between 3 and 150 characters")
	private String name;

	@Size(max = 1000, message = "Description cannot exceed 1000 characters")
	private String description;

	@NotNull(message = "Product price is required")
	@DecimalMin(value = "0.01", message = "Price must be greater than zero")
	@Digits(integer = 10, fraction = 2, message = "Price can have at most 2 decimal places")
	private BigDecimal price;

	@NotNull(message = "Stock quantity is required")
	@Min(value = 0, message = "Stock cannot be negative")
	private Integer stockQuantity;

	@NotBlank(message = "SKU is required")
	@Size(max = 100, message = "SKU cannot exceed 100 characters")
	private String sku;

	@Size(max = 500, message = "Image URL cannot exceed 500 characters")
	private String imageUrl;

	@NotNull(message = "Category ID is required")
	private Long categoryId;

	@NotNull(message = "Vendor ID is required")
	private Long vendorId;
}
