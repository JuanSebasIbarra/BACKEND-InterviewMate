package com.interviewmate.InterviewMate.security.oauth2;

import com.interviewmate.InterviewMate.entity.User;
import com.interviewmate.InterviewMate.enums.AuthProvider;
import com.interviewmate.InterviewMate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoogleOAuth2UserProvisioningService {

    private static final PasswordEncoder OAUTH_PASSWORD_ENCODER = new BCryptPasswordEncoder();

    private final UserRepository userRepository;

    public User upsertGoogleUser(Map<String, Object> attributes) {
        String email = asString(attributes.get("email"));
        String googleId = asString(attributes.get("sub"));
        Boolean emailVerified = asBoolean(attributes.get("email_verified"));

        if (email == null || email.isBlank()) {
            throw oauth2Error("Google did not return a valid email");
        }
        if (!Boolean.TRUE.equals(emailVerified)) {
            throw oauth2Error("Google account email is not verified");
        }
        if (googleId == null || googleId.isBlank()) {
            throw oauth2Error("Google did not return a valid user id");
        }

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            user = createNewUser(attributes, email, googleId);
        } else {
            // Prevent accidental account takeover when a different Google account is linked.
            if (user.getGoogleId() != null && !user.getGoogleId().equals(googleId)) {
                throw oauth2Error("Email already linked to a different Google account");
            }
            user.setGoogleId(googleId);
            user.setAuthProvider(AuthProvider.GOOGLE);
            user.setProfilePictureUrl(asString(attributes.get("picture")));
        }

        return userRepository.save(user);
    }

    private User createNewUser(Map<String, Object> attributes, String email, String googleId) {
        String preferredName = asString(attributes.get("given_name"));
        String usernameCandidate = (preferredName == null || preferredName.isBlank())
                ? email.substring(0, email.indexOf('@'))
                : preferredName;

        return User.builder()
                .username(generateUniqueUsername(usernameCandidate))
                .email(email)
                .password(OAUTH_PASSWORD_ENCODER.encode(UUID.randomUUID().toString()))
                .authProvider(AuthProvider.GOOGLE)
                .googleId(googleId)
                .profilePictureUrl(asString(attributes.get("picture")))
                .roles(Set.of("ROLE_USER"))
                .createdAt(Instant.now())
                .build();
    }

    private String generateUniqueUsername(String base) {
        String normalizedBase = base.toLowerCase().replaceAll("[^a-z0-9._-]", "");
        if (normalizedBase.isBlank()) {
            normalizedBase = "user";
        }

        String candidate = normalizedBase;
        int suffix = 1;
        while (userRepository.existsByUsername(candidate)) {
            candidate = normalizedBase + suffix;
            suffix++;
        }
        return candidate;
    }

    private OAuth2AuthenticationException oauth2Error(String message) {
        return new OAuth2AuthenticationException(new OAuth2Error("oauth2_authentication_error", message, null), message);
    }

    private String asString(Object value) {
        return value instanceof String string ? string : null;
    }

    private Boolean asBoolean(Object value) {
        return value instanceof Boolean bool ? bool : null;
    }
}



