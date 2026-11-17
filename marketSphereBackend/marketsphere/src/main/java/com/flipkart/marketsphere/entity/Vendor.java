
package com.flipkart.marketsphere.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vendors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vendor {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 100)
	private String storeName;

	@Column(length = 500)
	private String description;

	@Column(nullable = false, length = 150)
	private String businessEmail;

	@Column(length = 15)
	private String businessPhone;

	@Column(length = 255)
	private String address;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private VendorStatus status;

	// Relationship with User
	@OneToOne
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;
}
