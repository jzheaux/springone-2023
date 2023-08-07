package com.example.clientapp;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.saml2.provider.service.authentication.DefaultSaml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	UserRepository userRepository;

	@Bean
	SecurityFilterChain appSecurity(HttpSecurity http) throws Exception {
		OpenSaml4AuthenticationProvider provider = getOpenSaml4AuthenticationProvider();
		http
				.authorizeHttpRequests((authorize) -> authorize
						.anyRequest().authenticated()
				)
				.saml2Login((saml2) -> saml2
						.authenticationManager(new ProviderManager(provider))
						.successHandler(new SimpleUrlAuthenticationSuccessHandler("/dvd/list"))
				);
		return http.build();
	}

	private OpenSaml4AuthenticationProvider getOpenSaml4AuthenticationProvider() {
		OpenSaml4AuthenticationProvider authenticationProvider = new OpenSaml4AuthenticationProvider();
		authenticationProvider.setResponseAuthenticationConverter(responseToken -> {
			Saml2Authentication authentication = OpenSaml4AuthenticationProvider
					.createDefaultResponseAuthenticationConverter()
					.convert(responseToken);
			User user = createAndRetrieve(authentication);
			return new MySaml2Authentication(user, authentication);
		});
		return authenticationProvider;
	}

	private User createAndRetrieve(Authentication authentication) {
		Optional<User> maybeUser = this.userRepository.findByEmail(authentication.getName());
		if (maybeUser.isPresent()) {
			return maybeUser.get();
		}
		User user = new User();
		user.setEmail(authentication.getName());
		if (authentication.getPrincipal() instanceof DefaultSaml2AuthenticatedPrincipal saml2Principal) {
			user.setFirstName(saml2Principal.getFirstAttribute("firstName"));
			user.setLastName(saml2Principal.getFirstAttribute("lastName"));
		}
		return this.userRepository.save(user);
	}

	static class MySaml2Authentication extends AbstractAuthenticationToken {

		private final User user;

		private final Saml2Authentication authentication;

		MySaml2Authentication(User user, Saml2Authentication authentication) {
			super(authentication.getAuthorities());
			this.user = user;
			this.authentication = authentication;
			setAuthenticated(true);
		}

		@Override
		public Object getCredentials() {
			return this.authentication.getCredentials();
		}

		@Override
		public Object getPrincipal() {
			return this.user;
		}

	}

}
