package com.example.clientapp;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
	private final Iterable<RelyingPartyRegistration> registrations;

	public UserController(RelyingPartyRegistrationRepository registrations) {
		this.registrations = (Iterable<RelyingPartyRegistration>) registrations;
	}

	@GetMapping("/login")
	String loginPage(Model model) {
		List<RelyingPartyRegistration> registrations = new ArrayList<>();
		this.registrations.forEach(registrations::add);
		model.addAttribute("sources", registrations);
		return "login";
	}
}
