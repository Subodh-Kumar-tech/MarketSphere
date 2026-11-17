
package com.flipkart.marketsphere.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final SecurityErrorHandler securityErrorHandler;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, SecurityErrorHandler securityErrorHandler) {

		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.securityErrorHandler = securityErrorHandler;
	}

	// --------------------------------------------------
	// SECURITY FILTER CHAIN
	// --------------------------------------------------

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.disable())

				.cors(Customizer.withDefaults())

				.httpBasic(basic -> basic.disable())

				.formLogin(form -> form.disable())

				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				// SECURITY ERROR HANDLING
				.exceptionHandling(exception -> exception.authenticationEntryPoint(securityErrorHandler)
						.accessDeniedHandler(securityErrorHandler))

				// AUTHORIZATION RULES
				.authorizeHttpRequests(auth -> auth

						// Swagger APIs
						.requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()

						// CORS preflight
						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

						// Authentication APIs
						.requestMatchers("/api/auth/**").permitAll()

						// Public registration
						.requestMatchers(HttpMethod.POST, "/api/users").permitAll()

						// Public product browsing
						.requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()

						// Public category browsing
						.requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()

						// Admin dashboard
						.requestMatchers("/api/admin/dashboard/**").hasRole("ADMIN")

						// Cart APIs
						.requestMatchers("/api/cart/**").hasRole("CUSTOMER")

						// Order APIs
						.requestMatchers("/api/orders/**").hasAnyRole("CUSTOMER", "ADMIN")

						// Payment APIs
						.requestMatchers("/api/payments/**").hasAnyRole("CUSTOMER", "ADMIN")

						// Vendor APIs
						.requestMatchers("/api/vendors/**").authenticated()

						// User APIs
						.requestMatchers("/api/users/**").authenticated()

						// Product APIs
						.requestMatchers("/api/products/**").authenticated()

						// Category APIs
						.requestMatchers("/api/categories/**").authenticated()

						// All remaining endpoints
						.anyRequest().authenticated());

		// JWT AUTHENTICATION FILTER
		http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	// --------------------------------------------------
	// CORS CONFIGURATION
	// --------------------------------------------------

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {

		CorsConfiguration configuration = new CorsConfiguration();

		configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000"));

		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

		configuration.setAllowedHeaders(List.of("*"));

		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		source.registerCorsConfiguration("/**", configuration);

		return source;
	}
}
