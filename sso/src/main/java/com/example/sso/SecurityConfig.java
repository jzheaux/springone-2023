package com.example.sso;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
		OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
		OAuth2AuthorizationServerConfigurer configurer = http.getConfigurer(OAuth2AuthorizationServerConfigurer.class);
		configurer.oidc(withDefaults());
		http
				.oauth2ResourceServer((oauth2) -> oauth2
						.jwt(withDefaults())
				)
				.exceptionHandling((exceptions) -> exceptions
						.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
				);
		return http.build();
	}

	@Bean
	@Order(1)
	SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests((authorize) -> authorize.anyRequest().authenticated())
				.saml2Login(withDefaults())
				.saml2Metadata(withDefaults())
				.saml2Logout(withDefaults())
				.logout((logout) -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout")));
		return http.build();
	}

	@Bean
	OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer() {
		return (context) -> {
			if (context.getPrincipal() instanceof Saml2Authentication saml2Authentication) {
				Saml2AuthenticatedPrincipal principal = (Saml2AuthenticatedPrincipal) saml2Authentication.getPrincipal();
				context.getClaims().claim("firstName", principal.getFirstAttribute("firstName"));
				context.getClaims().claim("lastName", principal.getFirstAttribute("lastName"));
				context.getClaims().claim("email", principal.getFirstAttribute("email"));
			}
		};
	}

}
