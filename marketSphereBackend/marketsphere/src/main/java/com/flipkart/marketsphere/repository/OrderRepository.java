
package com.flipkart.marketsphere.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flipkart.marketsphere.entity.Order;
import com.flipkart.marketsphere.entity.OrderStatus;

public interface OrderRepository extends JpaRepository<Order, Long> {

	Optional<Order> findByOrderNumber(String orderNumber);

	List<Order> findByUserId(Long userId);

	List<Order> findByStatus(OrderStatus status);

	List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);

	boolean existsByOrderNumber(String orderNumber);

	long count();
}
