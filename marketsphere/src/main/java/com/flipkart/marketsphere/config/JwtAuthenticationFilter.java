
package com.flipkart.marketsphere.config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.flipkart.marketsphere.service.CustomUserDetailsService;
import com.flipkart.marketsphere.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final CustomUserDetailsService userDetailsService;

	public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {

		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
	}

	// --------------------------------------------------
	// JWT AUTHENTICATION FILTER
	// --------------------------------------------------

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		final String authorizationHeader = request.getHeader("Authorization");

		String email = null;
		String token = null;

		// --------------------------------------------------
		// 1. EXTRACT JWT TOKEN
		// --------------------------------------------------

		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {

			token = authorizationHeader.substring(7).trim();

			try {

				email = jwtService.extractEmail(token);

			} catch (Exception exception) {

				// Invalid token
				SecurityContextHolder.clearContext();
			}
		}

		// --------------------------------------------------
		// 2. AUTHENTICATE USER
		// --------------------------------------------------

		if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

			try {

				// Load user from database
				UserDetails userDetails = userDetailsService.loadUserByUsername(email);

				// Validate JWT
				if (jwtService.isTokenValid(token, userDetails.getUsername())) {

					// Create authentication object
					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());

					// Add request details
					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

					// Store authentication
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}

			} catch (Exception exception) {

				// Invalid token or user not found
				SecurityContextHolder.clearContext();
			}
		}

		// --------------------------------------------------
		// 3. CONTINUE FILTER CHAIN
		// --------------------------------------------------

		filterChain.doFilter(request, response);
	}
}
