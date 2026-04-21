package com.interviewmate.InterviewMate.security.oauth2;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class AuthCookieService {

	private final OAuth2Properties oAuth2Properties;

	public AuthCookieService(OAuth2Properties oAuth2Properties) {
		this.oAuth2Properties = oAuth2Properties;
	}

	public ResponseCookie buildAuthCookie(String token, Duration maxAge) {
		return ResponseCookie.from(oAuth2Properties.getCookieName(), token)
				.path(oAuth2Properties.getCookiePath())
				.sameSite(oAuth2Properties.getCookieSameSite())
				.secure(oAuth2Properties.isCookieSecure())
				.httpOnly(oAuth2Properties.isCookieHttpOnly())
				.maxAge(maxAge)
				.build();
	}

	public ResponseCookie buildClearCookie() {
		return ResponseCookie.from(oAuth2Properties.getCookieName(), "")
				.path(oAuth2Properties.getCookiePath())
				.sameSite(oAuth2Properties.getCookieSameSite())
				.secure(oAuth2Properties.isCookieSecure())
				.httpOnly(oAuth2Properties.isCookieHttpOnly())
				.maxAge(Duration.ZERO)
				.build();
	}
}
