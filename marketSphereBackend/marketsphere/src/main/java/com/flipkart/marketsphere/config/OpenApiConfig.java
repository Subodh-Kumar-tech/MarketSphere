
package com.flipkart.marketsphere.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI marketSphereOpenAPI() {

		return new OpenAPI().info(new Info().title("MarketSphere API")
				.description("REST API documentation for MarketSphere, " + "a multi-vendor e-commerce marketplace "
						+ "built using Spring Boot and Spring Data JPA.")
				.version("1.0.0")
				.contact(new Contact().name("MarketSphere Development Team").email("support@marketsphere.com"))
				.license(new License().name("MIT License")));
	}
}
