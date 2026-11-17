
package com.flipkart.marketsphere.service.impl;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flipkart.marketsphere.dto.AdminDashboardResponse;
import com.flipkart.marketsphere.entity.PaymentStatus;
import com.flipkart.marketsphere.entity.VendorStatus;
import com.flipkart.marketsphere.repository.OrderRepository;
import com.flipkart.marketsphere.repository.PaymentRepository;
import com.flipkart.marketsphere.repository.ProductRepository;
import com.flipkart.marketsphere.repository.UserRepository;
import com.flipkart.marketsphere.repository.VendorRepository;
import com.flipkart.marketsphere.service.AdminDashboardService;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {

	private final UserRepository userRepository;
	private final VendorRepository vendorRepository;
	private final ProductRepository productRepository;
	private final OrderRepository orderRepository;
	private final PaymentRepository paymentRepository;

	public AdminDashboardServiceImpl(UserRepository userRepository, VendorRepository vendorRepository,
			ProductRepository productRepository, OrderRepository orderRepository, PaymentRepository paymentRepository) {

		this.userRepository = userRepository;
		this.vendorRepository = vendorRepository;
		this.productRepository = productRepository;
		this.orderRepository = orderRepository;
		this.paymentRepository = paymentRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public AdminDashboardResponse getDashboardStatistics() {

		long totalUsers = userRepository.count();

		long totalVendors = vendorRepository.count();

		long pendingVendors = vendorRepository.countByStatus(VendorStatus.PENDING);

		long totalProducts = productRepository.count();

		long totalOrders = orderRepository.count();

		long successfulPayments = paymentRepository.countByStatus(PaymentStatus.SUCCESS);

		BigDecimal totalRevenue = paymentRepository.calculateRevenueByStatus(PaymentStatus.SUCCESS);

		// Handle null revenue when no successful payments exist
		if (totalRevenue == null) {
			totalRevenue = BigDecimal.ZERO;
		}

		return AdminDashboardResponse.builder().totalUsers(totalUsers).totalVendors(totalVendors)
				.pendingVendors(pendingVendors).totalProducts(totalProducts).totalOrders(totalOrders)
				.successfulPayments(successfulPayments).totalRevenue(totalRevenue).build();
	}
}
