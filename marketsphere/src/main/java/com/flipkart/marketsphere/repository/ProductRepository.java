
package com.flipkart.marketsphere.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.flipkart.marketsphere.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

	boolean existsBySku(String sku);

	List<Product> findByVendorId(Long vendorId);

	List<Product> findByCategoryId(Long categoryId);

	// Search products by name
	List<Product> findByNameContainingIgnoreCase(String name);

	List<Product> findAll(Sort sort);

	Page<Product> findAll(Pageable pageable);

	List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

	long count();

}
