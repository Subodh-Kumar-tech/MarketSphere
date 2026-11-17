
package com.flipkart.marketsphere.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.flipkart.marketsphere.dto.ApiResponse;
import com.flipkart.marketsphere.dto.ProductRequest;
import com.flipkart.marketsphere.dto.ProductResponse;
import com.flipkart.marketsphere.dto.StockUpdateRequest;
import com.flipkart.marketsphere.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Product management, search, sorting, filtering, pagination and inventory APIs")
public class ProductController {

	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	// Create Product
	@PostMapping
	@PreAuthorize("hasAnyRole('VENDOR','ADMIN')")
	@Operation(summary = "Create product", description = "Creates a new product associated with an approved vendor and category")
	public ResponseEntity<ApiResponse<ProductResponse>> createProduct(@Valid @RequestBody ProductRequest request) {

		ProductResponse response = productService.createProduct(request);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success("Product created successfully", response));
	}

	// Get Products By Category
	@GetMapping("/category/{categoryId}")
	@Operation(summary = "Get products by category", description = "Retrieves all products belonging to a particular category")
	public ResponseEntity<ApiResponse<List<ProductResponse>>> getProductsByCategory(@PathVariable Long categoryId) {

		List<ProductResponse> response = productService.getProductsByCategory(categoryId);

		return ResponseEntity.ok(ApiResponse.success("Products fetched by category successfully", response));
	}

	// Get Products By Vendor
	@GetMapping("/vendor/{vendorId}")
	@Operation(summary = "Get products by vendor", description = "Retrieves all products belonging to a particular vendor")
	public ResponseEntity<ApiResponse<List<ProductResponse>>> getProductsByVendor(@PathVariable Long vendorId) {

		List<ProductResponse> response = productService.getProductsByVendor(vendorId);

		return ResponseEntity.ok(ApiResponse.success("Products fetched by vendor successfully", response));
	}

	// Get Product By ID
	@GetMapping("/{id}")
	@Operation(summary = "Get product by ID", description = "Fetches product details using its unique product ID")
	public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long id) {

		ProductResponse response = productService.getProductById(id);

		return ResponseEntity.ok(ApiResponse.success("Product fetched successfully", response));
	}

	// Get All Products
	@GetMapping
	@Operation(summary = "Get all products", description = "Retrieves all products available in the marketplace")
	public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {

		List<ProductResponse> response = productService.getAllProducts();

		return ResponseEntity.ok(ApiResponse.success("Products fetched successfully", response));
	}

	// Deactivate Product
	@PutMapping("/{id}/deactivate")
	@PreAuthorize("hasAnyRole('VENDOR','ADMIN')")
	@Operation(summary = "Deactivate product", description = "Deactivates a product so that it is no longer available for purchase")
	public ResponseEntity<ApiResponse<ProductResponse>> deactivateProduct(@PathVariable Long id) {

		ProductResponse response = productService.deactivateProduct(id);

		return ResponseEntity.ok(ApiResponse.success("Product deactivated successfully", response));
	}

	// Activate Product
	@PutMapping("/{id}/activate")
	@PreAuthorize("hasAnyRole('VENDOR','ADMIN')")
	@Operation(summary = "Activate product", description = "Activates a previously inactive product")
	public ResponseEntity<ApiResponse<ProductResponse>> activateProduct(@PathVariable Long id) {

		ProductResponse response = productService.activateProduct(id);

		return ResponseEntity.ok(ApiResponse.success("Product activated successfully", response));
	}

	// Delete Product
	@DeleteMapping("/{id}")
	@PreAuthorize("hasAnyRole('VENDOR','ADMIN')")
	@Operation(summary = "Delete product", description = "Deletes a product using its unique product ID")
	public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {

		productService.deleteProduct(id);

		return ResponseEntity.ok(ApiResponse.success("Product deleted successfully", null));
	}

	// Update Product
	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('VENDOR','ADMIN')")
	@Operation(summary = "Update product", description = "Updates product information such as name, description, price, category and other details")
	public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(@PathVariable Long id,
			@Valid @RequestBody ProductRequest request) {

		ProductResponse response = productService.updateProduct(id, request);

		return ResponseEntity.ok(ApiResponse.success("Product updated successfully", response));
	}

	// Search Products
	@GetMapping("/search")
	@Operation(summary = "Search products by name", description = "Searches products using a name keyword with case-insensitive matching")
	public ResponseEntity<ApiResponse<List<ProductResponse>>> searchProducts(@RequestParam String name) {

		List<ProductResponse> response = productService.searchProducts(name);

		return ResponseEntity.ok(ApiResponse.success("Products search completed successfully", response));
	}

	// Sort Products
	@GetMapping("/sort")
	@Operation(summary = "Sort products", description = "Retrieves products sorted by a specified field in ascending or descending order")
	public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProductsSorted(
			@RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "asc") String direction) {

		List<ProductResponse> response = productService.getAllProductsSorted(sortBy, direction);

		return ResponseEntity.ok(ApiResponse.success("Products sorted successfully", response));
	}

	// Pagination + Sorting
	@GetMapping("/page")
	@Operation(summary = "Get paginated products", description = "Retrieves products page by page with configurable page size and sorting")
	public ResponseEntity<ApiResponse<Page<ProductResponse>>> getAllProductsPaginated(
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size,
			@RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "asc") String direction) {

		Page<ProductResponse> response = productService.getAllProductsPaginated(page, size, sortBy, direction);

		return ResponseEntity.ok(ApiResponse.success("Products page fetched successfully", response));
	}

	// Filter Products By Price
	@GetMapping("/filter/price")
	@Operation(summary = "Filter products by price", description = "Retrieves products whose prices fall within the specified minimum and maximum values")
	public ResponseEntity<ApiResponse<List<ProductResponse>>> filterProductsByPrice(@RequestParam BigDecimal minPrice,
			@RequestParam BigDecimal maxPrice) {

		List<ProductResponse> response = productService.filterProductsByPrice(minPrice, maxPrice);

		return ResponseEntity.ok(ApiResponse.success("Products filtered by price successfully", response));
	}

	// Combined Search + Filter + Sort + Pagination
	@GetMapping("/filter")
	@Operation(summary = "Search, filter, sort and paginate products", description = "Combines optional product name search, price filtering, sorting and pagination in one API")
	public ResponseEntity<ApiResponse<Page<ProductResponse>>> filterSearchSortPaginateProducts(
			@RequestParam(required = false) String name, @RequestParam(required = false) BigDecimal minPrice,
			@RequestParam(required = false) BigDecimal maxPrice, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "5") int size, @RequestParam(defaultValue = "id") String sortBy,
			@RequestParam(defaultValue = "asc") String direction) {

		Page<ProductResponse> response = productService.filterSearchSortPaginateProducts(name, minPrice, maxPrice, page,
				size, sortBy, direction);

		return ResponseEntity.ok(ApiResponse.success("Products filtered and paginated successfully", response));
	}

	// Update Product Stock
	@PutMapping("/{id}/stock")
	@PreAuthorize("hasAnyRole('VENDOR','ADMIN')")
	@Operation(summary = "Update product stock", description = "Updates the inventory quantity of a particular product")
	public ResponseEntity<ApiResponse<ProductResponse>> updateProductStock(@PathVariable Long id,
			@Valid @RequestBody StockUpdateRequest request) {

		ProductResponse response = productService.updateProductStock(id, request);

		return ResponseEntity.ok(ApiResponse.success("Product stock updated successfully", response));
	}
}
