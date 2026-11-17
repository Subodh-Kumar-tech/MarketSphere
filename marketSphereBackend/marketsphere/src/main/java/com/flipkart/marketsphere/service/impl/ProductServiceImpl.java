
package com.flipkart.marketsphere.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flipkart.marketsphere.dto.ProductRequest;
import com.flipkart.marketsphere.dto.ProductResponse;
import com.flipkart.marketsphere.dto.StockUpdateRequest;
import com.flipkart.marketsphere.entity.Category;
import com.flipkart.marketsphere.entity.Product;
import com.flipkart.marketsphere.entity.ProductStatus;
import com.flipkart.marketsphere.entity.Vendor;
import com.flipkart.marketsphere.entity.VendorStatus;
import com.flipkart.marketsphere.exception.ProductAlreadyExistsException;
import com.flipkart.marketsphere.exception.ProductNotFoundException;
import com.flipkart.marketsphere.repository.CategoryRepository;
import com.flipkart.marketsphere.repository.ProductRepository;
import com.flipkart.marketsphere.repository.VendorRepository;
import com.flipkart.marketsphere.repository.specification.ProductSpecification;
import com.flipkart.marketsphere.service.ProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;
	private final VendorRepository vendorRepository;
	private final CategoryRepository categoryRepository;

	// ==================================================
	// SECURITY HELPERS
	// ==================================================

	private String getCurrentUserEmail() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			throw new AccessDeniedException("User is not authenticated");
		}

		return authentication.getName();
	}

	private boolean isAdmin() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null) {
			return false;
		}

		return authentication.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
	}

	// Validate vendor ownership and approval before creating product
	private void validateVendorOwnership(Vendor vendor) {

		if (isAdmin()) {
			return;
		}

		if (!vendor.getUser().getEmail().equalsIgnoreCase(getCurrentUserEmail())) {

			throw new AccessDeniedException("You can create products only for your own store");
		}

		if (vendor.getStatus() != VendorStatus.APPROVED) {
			throw new AccessDeniedException("Only approved vendors can create products");
		}
	}

	// Validate product ownership before modifying product
	private void validateProductOwnership(Product product) {

		if (isAdmin()) {
			return;
		}

		Vendor vendor = product.getVendor();

		if (!vendor.getUser().getEmail().equalsIgnoreCase(getCurrentUserEmail())) {

			throw new AccessDeniedException("You are not allowed to manage another vendor's product");
		}

		if (vendor.getStatus() != VendorStatus.APPROVED) {
			throw new AccessDeniedException("Only approved vendors can manage products");
		}
	}

	// ==================================================
	// CREATE PRODUCT
	// ==================================================

	@Override
	public ProductResponse createProduct(ProductRequest request) {

		if (productRepository.existsBySku(request.getSku())) {
			throw new ProductAlreadyExistsException("Product with this SKU already exists");
		}

		Vendor vendor = vendorRepository.findById(request.getVendorId())
				.orElseThrow(() -> new RuntimeException("Vendor not found with ID: " + request.getVendorId()));

		validateVendorOwnership(vendor);

		Category category = categoryRepository.findById(request.getCategoryId())
				.orElseThrow(() -> new RuntimeException("Category not found with ID: " + request.getCategoryId()));

		if (!category.getActive()) {
			throw new RuntimeException("Cannot create product under inactive category");
		}

		ProductStatus status = request.getStockQuantity() == 0 ? ProductStatus.OUT_OF_STOCK : ProductStatus.ACTIVE;

		Product product = Product.builder().name(request.getName()).description(request.getDescription())
				.price(request.getPrice()).stockQuantity(request.getStockQuantity()).sku(request.getSku())
				.imageUrl(request.getImageUrl()).status(status).vendor(vendor).category(category).build();

		Product savedProduct = productRepository.save(product);

		return convertToResponse(savedProduct, "Product created successfully");
	}

	// ==================================================
	// GET ALL PRODUCTS
	// ==================================================

	@Override
	@Transactional(readOnly = true)
	public List<ProductResponse> getAllProducts() {

		return productRepository.findAll().stream()
				.map(product -> convertToResponse(product, "Product fetched successfully")).toList();
	}

	// ==================================================
	// GET PRODUCT BY ID
	// ==================================================

	@Override
	@Transactional(readOnly = true)
	public ProductResponse getProductById(Long id) {

		Product product = productRepository.findById(id)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));

		return convertToResponse(product, "Product fetched successfully");
	}

	// ==================================================
	// GET PRODUCTS BY VENDOR
	// ==================================================

	@Override
	@Transactional(readOnly = true)
	public List<ProductResponse> getProductsByVendor(Long vendorId) {

		return productRepository.findByVendorId(vendorId).stream()
				.map(product -> convertToResponse(product, "Product fetched successfully")).toList();
	}

	// ==================================================
	// GET PRODUCTS BY CATEGORY
	// ==================================================

	@Override
	@Transactional(readOnly = true)
	public List<ProductResponse> getProductsByCategory(Long categoryId) {

		return productRepository.findByCategoryId(categoryId).stream()
				.map(product -> convertToResponse(product, "Product fetched successfully")).toList();
	}

	// ==================================================
	// UPDATE PRODUCT
	// ==================================================

	@Override
	public ProductResponse updateProduct(Long id, ProductRequest request) {

		Product product = productRepository.findById(id)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));

		validateProductOwnership(product);

		if (!product.getSku().equals(request.getSku()) && productRepository.existsBySku(request.getSku())) {

			throw new ProductAlreadyExistsException("Product with this SKU already exists");
		}

		Category category = categoryRepository.findById(request.getCategoryId())
				.orElseThrow(() -> new RuntimeException("Category not found with ID: " + request.getCategoryId()));

		if (!category.getActive()) {
			throw new RuntimeException("Cannot update product under inactive category");
		}

		product.setName(request.getName());
		product.setDescription(request.getDescription());
		product.setPrice(request.getPrice());
		product.setStockQuantity(request.getStockQuantity());
		product.setSku(request.getSku());
		product.setImageUrl(request.getImageUrl());
		product.setCategory(category);

		// Preserve INACTIVE status
		if (product.getStatus() != ProductStatus.INACTIVE) {
			product.setStatus(request.getStockQuantity() == 0 ? ProductStatus.OUT_OF_STOCK : ProductStatus.ACTIVE);
		}

		// Vendor ownership is intentionally not changed
		Product updatedProduct = productRepository.save(product);

		return convertToResponse(updatedProduct, "Product updated successfully");
	}

	// ==================================================
	// DELETE PRODUCT
	// ==================================================

	@Override
	public void deleteProduct(Long id) {

		Product product = productRepository.findById(id)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));

		validateProductOwnership(product);

		productRepository.delete(product);
	}

	// ==================================================
	// ACTIVATE PRODUCT
	// ==================================================

	@Override
	public ProductResponse activateProduct(Long id) {

		Product product = productRepository.findById(id)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));

		validateProductOwnership(product);

		if (product.getStockQuantity() == 0) {
			throw new RuntimeException("Cannot activate product with zero stock");
		}

		product.setStatus(ProductStatus.ACTIVE);

		Product updatedProduct = productRepository.save(product);

		return convertToResponse(updatedProduct, "Product activated successfully");
	}

	// ==================================================
	// DEACTIVATE PRODUCT
	// ==================================================

	@Override
	public ProductResponse deactivateProduct(Long id) {

		Product product = productRepository.findById(id)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));

		validateProductOwnership(product);

		product.setStatus(ProductStatus.INACTIVE);

		Product updatedProduct = productRepository.save(product);

		return convertToResponse(updatedProduct, "Product deactivated successfully");
	}

	// ==================================================
	// CONVERT ENTITY TO RESPONSE DTO
	// ==================================================

	private ProductResponse convertToResponse(Product product, String message) {

		return new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getPrice(),
				product.getStockQuantity(), product.getSku(), product.getImageUrl(), product.getStatus(),
				product.getVendor().getId(), product.getVendor().getStoreName(), product.getCategory().getId(),
				product.getCategory().getName(), message);
	}

	// ==================================================
	// SEARCH PRODUCTS
	// ==================================================

	@Override
	@Transactional(readOnly = true)
	public List<ProductResponse> searchProducts(String name) {

		return productRepository.findByNameContainingIgnoreCase(name).stream()
				.map(product -> convertToResponse(product, "Product found successfully")).toList();
	}

	// ==================================================
	// SORT PRODUCTS
	// ==================================================

	@Override
	@Transactional(readOnly = true)
	public List<ProductResponse> getAllProductsSorted(String sortBy, String direction) {

		Sort sort;

		if (direction.equalsIgnoreCase("desc")) {
			sort = Sort.by(sortBy).descending();
		} else {
			sort = Sort.by(sortBy).ascending();
		}

		return productRepository.findAll(sort).stream()
				.map(product -> convertToResponse(product, "Products sorted successfully")).toList();
	}

	// ==================================================
	// PAGINATION + SORTING
	// ==================================================

	@Override
	@Transactional(readOnly = true)
	public Page<ProductResponse> getAllProductsPaginated(int page, int size, String sortBy, String direction) {

		if (page < 0) {
			throw new IllegalArgumentException("Page number cannot be negative");
		}

		if (size <= 0) {
			throw new IllegalArgumentException("Page size must be greater than zero");
		}

		if (!List.of("id", "name", "price", "stockQuantity").contains(sortBy)) {

			throw new IllegalArgumentException("Invalid sort field: " + sortBy);
		}

		Sort sort;

		if ("desc".equalsIgnoreCase(direction)) {
			sort = Sort.by(sortBy).descending();
		} else if ("asc".equalsIgnoreCase(direction)) {
			sort = Sort.by(sortBy).ascending();
		} else {
			throw new IllegalArgumentException("Direction must be asc or desc");
		}

		Pageable pageable = PageRequest.of(page, size, sort);

		Page<Product> productPage = productRepository.findAll(pageable);

		return productPage.map(product -> convertToResponse(product, "Product fetched successfully"));
	}

	// ==================================================
	// FILTER PRODUCTS BY PRICE
	// ==================================================

	@Override
	@Transactional(readOnly = true)
	public List<ProductResponse> filterProductsByPrice(BigDecimal minPrice, BigDecimal maxPrice) {

		if (minPrice == null || maxPrice == null) {
			throw new IllegalArgumentException("Minimum and maximum prices are required");
		}

		if (minPrice.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("Minimum price cannot be negative");
		}

		if (minPrice.compareTo(maxPrice) > 0) {
			throw new IllegalArgumentException("Minimum price cannot exceed maximum price");
		}

		return productRepository.findByPriceBetween(minPrice, maxPrice).stream()
				.map(product -> convertToResponse(product, "Products filtered successfully")).toList();
	}

	// ==================================================
	// SEARCH + FILTER + SORT + PAGINATION
	// ==================================================

	@Override
	@Transactional(readOnly = true)
	public Page<ProductResponse> filterSearchSortPaginateProducts(String name, BigDecimal minPrice, BigDecimal maxPrice,
			int page, int size, String sortBy, String direction) {

		if (page < 0) {
			throw new IllegalArgumentException("Page number cannot be negative");
		}

		if (size <= 0 || size > 100) {
			throw new IllegalArgumentException("Page size must be between 1 and 100");
		}

		if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) < 0) {

			throw new IllegalArgumentException("Minimum price cannot be negative");
		}

		if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) < 0) {

			throw new IllegalArgumentException("Maximum price cannot be negative");
		}

		if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {

			throw new IllegalArgumentException("Minimum price cannot exceed maximum price");
		}

		if (!List.of("id", "name", "price", "stockQuantity").contains(sortBy)) {

			throw new IllegalArgumentException("Invalid sort field: " + sortBy);
		}

		Sort sort;

		if ("asc".equalsIgnoreCase(direction)) {
			sort = Sort.by(sortBy).ascending();
		} else if ("desc".equalsIgnoreCase(direction)) {
			sort = Sort.by(sortBy).descending();
		} else {
			throw new IllegalArgumentException("Direction must be asc or desc");
		}

		Pageable pageable = PageRequest.of(page, size, sort);

		Specification<Product> specification = ProductSpecification.filterProducts(name, minPrice, maxPrice);

		Page<Product> productPage = productRepository.findAll(specification, pageable);

		return productPage.map(product -> convertToResponse(product, "Products fetched successfully"));
	}

	// ==================================================
	// UPDATE PRODUCT STOCK
	// ==================================================

	@Override
	public ProductResponse updateProductStock(Long productId, StockUpdateRequest request) {

		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));

		validateProductOwnership(product);

		Integer stockQuantity = request.getStockQuantity();

		product.setStockQuantity(stockQuantity);

		// Preserve manually inactive products
		if (product.getStatus() != ProductStatus.INACTIVE) {

			if (stockQuantity == 0) {
				product.setStatus(ProductStatus.OUT_OF_STOCK);
			} else {
				product.setStatus(ProductStatus.ACTIVE);
			}
		}

		Product updatedProduct = productRepository.save(product);

		return convertToResponse(updatedProduct, "Product stock updated successfully");
	}
}
