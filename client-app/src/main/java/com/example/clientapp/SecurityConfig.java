package com.example.clientapp;

import java.util.List;
import java.util.Map;
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
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
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
						.defaultSuccessUrl("/dvd/list", true)
				);
		return http.build();
	}

	private OpenSaml4AuthenticationProvider getOpenSaml4AuthenticationProvider() {
		OpenSaml4AuthenticationProvider authenticationProvider = new OpenSaml4AuthenticationProvider();
		var delegate = OpenSaml4AuthenticationProvider.createDefaultResponseAuthenticationConverter();
		authenticationProvider.setResponseAuthenticationConverter((responseToken) -> {
			Saml2Authentication authentication = delegate.convert(responseToken);
			UserAdapter user = createAndRetrieve((Saml2AuthenticatedPrincipal) authentication.getPrincipal());
			return new Saml2Authentication(user, authentication.getSaml2Response(), authentication.getAuthorities());
		});
		return authenticationProvider;
	}

	private UserAdapter createAndRetrieve(Saml2AuthenticatedPrincipal principal) {
		return this.userRepository.findByEmail(principal.getName())
				.map((user) -> new UserAdapter(user, principal))
				.orElseGet(() -> {
					User user = new User();
					user.setEmail(principal.getName());
					user.setFirstName(principal.getFirstAttribute("firstName"));
					user.setLastName(principal.getFirstAttribute("lastName"));
					return new UserAdapter(this.userRepository.save(user), principal);
				});
	}

	private static class UserAdapter extends User implements Saml2AuthenticatedPrincipal {
		private final Saml2AuthenticatedPrincipal principal;

		private UserAdapter(User user, Saml2AuthenticatedPrincipal principal) {
			super(user);
			this.principal = principal;
		}

		@Override
		public String getName() {
			return this.principal.getName();
		}

		@Override
		public Map<String, List<Object>> getAttributes() {
			return this.principal.getAttributes();
		}

		@Override
		public String getRelyingPartyRegistrationId() {
			return this.principal.getRelyingPartyRegistrationId();
		}

		@Override
		public List<String> getSessionIndexes() {
			return this.principal.getSessionIndexes();
		}
	}

}
