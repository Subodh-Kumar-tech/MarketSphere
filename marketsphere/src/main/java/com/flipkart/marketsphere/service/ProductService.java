
package com.flipkart.marketsphere.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;

import com.flipkart.marketsphere.dto.ProductRequest;
import com.flipkart.marketsphere.dto.ProductResponse;
import com.flipkart.marketsphere.dto.StockUpdateRequest;

public interface ProductService {

	ProductResponse createProduct(ProductRequest request);

	List<ProductResponse> getAllProducts();

	ProductResponse getProductById(Long id);

	List<ProductResponse> getProductsByVendor(Long vendorId);

	List<ProductResponse> getProductsByCategory(Long categoryId);

	ProductResponse updateProduct(Long id, ProductRequest request);

	void deleteProduct(Long id);

	ProductResponse activateProduct(Long id);

	ProductResponse deactivateProduct(Long id);

	List<ProductResponse> searchProducts(String name);

	List<ProductResponse> getAllProductsSorted(String sortBy, String direction);

//	Page<ProductResponse> getAllProductsPaginated(int page, int size);

	Page<ProductResponse> getAllProductsPaginated(int page, int size, String sortBy, String direction);

	List<ProductResponse> filterProductsByPrice(BigDecimal minPrice, BigDecimal maxPrice);

	Page<ProductResponse> filterSearchSortPaginateProducts(String name, BigDecimal minPrice, BigDecimal maxPrice,
			int page, int size, String sortBy, String direction);

	ProductResponse updateProductStock(Long productId, StockUpdateRequest request);
}
