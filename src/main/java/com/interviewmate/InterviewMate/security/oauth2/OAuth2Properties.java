package com.interviewmate.InterviewMate.security.oauth2;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.oauth2")
public class OAuth2Properties {

	private String authorizedRedirectUri = "http://localhost:5173/dashboard";
	private String errorRedirectUri = "http://localhost:5173/login";
	private String cookieName = "interviewmate_auth";
	private String cookieSameSite = "Lax";
	private boolean cookieSecure = false;
	private boolean cookieHttpOnly = false;
	private String cookiePath = "/";

	public String getAuthorizedRedirectUri() {
		return authorizedRedirectUri;
	}

	public void setAuthorizedRedirectUri(String authorizedRedirectUri) {
		this.authorizedRedirectUri = authorizedRedirectUri;
	}

	public String getErrorRedirectUri() {
		return errorRedirectUri;
	}

	public void setErrorRedirectUri(String errorRedirectUri) {
		this.errorRedirectUri = errorRedirectUri;
	}

	public String getCookieName() {
		return cookieName;
	}

	public void setCookieName(String cookieName) {
		this.cookieName = cookieName;
	}

	public String getCookieSameSite() {
		return cookieSameSite;
	}

	public void setCookieSameSite(String cookieSameSite) {
		this.cookieSameSite = cookieSameSite;
	}

	public boolean isCookieSecure() {
		return cookieSecure;
	}

	public void setCookieSecure(boolean cookieSecure) {
		this.cookieSecure = cookieSecure;
	}

	public boolean isCookieHttpOnly() {
		return cookieHttpOnly;
	}

	public void setCookieHttpOnly(boolean cookieHttpOnly) {
		this.cookieHttpOnly = cookieHttpOnly;
	}

	public String getCookiePath() {
		return cookiePath;
	}

	public void setCookiePath(String cookiePath) {
		this.cookiePath = cookiePath;
	}
}
