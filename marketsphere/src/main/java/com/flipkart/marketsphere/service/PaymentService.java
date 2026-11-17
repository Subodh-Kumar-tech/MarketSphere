
package com.flipkart.marketsphere.service;

import com.flipkart.marketsphere.dto.PaymentRequest;
import com.flipkart.marketsphere.dto.PaymentResponse;

public interface PaymentService {

	PaymentResponse processPayment(Long orderId, PaymentRequest request);

	PaymentResponse getPaymentByOrderId(Long orderId);
}
