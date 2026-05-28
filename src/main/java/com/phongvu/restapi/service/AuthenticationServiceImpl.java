package com.phongvu.restapi.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.phongvu.restapi.constraint.ErrorCode;
import com.phongvu.restapi.dto.request.AuthenticationRequest;
import com.phongvu.restapi.dto.request.IntrospectRequest;
import com.phongvu.restapi.dto.response.AuthenticationResponse;
import com.phongvu.restapi.dto.response.IntrospectResponse;
import com.phongvu.restapi.model.User;
import com.phongvu.restapi.repository.UserRepo;
import com.phongvu.restapi.utils.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String secretKey;

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
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepo.findUserByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        boolean auth = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!auth) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        var token = genToken(user);

        return AuthenticationResponse
                .builder()
                .token(token)
                .isAuthenticated(true)
                .build();
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(stringJoiner::add);
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
    private String genToken(User user) {
        try {
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(user.getUsername())
                    .issuer("jwt.com")
                    .issueTime(new Date())
                    .expirationTime(
                            Date.from(Instant.now().plus(30, ChronoUnit.MINUTES)))
                    .claim("scope", buildScope(user))
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

    /**
     * Validates a JWT token by verifying its signature and expiration time.
     *
     * @param request the introspection request containing JWT token
     * @return {@link IntrospectResponse} indicating whether the token is valid
     */
    public IntrospectResponse introspect(IntrospectRequest request) {
        try {
            String token = request.getToken();

            if (token == null || token.isBlank()) {
                return IntrospectResponse.builder()
                        .valid(false)
                        .build();
            }

            SignedJWT signedJWT = SignedJWT.parse(token);

            if (!JWSAlgorithm.HS256.equals(
                    signedJWT.getHeader().getAlgorithm())) {
                return IntrospectResponse.builder()
                        .valid(false)
                        .build();
            }

            JWSVerifier jwsVerifier = new MACVerifier(secretKey.getBytes(StandardCharsets.UTF_8));

            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();

            boolean notExpired = expirationTime != null
                    && expirationTime.after(new Date());

            boolean signatureValid = signedJWT.verify(jwsVerifier);

            return IntrospectResponse.builder()
                    .valid(signatureValid && notExpired)
                    .build();
        } catch (JOSEException | ParseException e) {
            log.warn("Invalid JWT token", e);
            return IntrospectResponse.builder()
                    .valid(false)
                    .build();
        }
    }
}
