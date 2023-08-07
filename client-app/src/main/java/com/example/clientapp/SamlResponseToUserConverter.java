package com.example.clientapp;

import java.util.List;
import java.util.Map;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider.ResponseToken;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.stereotype.Component;

@Component
public class SamlResponseToUserConverter implements Converter<ResponseToken, Saml2Authentication> {

	private final UserRepository userRepository;

	private final Converter<ResponseToken, Saml2Authentication> delegate = OpenSaml4AuthenticationProvider.createDefaultResponseAuthenticationConverter();

	public SamlResponseToUserConverter(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public Saml2Authentication convert(ResponseToken source) {
		Saml2Authentication authentication = this.delegate.convert(source);
		UserAdapter user = createAndRetrieve((Saml2AuthenticatedPrincipal) authentication.getPrincipal());
		return new Saml2Authentication(user, authentication.getSaml2Response(), authentication.getAuthorities());
	}

	private UserAdapter createAndRetrieve(Saml2AuthenticatedPrincipal principal) {
		String email = principal.getFirstAttribute("email");
		return this.userRepository.findByEmail(email)
				.map((user) -> new UserAdapter(user, principal))
				.orElseGet(() -> {
					User user = new User();
					user.setEmail(email);
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
