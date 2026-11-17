
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
public class AdminDashboardResponse {

	private long totalUsers;
	private long totalVendors;
	private long pendingVendors;
	private long totalProducts;
	private long totalOrders;
	private long successfulPayments;
	private BigDecimal totalRevenue;
}
