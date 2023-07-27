package com.example.sso;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcLogoutAuthenticationToken;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

public class PostLogoutRedirectUriLogoutSuccessHandler implements LogoutSuccessHandler {

	private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		if (!(authentication instanceof OidcLogoutAuthenticationToken oidcLogoutAuthentication)) {
			return;
		}
		if (oidcLogoutAuthentication.isAuthenticated() &&
				StringUtils.hasText(oidcLogoutAuthentication.getPostLogoutRedirectUri())) {
			// Perform post-logout redirect
			UriComponentsBuilder uriBuilder = UriComponentsBuilder
					.fromUriString(oidcLogoutAuthentication.getPostLogoutRedirectUri());
			String redirectUri;
			if (StringUtils.hasText(oidcLogoutAuthentication.getState())) {
				uriBuilder.queryParam(
						OAuth2ParameterNames.STATE,
						UriUtils.encode(oidcLogoutAuthentication.getState(), StandardCharsets.UTF_8));
			}
			redirectUri = uriBuilder.build(true).toUriString();		// build(true) -> Components are explicitly encoded
			this.redirectStrategy.sendRedirect(request, response, redirectUri);
		} else {
			// Perform default redirect
			this.redirectStrategy.sendRedirect(request, response, "/");
		}
	}

}
