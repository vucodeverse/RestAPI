package com.phongvu.restapi.service.impl;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.phongvu.restapi.constraint.ErrorCode;
import com.phongvu.restapi.dto.request.*;
import com.phongvu.restapi.dto.response.AuthenticationResponse;
import com.phongvu.restapi.dto.response.IntrospectResponse;
import com.phongvu.restapi.model.User;
import com.phongvu.restapi.model.UserSession;
import com.phongvu.restapi.model.PasswordResetToken;
import com.phongvu.restapi.repository.PasswordResetTokenRepository;
import com.phongvu.restapi.repository.UserRepository;
import com.phongvu.restapi.repository.UserSessionRepository;
import com.phongvu.restapi.service.AuthenticationService;
import com.phongvu.restapi.service.MailService;
import com.phongvu.restapi.utils.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Base64;
import java.util.concurrent.TimeUnit;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserSessionRepository userSessionRepository;
    private final StringRedisTemplate redisTemplate;
    private final MailService mailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.valid-duration}")
    private long validDuration;

    @Value("${jwt.refreshable-duration}")
    private long refreshableDuration;

    /**
     * Authenticates a user using username and password.
     * <p>
     * The method finds the user, validates the password, and generates a JWT token.
     * </p>
     *
     * @param request the authentication request containing username and password
     * @return {@link AuthenticationResponse} containing JWT token and
     *         authentication status
     * @throws AppException if user not found or password is incorrect
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletRequest httpServletRequest) {
        var user = userRepository.findUserByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        boolean auth = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!auth) throw new AppException(ErrorCode.UNAUTHENTICATED);
        
        boolean isAdmin = user.getRoles().stream().anyMatch(r -> "ADMIN".equals(r.getName()));
        boolean mfaRequired = isAdmin || user.is2faEnabled() || user.getTotpSecret() != null;
        
        if (mfaRequired) {
            String tempToken = genPreAuthToken(user);
            return AuthenticationResponse
                    .builder()
                    .token(tempToken)
                    .authenticated(false)
                    .mfaRequired(true)
                    .build();
        }
        
        String sessionId = UUID.randomUUID().toString();
        var token = genToken(user, sessionId);
        
        String ipAddress = httpServletRequest.getRemoteAddr();
        String userAgent = httpServletRequest.getHeader("User-Agent");
        if (userAgent == null) userAgent = "Unknown Device";
        if (ipAddress == null) ipAddress = "0.0.0.0";

        UserSession session = UserSession.builder()
                .id(sessionId)
                .user(user)
                .deviceInfo(userAgent)
                .ipAddress(ipAddress)
                .isRevoked(false)
                .build();
        userSessionRepository.save(session);
        
        return AuthenticationResponse
                .builder()
                .token(token)
                .authenticated(true)
                .mfaRequired(false)
                .build();
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions())) {
                    role.getPermissions().forEach(permission -> stringJoiner.add(permission.getCode()));
                }
            });
        }
        return stringJoiner.toString();
    }

    /**
     * Generates a signed JWT token using HMAC SHA-256 algorithm.
     * <p>
     * Token contains: subject (username), issuer, issued time, expiration (30
     * minutes), and scope (roles).
     * </p>
     *
     * @param user the user object used as the subject of the token
     * @return a serialized JWT token string
     * @throws AppException if token generation fails
     */
    private String genToken(User user, String sessionId) {
        try {
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(user.getUsername())
                    .issuer("jwt.com")
                    .issueTime(new Date())
                    .expirationTime(Date.from(Instant.now().plus(validDuration, ChronoUnit.SECONDS)))
                    .jwtID(UUID.randomUUID().toString())
                    .claim("scope", buildScope(user))
                    .claim("session_id", sessionId)
                    .build();

            Payload payload = new Payload(claimsSet.toJSONObject());
            JWSObject jwsObject = new JWSObject(header, payload);
            jwsObject.sign(new MACSigner(secretKey.getBytes(StandardCharsets.UTF_8)));

            return jwsObject.serialize();

        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private String genPreAuthToken(User user) {
        try {
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(user.getUsername())
                    .issuer("jwt.com")
                    .issueTime(new Date())
                    .expirationTime(Date.from(Instant.now().plus(5, ChronoUnit.MINUTES)))
                    .jwtID(UUID.randomUUID().toString())
                    .claim("scope", "PRE_AUTH")
                    .build();

            Payload payload = new Payload(claimsSet.toJSONObject());
            JWSObject jwsObject = new JWSObject(header, payload);
            jwsObject.sign(new MACSigner(secretKey.getBytes(StandardCharsets.UTF_8)));

            return jwsObject.serialize();

        } catch (JOSEException e) {
            log.error("Cannot create pre-auth token", e);
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public String genToken(User user, HttpServletRequest httpServletRequest) {
        String sessionId = UUID.randomUUID().toString();
        String token = genToken(user, sessionId);
        
        String ipAddress = httpServletRequest.getRemoteAddr();
        String userAgent = httpServletRequest.getHeader("User-Agent");
        if (userAgent == null) userAgent = "Unknown Device";
        if (ipAddress == null) ipAddress = "0.0.0.0";
        
        UserSession session = UserSession.builder()
                .id(sessionId)
                .user(user)
                .deviceInfo(userAgent + " (Verified 2FA)")
                .ipAddress(ipAddress)
                .isRevoked(false)
                .build();
        userSessionRepository.save(session);
        return token;
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(secretKey.getBytes(StandardCharsets.UTF_8));
        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = (isRefresh)
                ? new Date(signedJWT.getJWTClaimsSet().getIssueTime().toInstant().plus(refreshableDuration, ChronoUnit.SECONDS).toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);
        if (!(verified && expiryTime.after(new Date())))
            throw new AppException(ErrorCode.UNAUTHENTICATED);
            
        String sessionId = signedJWT.getJWTClaimsSet().getStringClaim("session_id");
        if (sessionId != null && Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:session:" + sessionId))) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }

    /**
     * Validates a JWT token by verifying its signature and expiration time.
     *
     * @param request the introspection request containing JWT token
     * @return {@link IntrospectResponse} indicating whether the token is valid
     */
    public IntrospectResponse introspect(IntrospectRequest request) {
        var token = request.getToken();
        boolean isValid = true;
        try {
            verifyToken(token, false);
        } catch (AppException | JOSEException | ParseException e) {
            isValid = false;
        }
        return IntrospectResponse.builder().valid(isValid).build();
    }

    @Override
    public AuthenticationResponse refreshToken(RefreshTokenRequest request, HttpServletRequest httpServletRequest) {
        try {
            var signedJWT = verifyToken(request.getToken(), true);
            String oldSessionId = signedJWT.getJWTClaimsSet().getStringClaim("session_id");
            Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            
            // Đưa old session vào blacklist trên Redis
            if (oldSessionId != null) {
                long ttl = expiryTime.getTime() - new Date().getTime();
                if (ttl > 0) {
                    redisTemplate.opsForValue().set("blacklist:session:" + oldSessionId, "revoked", ttl, java.util.concurrent.TimeUnit.MILLISECONDS);
                }
                userSessionRepository.findById(oldSessionId).ifPresent(session -> {
                    session.setRevoked(true);
                    userSessionRepository.save(session);
                });
            }

            var username = signedJWT.getJWTClaimsSet().getSubject();
            var user = userRepository.findUserByUsername(username)
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

            String newSessionId = UUID.randomUUID().toString();
            var token = genToken(user, newSessionId);
            
            String ipAddress = httpServletRequest.getRemoteAddr();
            String userAgent = httpServletRequest.getHeader("User-Agent");
            if (userAgent == null) userAgent = "Unknown Device";
            if (ipAddress == null) ipAddress = "0.0.0.0";

            UserSession session = UserSession.builder()
                    .id(newSessionId)
                    .user(user)
                    .deviceInfo(userAgent + " (Refreshed)")
                    .ipAddress(ipAddress)
                    .isRevoked(false)
                    .build();
            userSessionRepository.save(session);
            
            return AuthenticationResponse.builder()
                    .token(token)
                    .authenticated(true)
                    .build();
        } catch (JOSEException | ParseException e) {
            log.error("Cannot refresh token", e);
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }

    @Override
    public void logout(LogoutRequest request) {
        try {
            var signToken = verifyToken(request.getToken(), true);
            String sessionId = signToken.getJWTClaimsSet().getStringClaim("session_id");
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            if (sessionId != null) {
                long ttl = expiryTime.getTime() - new Date().getTime();
                if (ttl > 0) {
                    redisTemplate.opsForValue().set("blacklist:session:" + sessionId, "revoked", ttl, TimeUnit.MILLISECONDS);
                }
                userSessionRepository.findById(sessionId).ifPresent(session -> {
                    session.setRevoked(true);
                    userSessionRepository.save(session);
                });
            }
            log.info("Token đã được logout thành công!");
        } catch (AppException | JOSEException | ParseException e) {
            log.warn("Token already expired or invalid", e);
        }
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new AppException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        String rawToken = UUID.randomUUID().toString();
        String hashedToken = hashToken(rawToken);

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .tokenHash(hashedToken)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .isUsed(false)
                .build();
        passwordResetTokenRepository.save(resetToken);

        mailService.sendResetPasswordEmail(user.getEmail(), rawToken);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        String hashedToken = hashToken(request.getToken());
        PasswordResetToken resetToken = passwordResetTokenRepository.findByTokenHashAndIsUsedFalse(hashedToken)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
    }
}
