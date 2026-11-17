
package com.flipkart.marketsphere.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flipkart.marketsphere.entity.Vendor;
import com.flipkart.marketsphere.entity.VendorStatus;

public interface VendorRepository extends JpaRepository<Vendor, Long> {

	boolean existsByUserId(Long userId);

	boolean existsByStoreName(String storeName);

	List<Vendor> findByStatus(VendorStatus status);

	Optional<Vendor> findByUserId(Long userId);

	long count();

	long countByStatus(VendorStatus status);
}
