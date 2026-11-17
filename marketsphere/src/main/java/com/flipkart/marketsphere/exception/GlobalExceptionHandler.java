
package com.flipkart.marketsphere.exception;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.flipkart.marketsphere.dto.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	// USER NOT FOUND
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ApiResponse<Object>> handleUserNotFound(UserNotFoundException ex) {

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ex.getMessage(), null));
	}

	// PRODUCT NOT FOUND
	@ExceptionHandler(ProductNotFoundException.class)
	public ResponseEntity<ApiResponse<Object>> handleProductNotFound(ProductNotFoundException ex) {

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ex.getMessage(), null));
	}

	// CART NOT FOUND
	@ExceptionHandler(CartNotFoundException.class)
	public ResponseEntity<ApiResponse<Object>> handleCartNotFound(CartNotFoundException ex) {

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ex.getMessage(), null));
	}

	// EMAIL ALREADY EXISTS
	@ExceptionHandler(EmailAlreadyExistsException.class)
	public ResponseEntity<ApiResponse<Object>> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {

		return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(ex.getMessage(), null));
	}

	// PRODUCT ALREADY EXISTS
	@ExceptionHandler(ProductAlreadyExistsException.class)
	public ResponseEntity<ApiResponse<Object>> handleProductAlreadyExists(ProductAlreadyExistsException ex) {

		return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(ex.getMessage(), null));
	}

	// INVALID LOGIN CREDENTIALS
	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<ApiResponse<Object>> handleInvalidCredentials(InvalidCredentialsException ex) {

		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(ex.getMessage(), null));
	}

	// ACCESS DENIED
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiResponse<Object>> handleAccessDenied(AccessDeniedException ex) {

		return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body(ApiResponse.error("You do not have permission to access this resource", null));
	}

	// INVALID ORDER STATUS
	@ExceptionHandler(InvalidOrderStatusException.class)
	public ResponseEntity<ApiResponse<Object>> handleInvalidOrderStatus(InvalidOrderStatusException ex) {

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ex.getMessage(), null));
	}

	// INSUFFICIENT STOCK
	@ExceptionHandler(InsufficientStockException.class)
	public ResponseEntity<ApiResponse<Object>> handleInsufficientStock(InsufficientStockException ex) {

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ex.getMessage(), null));
	}

	// EMPTY CART
	@ExceptionHandler(CartEmptyException.class)
	public ResponseEntity<ApiResponse<Object>> handleCartEmpty(CartEmptyException ex) {

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ex.getMessage(), null));
	}

	// VALIDATION ERRORS
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Object>> handleValidationErrors(MethodArgumentNotValidException ex) {

		Map<String, String> validationErrors = new HashMap<>();

		ex.getBindingResult().getFieldErrors()
				.forEach(error -> validationErrors.put(error.getField(), error.getDefaultMessage()));

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ApiResponse.error("Validation failed", validationErrors));
	}

	// INVALID JSON / REQUEST BODY
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiResponse<Object>> handleInvalidRequestBody(HttpMessageNotReadableException ex) {

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ApiResponse.error("Invalid request body or JSON format", null));
	}

	// MISSING REQUEST PARAMETER
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ApiResponse<Object>> handleMissingParameter(MissingServletRequestParameterException ex) {

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ApiResponse.error("Missing required parameter: " + ex.getParameterName(), null));
	}

	// INVALID PARAMETER TYPE
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiResponse<Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ApiResponse.error("Invalid value for parameter: " + ex.getName(), null));
	}

	// DATABASE CONSTRAINT VIOLATION
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ApiResponse<Object>> handleDatabaseConstraint(DataIntegrityViolationException ex) {

		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(ApiResponse.error("Database constraint violation. Check duplicate or related data.", null));
	}

	// INVALID ARGUMENT
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiResponse<Object>> handleIllegalArgument(IllegalArgumentException ex) {

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(ex.getMessage(), null));
	}

	// GENERAL EXCEPTION
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception ex) {

		logger.error("Unexpected application error", ex);

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ApiResponse.error("An unexpected error occurred", null));
	}
}
