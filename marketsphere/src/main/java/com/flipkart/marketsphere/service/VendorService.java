
package com.flipkart.marketsphere.service;

import java.util.List;

import com.flipkart.marketsphere.dto.VendorRegistrationRequest;
import com.flipkart.marketsphere.dto.VendorResponse;
import com.flipkart.marketsphere.entity.VendorStatus;

public interface VendorService {

	VendorResponse registerVendor(Long userId, VendorRegistrationRequest request);

	List<VendorResponse> getAllVendors();

	VendorResponse getVendorById(Long id);

	VendorResponse updateVendor(Long id, VendorRegistrationRequest request);

	void deleteVendor(Long id);

	VendorResponse approveVendor(Long id);

	VendorResponse rejectVendor(Long id);

	VendorResponse suspendVendor(Long id);

	List<VendorResponse> getPendingVendors();

	VendorResponse getVendorByUserId(Long userId);

	VendorStatus getVendorStatus(Long userId);
}
