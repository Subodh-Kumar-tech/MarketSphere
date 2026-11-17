
package com.flipkart.marketsphere.mapper;

import org.springframework.stereotype.Component;

import com.flipkart.marketsphere.dto.UserResponse;
import com.flipkart.marketsphere.entity.User;

@Component
public class UserMapper {

	public UserResponse toUserResponse(User user) {

		return UserResponse.builder().id(user.getId()).name(user.getName()).email(user.getEmail())
				.phone(user.getPhone()).role(user.getRole()).build();
	}
}
