package com.example.clientapp;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

@Component
public class OidcUserToUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

	private final UserRepository userRepository;

	private final OidcUserService delegate = new OidcUserService();

	public OidcUserToUserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
		OidcUser oidcUser = this.delegate.loadUser(userRequest);
		User user = new User();
		user.setEmail(oidcUser.getEmail());
		user.setFirstName(oidcUser.getGivenName());
		user.setLastName(oidcUser.getFamilyName());
		return new UserAdapter(this.userRepository.save(user), oidcUser);
	}

	private static class UserAdapter extends User implements OidcUser {

		private final OidcUser principal;

		private UserAdapter(User user, OidcUser principal) {
			super(user);
			this.principal = principal;
		}


		@Override
		public Map<String, Object> getClaims() {
			return this.principal.getClaims();
		}

		@Override
		public OidcUserInfo getUserInfo() {
			return this.principal.getUserInfo();
		}

		@Override
		public OidcIdToken getIdToken() {
			return this.principal.getIdToken();
		}

		@Override
		public Map<String, Object> getAttributes() {
			return this.principal.getAttributes();
		}

		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			return this.principal.getAuthorities();
		}

		@Override
		public String getName() {
			return this.principal.getName();
		}

	}

}
