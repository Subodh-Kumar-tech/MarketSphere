
package com.flipkart.marketsphere.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.flipkart.marketsphere.entity.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

	private Long paymentId;
	private String transactionId;
	private Long orderId;
	private String orderNumber;
	private BigDecimal amount;
	private PaymentStatus status;
	private String paymentMethod;
	private LocalDateTime paymentDate;
	private String message;
}
