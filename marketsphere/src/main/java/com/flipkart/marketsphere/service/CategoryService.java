package com.flipkart.marketsphere.service;

import java.util.List;

import com.flipkart.marketsphere.dto.CategoryRequest;
import com.flipkart.marketsphere.dto.CategoryResponse;

public interface CategoryService {

	CategoryResponse createCategory(CategoryRequest request);

	List<CategoryResponse> getAllCategories();

	CategoryResponse getCategoryById(Long id);

	CategoryResponse updateCategory(Long id, CategoryRequest request);

	void deleteCategory(Long id);
}