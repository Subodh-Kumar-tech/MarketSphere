
package com.flipkart.marketsphere.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import com.flipkart.marketsphere.dto.ApiResponse;
import com.flipkart.marketsphere.dto.CategoryRequest;
import com.flipkart.marketsphere.dto.CategoryResponse;
import com.flipkart.marketsphere.service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categories", description = "APIs for managing product categories")
public class CategoryController {

	private final CategoryService categoryService;

	public CategoryController(CategoryService categoryService) {
		this.categoryService = categoryService;
	}

	// ==================================================
	// CREATE CATEGORY - ADMIN
	// ==================================================

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Create category", description = "Creates a new product category")
	public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(@Valid @RequestBody CategoryRequest request) {

		CategoryResponse response = categoryService.createCategory(request);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success("Category created successfully", response));
	}

	// ==================================================
	// GET ALL CATEGORIES - PUBLIC
	// ==================================================

	@GetMapping
	@Operation(summary = "Get all categories", description = "Retrieves all available product categories")
	public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {

		List<CategoryResponse> response = categoryService.getAllCategories();

		return ResponseEntity.ok(ApiResponse.success("Categories fetched successfully", response));
	}

	// ==================================================
	// GET CATEGORY BY ID - PUBLIC
	// ==================================================

	@GetMapping("/{id}")
	@Operation(summary = "Get category by ID", description = "Retrieves a specific category using its ID")
	public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable Long id) {

		CategoryResponse response = categoryService.getCategoryById(id);

		return ResponseEntity.ok(ApiResponse.success("Category fetched successfully", response));
	}

	// ==================================================
	// UPDATE CATEGORY - ADMIN
	// ==================================================

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Update category", description = "Updates an existing category using its ID")
	public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(@PathVariable Long id,
			@Valid @RequestBody CategoryRequest request) {

		CategoryResponse response = categoryService.updateCategory(id, request);

		return ResponseEntity.ok(ApiResponse.success("Category updated successfully", response));
	}

	// ==================================================
	// DELETE CATEGORY - ADMIN
	// ==================================================

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Delete category", description = "Deletes a category using its unique ID")
	public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {

		categoryService.deleteCategory(id);

		return ResponseEntity.ok(ApiResponse.success("Category deleted successfully", null));
	}
}
