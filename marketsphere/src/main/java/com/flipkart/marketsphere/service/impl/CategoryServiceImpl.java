
package com.flipkart.marketsphere.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.flipkart.marketsphere.dto.CategoryRequest;
import com.flipkart.marketsphere.dto.CategoryResponse;
import com.flipkart.marketsphere.entity.Category;
import com.flipkart.marketsphere.exception.CategoryAlreadyExistsException;
import com.flipkart.marketsphere.exception.CategoryNotFoundException;
import com.flipkart.marketsphere.repository.CategoryRepository;
import com.flipkart.marketsphere.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;

	public CategoryServiceImpl(CategoryRepository categoryRepository) {
		this.categoryRepository = categoryRepository;
	}

	@Override
	public CategoryResponse createCategory(CategoryRequest request) {

		if (categoryRepository.existsByName(request.getName())) {
			throw new CategoryAlreadyExistsException("Category already exists with name: " + request.getName());
		}

		Category category = Category.builder().name(request.getName()).description(request.getDescription())
				.active(true).build();

		Category savedCategory = categoryRepository.save(category);

		return convertToResponse(savedCategory, "Category created successfully");
	}

	@Override
	public List<CategoryResponse> getAllCategories() {

		return categoryRepository.findAll().stream()
				.map(category -> convertToResponse(category, "Category fetched successfully")).toList();
	}

	@Override
	public CategoryResponse getCategoryById(Long id) {

		Category category = categoryRepository.findById(id)
				.orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));

		return convertToResponse(category, "Category fetched successfully");
	}

	@Override
	public CategoryResponse updateCategory(Long id, CategoryRequest request) {

		Category category = categoryRepository.findById(id)
				.orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));

		if (!category.getName().equals(request.getName()) && categoryRepository.existsByName(request.getName())) {
			throw new RuntimeException("Category name already exists");
		}

		category.setName(request.getName());
		category.setDescription(request.getDescription());

		Category updatedCategory = categoryRepository.save(category);

		return convertToResponse(updatedCategory, "Category updated successfully");
	}

	@Override
	public void deleteCategory(Long id) {

		Category category = categoryRepository.findById(id)
				.orElseThrow(() -> new CategoryNotFoundException("Category not found with id: " + id));

		categoryRepository.delete(category);
	}

	private CategoryResponse convertToResponse(Category category, String message) {

		return new CategoryResponse(category.getId(), category.getName(), category.getDescription(),
				category.getActive(), message);
	}
}
