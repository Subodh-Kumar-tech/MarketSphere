
package com.flipkart.marketsphere.service;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private final SecretKey secretKey;
	private final long expiration;

	public JwtService(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") long expiration) {

		this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));

		this.expiration = expiration;
	}

	// --------------------------------------------------
	// GENERATE JWT TOKEN
	// --------------------------------------------------

	public String generateToken(String email) {

		Date currentDate = new Date();

		Date expirationDate = new Date(currentDate.getTime() + expiration);

		return Jwts.builder().subject(email).issuedAt(currentDate).expiration(expirationDate).signWith(secretKey)
				.compact();
	}

	// --------------------------------------------------
	// EXTRACT EMAIL FROM JWT
	// --------------------------------------------------

	public String extractEmail(String token) {

		return extractAllClaims(token).getSubject();
	}

	// --------------------------------------------------
	// VALIDATE JWT TOKEN
	// --------------------------------------------------

	public boolean isTokenValid(String token, String email) {

		try {

			String tokenEmail = extractEmail(token);

			return tokenEmail != null && tokenEmail.equals(email) && !isTokenExpired(token);

		} catch (JwtException | IllegalArgumentException exception) {

			// Invalid, expired, or malformed JWT
			return false;
		}
	}

	// --------------------------------------------------
	// CHECK TOKEN EXPIRATION
	// --------------------------------------------------

	private boolean isTokenExpired(String token) {

		Date expirationDate = extractAllClaims(token).getExpiration();

		return expirationDate == null || expirationDate.before(new Date());
	}

	// --------------------------------------------------
	// EXTRACT CLAIMS AND VERIFY SIGNATURE
	// --------------------------------------------------

	private Claims extractAllClaims(String token) {

		return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
	}
}
