
package com.flipkart.marketsphere.dto;

import com.flipkart.marketsphere.entity.VendorStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VendorResponse {

	private Long id;
	private String storeName;
	private String description;
	private String businessEmail;
	private String businessPhone;
	private String address;
	private VendorStatus status;
	private Long userId;
	private String message;
}
