
package com.flipkart.marketsphere.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VendorRegistrationRequest {

	@NotBlank(message = "Store name is required")
	@Size(min = 3, max = 100, message = "Store name must be between 3 and 100 characters")
	private String storeName;

	@Size(max = 500, message = "Description cannot exceed 500 characters")
	private String description;

	@NotBlank(message = "Business email is required")
	@Email(message = "Please provide a valid business email")
	private String businessEmail;

	@Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone number must be a valid 10-digit Indian mobile number")
	private String businessPhone;

	@NotBlank(message = "Address is required")
	private String address;
}
