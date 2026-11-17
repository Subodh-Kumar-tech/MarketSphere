
package com.flipkart.marketsphere.repository.specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.flipkart.marketsphere.entity.Product;

import jakarta.persistence.criteria.Predicate;

public class ProductSpecification {

	public static Specification<Product> filterProducts(String name, BigDecimal minPrice, BigDecimal maxPrice) {

		return (root, query, criteriaBuilder) -> {

			List<Predicate> predicates = new ArrayList<>();

			// Search by product name
			if (name != null && !name.isBlank()) {
				predicates.add(
						criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
			}

			// Minimum price
			if (minPrice != null) {
				predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
			}

			// Maximum price
			if (maxPrice != null) {
				predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
			}

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
}
