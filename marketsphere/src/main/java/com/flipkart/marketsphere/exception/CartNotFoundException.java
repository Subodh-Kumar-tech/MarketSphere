package com.flipkart.marketsphere.exception;

public class CartNotFoundException extends RuntimeException {

	public CartNotFoundException(String message) {
		super(message);
	}
}