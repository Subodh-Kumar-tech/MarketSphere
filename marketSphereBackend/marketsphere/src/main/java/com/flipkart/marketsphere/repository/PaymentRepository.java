
package com.flipkart.marketsphere.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.flipkart.marketsphere.entity.Payment;
import com.flipkart.marketsphere.entity.PaymentStatus;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

	Optional<Payment> findByOrderId(Long orderId);

	boolean existsByOrderId(Long orderId);

	Optional<Payment> findByTransactionId(String transactionId);

	long countByStatus(PaymentStatus status);

	@Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = :status")
	BigDecimal calculateRevenueByStatus(@Param("status") PaymentStatus status);
}
