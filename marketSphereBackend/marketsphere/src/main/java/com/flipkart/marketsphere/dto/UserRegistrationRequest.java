
package com.flipkart.marketsphere.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationRequest {

	@NotBlank(message = "Name is required")
	@Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
	private String name;

	@NotBlank(message = "Email is required")
	@Email(message = "Please provide a valid email")
	private String email;

	@Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone number must be a valid 10-digit Indian mobile number")
	private String phone;

	@NotBlank(message = "Password is required")
	@Size(min = 8, message = "Password must contain at least 8 characters")
	private String password;
}
