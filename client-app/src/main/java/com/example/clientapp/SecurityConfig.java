package com.example.clientapp;

import java.util.Map;

import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientPropertiesMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	SecurityFilterChain appSecurity(HttpSecurity http, LogoutSuccessHandler handler) throws Exception {
		http
				.authorizeHttpRequests((authorize) -> authorize
						.requestMatchers("/css/**", "/images/**").permitAll()
						.anyRequest().authenticated()
				)
				.oauth2Login(Customizer.withDefaults())
				.logout((logout) -> logout.logoutSuccessHandler(handler));
		return http.build();
	}

	@Bean
	LogoutSuccessHandler logoutSuccessHandler(ClientRegistrationRepository registrations) {
		return new OidcClientInitiatedLogoutSuccessHandler(registrations);
	}

	@Bean
	ClientRegistrationRepository registrations(OAuth2ClientProperties properties) {
		ClientRegistration registration = new OAuth2ClientPropertiesMapper(properties)
				.asClientRegistrations().get("client-app");
		registration = ClientRegistration.withClientRegistration(registration).providerConfigurationMetadata(
				Map.of("end_session_endpoint", "http://auth-server:9000/logout")).build();
		return new InMemoryClientRegistrationRepository(registration);
	}
}
