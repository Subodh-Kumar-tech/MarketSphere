
package com.flipkart.marketsphere.dto;

import com.flipkart.marketsphere.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

	private Long id;
	private String name;
	private String email;
	private Role role;
	private String token;
	private String message;
}
