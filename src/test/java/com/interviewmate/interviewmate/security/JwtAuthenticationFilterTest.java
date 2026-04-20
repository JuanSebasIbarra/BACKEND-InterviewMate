package com.interviewmate.interviewmate.security;

import com.interviewmate.InterviewMate.security.JwtAuthenticationFilter;
import com.interviewmate.InterviewMate.security.JwtProvider;
import com.interviewmate.InterviewMate.security.oauth2.OAuth2Properties;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JwtAuthenticationFilterTest {

	private final JwtProvider jwtProvider = Mockito.mock(JwtProvider.class);
	private final UserDetailsService userDetailsService = Mockito.mock(UserDetailsService.class);
	private final FilterChain filterChain = Mockito.mock(FilterChain.class);

	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void authenticatesUsingAuthorizationHeader() throws Exception {
		OAuth2Properties properties = new OAuth2Properties();
		JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtProvider, userDetailsService);
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		request.addHeader("Authorization", "Bearer header-token");

		when(jwtProvider.validateToken("header-token")).thenReturn(true);
		when(jwtProvider.getUsernameFromToken("header-token")).thenReturn("alice");
		when(userDetailsService.loadUserByUsername("alice")).thenReturn(
				User.withUsername("alice").password("secret").authorities("ROLE_USER").build()
		);

		filter.doFilter(request, response, filterChain);

		assertNotNull(SecurityContextHolder.getContext().getAuthentication());
		assertEquals("alice", SecurityContextHolder.getContext().getAuthentication().getName());
		verify(filterChain).doFilter(request, response);
	}

	@Test
	void authenticatesUsingCookieWhenAuthorizationHeaderIsMissing() throws Exception {
		OAuth2Properties properties = new OAuth2Properties();
		properties.setCookieName("interviewmate_auth");
		JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtProvider, userDetailsService);
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		request.setCookies(new Cookie("interviewmate_auth", "cookie-token"));

		when(jwtProvider.validateToken("cookie-token")).thenReturn(true);
		when(jwtProvider.getUsernameFromToken("cookie-token")).thenReturn("bob");
		when(userDetailsService.loadUserByUsername("bob")).thenReturn(
				User.withUsername("bob").password("secret").authorities("ROLE_USER").build()
		);

		filter.doFilter(request, response, filterChain);

		assertNotNull(SecurityContextHolder.getContext().getAuthentication());
		assertEquals("bob", SecurityContextHolder.getContext().getAuthentication().getName());
		verify(filterChain).doFilter(request, response);
	}
}

