
package com.flipkart.marketsphere.service.impl;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flipkart.marketsphere.dto.LoginRequest;
import com.flipkart.marketsphere.dto.LoginResponse;
import com.flipkart.marketsphere.entity.Cart;
import com.flipkart.marketsphere.entity.Role;
import com.flipkart.marketsphere.entity.User;
import com.flipkart.marketsphere.exception.EmailAlreadyExistsException;
import com.flipkart.marketsphere.exception.InvalidCredentialsException;
import com.flipkart.marketsphere.exception.UserNotFoundException;
import com.flipkart.marketsphere.repository.CartRepository;
import com.flipkart.marketsphere.repository.UserRepository;
import com.flipkart.marketsphere.service.JwtService;
import com.flipkart.marketsphere.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final CartRepository cartRepository;
	private final JwtService jwtService;

	public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
			CartRepository cartRepository, JwtService jwtService) {

		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.cartRepository = cartRepository;
		this.jwtService = jwtService;
	}

	// --------------------------------------------------
	// SECURITY HELPERS
	// --------------------------------------------------

	private String getCurrentUserEmail() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			throw new AccessDeniedException("User is not authenticated");
		}

		return authentication.getName();
	}

	private boolean isAdmin() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		return authentication != null && authentication.getAuthorities().stream()
				.anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
	}

	private void validateUserOwnership(User user) {

		if (isAdmin()) {
			return;
		}

		if (!user.getEmail().equalsIgnoreCase(getCurrentUserEmail())) {
			throw new AccessDeniedException("You cannot access another user's account");
		}
	}

	// --------------------------------------------------
	// REGISTER USER
	// --------------------------------------------------

	@Override
	@Transactional
	public User registerUser(User user) {

		if (userRepository.existsByEmail(user.getEmail())) {
			throw new EmailAlreadyExistsException("Email already registered: " + user.getEmail());
		}

		// Encode password
		user.setPassword(passwordEncoder.encode(user.getPassword()));

		// Always assign CUSTOMER role during registration
		user.setRole(Role.CUSTOMER);

		// Save user first
		User savedUser = userRepository.save(user);

		// Automatically create cart for CUSTOMER
		Cart cart = Cart.builder().user(savedUser).build();

		cartRepository.save(cart);

		return savedUser;
	}

	// --------------------------------------------------
	// GET ALL USERS - ADMIN ONLY
	// --------------------------------------------------

	@Override
	@Transactional(readOnly = true)
	public List<User> getAllUsers() {

		if (!isAdmin()) {
			throw new AccessDeniedException("Only administrators can access all users");
		}

		return userRepository.findAll();
	}

	// --------------------------------------------------
	// GET USER BY ID
	// --------------------------------------------------

	@Override
	@Transactional(readOnly = true)
	public User getUserById(Long id) {

		User user = userRepository.findById(id)
				.orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

		validateUserOwnership(user);

		return user;
	}

	// --------------------------------------------------
	// UPDATE USER
	// --------------------------------------------------

	@Override
	@Transactional
	public User updateUser(Long id, User user) {

		User existingUser = userRepository.findById(id)
				.orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

		// Validate ownership
		validateUserOwnership(existingUser);

		// Validate email uniqueness
		if (!existingUser.getEmail().equalsIgnoreCase(user.getEmail())
				&& userRepository.existsByEmail(user.getEmail())) {

			throw new EmailAlreadyExistsException("Email already registered: " + user.getEmail());
		}

		// Update only permitted fields
		existingUser.setName(user.getName());
		existingUser.setEmail(user.getEmail());
		existingUser.setPhone(user.getPhone());

		// Role and password cannot be changed through this endpoint

		return userRepository.save(existingUser);
	}

	// --------------------------------------------------
	// DELETE USER
	// --------------------------------------------------

	@Override
	@Transactional
	public void deleteUser(Long id) {

		User existingUser = userRepository.findById(id)
				.orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

		// Validate ownership
		validateUserOwnership(existingUser);

		// Delete customer cart first if present
		cartRepository.findByUserId(id).ifPresent(cartRepository::delete);

		userRepository.delete(existingUser);
	}

	// --------------------------------------------------
	// LOGIN USER
	// --------------------------------------------------

	@Override
	@Transactional(readOnly = true)
	public LoginResponse loginUser(LoginRequest request) {

		String email = request.getEmail().trim();

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {

			throw new InvalidCredentialsException("Invalid email or password");
		}

		String token = jwtService.generateToken(user.getEmail());

		return new LoginResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), token,
				"Login successful");
	}

}
