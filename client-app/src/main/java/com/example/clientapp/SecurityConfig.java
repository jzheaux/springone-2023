package com.example.clientapp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	SecurityFilterChain appSecurity(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests((authorize) -> authorize
						.anyRequest().authenticated()
				)
				.saml2Login(Customizer.withDefaults());
		return http.build();
	}

	@Bean
	AuthenticationProvider openSaml4AuthenticationProvider(SamlResponseToUserConverter converter) {
		OpenSaml4AuthenticationProvider provider = new OpenSaml4AuthenticationProvider();
		provider.setResponseAuthenticationConverter(converter);
		return provider;
	}

}
