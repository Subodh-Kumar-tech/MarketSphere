
package com.flipkart.marketsphere.service.impl;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flipkart.marketsphere.dto.VendorRegistrationRequest;
import com.flipkart.marketsphere.dto.VendorResponse;
import com.flipkart.marketsphere.entity.Role;
import com.flipkart.marketsphere.entity.User;
import com.flipkart.marketsphere.entity.Vendor;
import com.flipkart.marketsphere.entity.VendorStatus;
import com.flipkart.marketsphere.exception.UserNotFoundException;
import com.flipkart.marketsphere.repository.UserRepository;
import com.flipkart.marketsphere.repository.VendorRepository;
import com.flipkart.marketsphere.service.VendorService;

@Service
public class VendorServiceImpl implements VendorService {

	private final VendorRepository vendorRepository;
	private final UserRepository userRepository;

	public VendorServiceImpl(VendorRepository vendorRepository, UserRepository userRepository) {
		this.vendorRepository = vendorRepository;
		this.userRepository = userRepository;
	}

	// Get authenticated user's email
	private String getCurrentUserEmail() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			throw new AccessDeniedException("User is not authenticated");
		}

		return authentication.getName();
	}

	// Check whether logged-in user is ADMIN
	private boolean isAdmin() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		return authentication.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
	}

	// Validate ownership using user ID
	private void validateOwnership(Long userId) {

		if (isAdmin()) {
			return;
		}

		User currentUser = userRepository.findByEmail(getCurrentUserEmail())
				.orElseThrow(() -> new UserNotFoundException("Authenticated user not found"));

		if (!currentUser.getId().equals(userId)) {
			throw new AccessDeniedException("You are not allowed to access another user's store");
		}
	}

	@Override
	@Transactional
	public VendorResponse registerVendor(Long userId, VendorRegistrationRequest request) {

		// User can register only their own account as vendor
		validateOwnership(userId);

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

		if (vendorRepository.existsByUserId(userId)) {
			throw new RuntimeException("This user already has a vendor profile");
		}

		if (vendorRepository.existsByStoreName(request.getStoreName())) {
			throw new RuntimeException("Store name already exists");
		}

		Vendor vendor = Vendor.builder().storeName(request.getStoreName()).description(request.getDescription())
				.businessEmail(request.getBusinessEmail()).businessPhone(request.getBusinessPhone())
				.address(request.getAddress()).status(VendorStatus.PENDING).user(user).build();

		Vendor savedVendor = vendorRepository.save(vendor);

		// Change CUSTOMER role to VENDOR
		user.setRole(Role.VENDOR);
		userRepository.save(user);

		return convertToVendorResponse(savedVendor, "Vendor registered successfully. Awaiting admin approval");
	}

	@Override
	public List<VendorResponse> getAllVendors() {
		return vendorRepository.findAll().stream()
				.map(vendor -> convertToVendorResponse(vendor, "Vendor retrieved successfully")).toList();
	}

	@Override
	public VendorResponse getVendorById(Long id) {
		Vendor vendor = vendorRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Vendor not found with id: " + id));

		return convertToVendorResponse(vendor, "Vendor retrieved successfully");
	}

	@Override
	@Transactional
	public VendorResponse updateVendor(Long id, VendorRegistrationRequest request) {

		Vendor vendor = vendorRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Vendor not found with id: " + id));

		// Only store owner or ADMIN can update
		validateOwnership(vendor.getUser().getId());

		if (!vendor.getStoreName().equals(request.getStoreName())
				&& vendorRepository.existsByStoreName(request.getStoreName())) {
			throw new RuntimeException("Store name already exists");
		}

		vendor.setStoreName(request.getStoreName());
		vendor.setDescription(request.getDescription());
		vendor.setBusinessEmail(request.getBusinessEmail());
		vendor.setBusinessPhone(request.getBusinessPhone());
		vendor.setAddress(request.getAddress());

		Vendor updatedVendor = vendorRepository.save(vendor);

		return convertToVendorResponse(updatedVendor, "Vendor updated successfully");
	}

	@Override
	@Transactional
	public void deleteVendor(Long id) {
		Vendor vendor = vendorRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Vendor not found with id: " + id));

		vendorRepository.delete(vendor);
	}

	@Override
	@Transactional
	public VendorResponse approveVendor(Long id) {
		Vendor vendor = vendorRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Vendor not found with id: " + id));

		vendor.setStatus(VendorStatus.APPROVED);

		Vendor updatedVendor = vendorRepository.save(vendor);

		return convertToVendorResponse(updatedVendor, "Vendor approved successfully");
	}

	@Override
	@Transactional
	public VendorResponse rejectVendor(Long id) {
		Vendor vendor = vendorRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Vendor not found with id: " + id));

		vendor.setStatus(VendorStatus.REJECTED);

		Vendor updatedVendor = vendorRepository.save(vendor);

		return convertToVendorResponse(updatedVendor, "Vendor rejected successfully");
	}

	@Override
	@Transactional
	public VendorResponse suspendVendor(Long id) {
		Vendor vendor = vendorRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Vendor not found with id: " + id));

		vendor.setStatus(VendorStatus.SUSPENDED);

		Vendor updatedVendor = vendorRepository.save(vendor);

		return convertToVendorResponse(updatedVendor, "Vendor suspended successfully");
	}

	@Override
	public List<VendorResponse> getPendingVendors() {
		return vendorRepository.findByStatus(VendorStatus.PENDING).stream()
				.map(vendor -> convertToVendorResponse(vendor, "Pending admin approval")).toList();
	}

	@Override
	public VendorResponse getVendorByUserId(Long userId) {

		// Vendor can access only their own store
		validateOwnership(userId);

		Vendor vendor = vendorRepository.findByUserId(userId)
				.orElseThrow(() -> new RuntimeException("Vendor profile not found for user id: " + userId));

		return convertToVendorResponse(vendor, "Vendor store details fetched successfully");
	}

	@Override
	public VendorStatus getVendorStatus(Long userId) {

		// Customer/vendor can access own status; ADMIN can access any
		validateOwnership(userId);

		Vendor vendor = vendorRepository.findByUserId(userId)
				.orElseThrow(() -> new RuntimeException("Vendor profile not found for user id: " + userId));

		return vendor.getStatus();
	}

	private VendorResponse convertToVendorResponse(Vendor vendor, String message) {

		return new VendorResponse(vendor.getId(), vendor.getStoreName(), vendor.getDescription(),
				vendor.getBusinessEmail(), vendor.getBusinessPhone(), vendor.getAddress(), vendor.getStatus(),
				vendor.getUser().getId(), message);
	}
}
