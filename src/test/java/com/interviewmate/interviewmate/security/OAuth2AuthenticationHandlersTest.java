package com.interviewmate.interviewmate.security;

import com.interviewmate.InterviewMate.entity.User;
import com.interviewmate.InterviewMate.repository.UserRepository;
import com.interviewmate.InterviewMate.security.JwtProvider;
import com.interviewmate.InterviewMate.security.oauth2.AuthCookieService;
import com.interviewmate.InterviewMate.security.oauth2.OAuth2AuthenticationFailureHandler;
import com.interviewmate.InterviewMate.security.oauth2.OAuth2AuthenticationSuccessHandler;
import com.interviewmate.InterviewMate.security.oauth2.OAuth2Properties;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class OAuth2AuthenticationHandlersTest {

	@Test
	void successHandlerSetsConfiguredCookieAndRedirectsToFrontend() throws Exception {
		JwtProvider jwtProvider = Mockito.mock(JwtProvider.class);
		UserRepository userRepository = Mockito.mock(UserRepository.class);
		OAuth2Properties properties = buildProperties();
		AuthCookieService authCookieService = new AuthCookieService(properties);
		OAuth2AuthenticationSuccessHandler handler = new OAuth2AuthenticationSuccessHandler(
				jwtProvider,
				userRepository,
				properties,
				authCookieService
		);

		User user = User.builder()
				.id(1L)
				.username("alice")
				.email("alice@example.com")
				.password("secret")
				.roles(Set.of("ROLE_USER"))
				.createdAt(Instant.now())
				.build();

		when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));
		when(jwtProvider.generateToken("alice")).thenReturn("jwt-token");
		when(jwtProvider.getValidityInMillis()).thenReturn(3_600_000L);

		DefaultOAuth2User principal = new DefaultOAuth2User(Set.of(), Map.of("email", "alice@example.com"), "email");
		var authentication = new TestingAuthenticationToken(principal, "n/a");
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();

		handler.onAuthenticationSuccess(request, response, authentication);

		String setCookie = response.getHeader("Set-Cookie");
		assertNotNull(setCookie);
		assertTrue(setCookie.contains("interviewmate_auth=jwt-token"));
		assertTrue(setCookie.contains("SameSite=None"));
		assertTrue(setCookie.contains("Secure"));
		assertTrue(setCookie.contains("HttpOnly"));
		assertEquals("https://frontend.example.com/dashboard", response.getRedirectedUrl());
	}

	@Test
	void failureHandlerClearsCookieAndRedirectsWithError() throws Exception {
		OAuth2Properties properties = buildProperties();
		AuthCookieService authCookieService = new AuthCookieService(properties);
		OAuth2AuthenticationFailureHandler handler = new OAuth2AuthenticationFailureHandler(properties, authCookieService);

		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		AuthenticationException exception = new AuthenticationException("google oauth failed") {};

		handler.onAuthenticationFailure(request, response, exception);

		String setCookie = response.getHeader("Set-Cookie");
		String redirectedUrl = response.getRedirectedUrl();
		assertNotNull(setCookie);
		assertNotNull(redirectedUrl);
		assertTrue(setCookie.contains("interviewmate_auth="));
		assertTrue(setCookie.contains("Max-Age=0"));
		assertTrue(setCookie.contains("SameSite=None"));
		assertTrue(redirectedUrl.startsWith("https://frontend.example.com/login"));
		assertTrue(redirectedUrl.contains("error=oauth2_authentication_failed"));
	}

	private OAuth2Properties buildProperties() {
		OAuth2Properties properties = new OAuth2Properties();
		properties.setAuthorizedRedirectUri("https://frontend.example.com/dashboard");
		properties.setErrorRedirectUri("https://frontend.example.com/login");
		properties.setCookieName("interviewmate_auth");
		properties.setCookieSameSite("None");
		properties.setCookieSecure(true);
		properties.setCookieHttpOnly(true);
		return properties;
	}
}

