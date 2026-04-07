package com.interviewmate.InterviewMate.security.oauth2;

import com.interviewmate.InterviewMate.repository.UserRepository;
import com.interviewmate.InterviewMate.security.JwtProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Value("${app.oauth2.authorized-redirect-uri:http://localhost:3000/dashboard}")
    private String postLoginRedirectUri;

    @Value("${app.oauth2.cookie-name:interviewmate_auth}")
    private String authCookieName;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        String email = oauth2User.getAttribute("email");

        if (email == null || email.isBlank()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "OAuth2 email not available");
            return;
        }

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("OAuth2 user was not provisioned correctly"));

        String token = jwtProvider.generateToken(user.getUsername());
        ResponseCookie authCookie = ResponseCookie.from(authCookieName, token)
                .path("/")
                .sameSite("Lax")
                .secure(request.isSecure())
                .httpOnly(false)
                .maxAge(Duration.ofMillis(jwtProvider.getValidityInMillis()))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, authCookie.toString());

        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, postLoginRedirectUri);
    }
}


