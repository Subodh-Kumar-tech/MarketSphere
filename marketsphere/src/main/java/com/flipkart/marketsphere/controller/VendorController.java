
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
import com.flipkart.marketsphere.dto.VendorRegistrationRequest;
import com.flipkart.marketsphere.dto.VendorResponse;
import com.flipkart.marketsphere.entity.VendorStatus;
import com.flipkart.marketsphere.service.VendorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/vendors")
@Tag(name = "Vendors", description = "APIs for vendor registration, store management and vendor administration")
public class VendorController {

	private final VendorService vendorService;

	public VendorController(VendorService vendorService) {
		this.vendorService = vendorService;
	}

	// Register Vendor
	@PostMapping("/register/{userId}")
	@PreAuthorize("hasRole('CUSTOMER')")
	@Operation(summary = "Register vendor")
	public ResponseEntity<ApiResponse<VendorResponse>> registerVendor(@PathVariable Long userId,
			@Valid @RequestBody VendorRegistrationRequest request) {

		VendorResponse response = vendorService.registerVendor(userId, request);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success("Vendor registered successfully. Awaiting approval", response));
	}

	// Get All Vendors
	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Get all vendors")
	public ResponseEntity<ApiResponse<List<VendorResponse>>> getAllVendors() {

		List<VendorResponse> response = vendorService.getAllVendors();

		return ResponseEntity.ok(ApiResponse.success("Vendors fetched successfully", response));
	}

	// Get Vendor By ID
	@GetMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Get vendor by ID")
	public ResponseEntity<ApiResponse<VendorResponse>> getVendorById(@PathVariable Long id) {

		VendorResponse response = vendorService.getVendorById(id);

		return ResponseEntity.ok(ApiResponse.success("Vendor fetched successfully", response));
	}

	// Update Vendor Store
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('VENDOR')")
	@Operation(summary = "Update vendor store")
	public ResponseEntity<ApiResponse<VendorResponse>> updateVendor(@PathVariable Long id,
			@Valid @RequestBody VendorRegistrationRequest request) {

		// Ownership validation must be implemented in service layer.
		VendorResponse response = vendorService.updateVendor(id, request);

		return ResponseEntity.ok(ApiResponse.success("Vendor store updated successfully", response));
	}

	// Delete Vendor
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Delete vendor")
	public ResponseEntity<ApiResponse<Void>> deleteVendor(@PathVariable Long id) {

		vendorService.deleteVendor(id);

		return ResponseEntity.ok(ApiResponse.success("Vendor deleted successfully", null));
	}

	// Approve Vendor
	@PutMapping("/admin/{id}/approve")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Approve vendor")
	public ResponseEntity<ApiResponse<VendorResponse>> approveVendor(@PathVariable Long id) {

		VendorResponse response = vendorService.approveVendor(id);

		return ResponseEntity.ok(ApiResponse.success("Vendor approved successfully", response));
	}

	// Reject Vendor
	@PutMapping("/admin/{id}/reject")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Reject vendor")
	public ResponseEntity<ApiResponse<VendorResponse>> rejectVendor(@PathVariable Long id) {

		VendorResponse response = vendorService.rejectVendor(id);

		return ResponseEntity.ok(ApiResponse.success("Vendor rejected successfully", response));
	}

	// Suspend Vendor
	@PutMapping("/admin/{id}/suspend")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Suspend vendor")
	public ResponseEntity<ApiResponse<VendorResponse>> suspendVendor(@PathVariable Long id) {

		VendorResponse response = vendorService.suspendVendor(id);

		return ResponseEntity.ok(ApiResponse.success("Vendor suspended successfully", response));
	}

	// Get Pending Vendors
	@GetMapping("/admin/pending")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Get pending vendors")
	public ResponseEntity<ApiResponse<List<VendorResponse>>> getPendingVendors() {

		List<VendorResponse> response = vendorService.getPendingVendors();

		return ResponseEntity.ok(ApiResponse.success("Pending vendors fetched successfully", response));
	}

	// Get My Store
	@GetMapping("/my-store/{userId}")
	@PreAuthorize("hasRole('VENDOR')")
	@Operation(summary = "Get my store")
	public ResponseEntity<ApiResponse<VendorResponse>> getMyStore(@PathVariable Long userId) {

		// Ownership validation must be implemented in service layer.
		VendorResponse response = vendorService.getVendorByUserId(userId);

		return ResponseEntity.ok(ApiResponse.success("Vendor store fetched successfully", response));
	}

	// Get Vendor Status
	@GetMapping("/status/{userId}")
	@PreAuthorize("hasAnyRole('CUSTOMER','VENDOR','ADMIN')")
	@Operation(summary = "Get vendor status")
	public ResponseEntity<ApiResponse<VendorStatus>> getVendorStatus(@PathVariable Long userId) {

		VendorStatus response = vendorService.getVendorStatus(userId);

		return ResponseEntity.ok(ApiResponse.success("Vendor status fetched successfully", response));
	}
}
