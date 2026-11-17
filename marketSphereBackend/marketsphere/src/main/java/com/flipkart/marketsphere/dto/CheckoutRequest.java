
package com.flipkart.marketsphere.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckoutRequest {

	@NotBlank(message = "Shipping address is required")
	@Size(max = 500, message = "Shipping address cannot exceed 500 characters")
	private String shippingAddress;

	public String getShippingAddress() {
		return shippingAddress;
	}
}
