
package com.flipkart.marketsphere.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {

	@NotBlank(message = "Payment method is required")
	@Pattern(regexp = "COD|UPI|CARD", message = "Payment method must be COD, UPI or CARD")
	private String paymentMethod;

	public String getPaymentMethod() {
		return paymentMethod;
	}
}
