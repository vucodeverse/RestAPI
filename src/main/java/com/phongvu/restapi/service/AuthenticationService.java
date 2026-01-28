package com.phongvu.restapi.service;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.phongvu.restapi.constants.ApiMessage;
import com.phongvu.restapi.dto.request.AuthenticationRequest;
import com.phongvu.restapi.dto.request.IntrospectRequest;
import com.phongvu.restapi.dto.response.AuthenticationResponse;
import com.phongvu.restapi.dto.response.IntrospectResponse;
import com.phongvu.restapi.repository.UserRepo;
import com.phongvu.restapi.utils.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j // Automatically create a logger
@Service // Mark this as a Service layer in Spring
@RequiredArgsConstructor // Create your own constructor for final fields
public class AuthenticationService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    // Get the secret key from application.properties
    @Value("${jwt.secret}")
    private String secretKey;

    /**
     * Authenticates a user using username and password.
     * <p>The method performs the following steps:</p>
     * <ul>
     *  <li>Finds the user by username</li>
     *  <li>Validates the provided password against the stored hash</li>
     *  <li>Generates a JWT token if authentication succeeds</li>
     * </ul>
     * @param request request the authentication request containing username and password
     * @return {@link AuthenticationResponse} containing JWT token and authentication status
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // Find user by username
        // If user not exist -> throw an exception
        var user = userRepo.findUserByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ApiMessage.USER_NOT_FOUND));

        //Compare the password entered by the user with the hashed password in the database
        boolean auth = passwordEncoder.matches(request.getPassword(), user.getPassword());

        //If password not true -> throw exception
        if (!auth) {
            throw new AppException(ApiMessage.UNAUTHENTICATED);
        }

        //Create JWT if authentication success
        var token = genToken(request.getUsername());

        //Return response authentication success
        return AuthenticationResponse
                .builder()
                .token(token)
                .isAuthenticated(true)
                .build();
    }


    /**
     * Generates a signed JWT token using HMAC SHA-256 algorithm.
     *
     * <p>The generated token contains:</p>
     * <ul>
     *   <li>Subject: username</li>
     *   <li>Issuer</li>
     *   <li>Issued time</li>
     *   <li>Expiration time (5 minutes)</li>
     * </ul>
     *
     * @param username the username used as the subject of the token
     * @return a serialized JWT token string
     *
     * @throws AppException if token generation fails
     */
    private String genToken(String  username) {
        try {
            // Header of JWT
            // HS256 = HMAC SHA-256 (symmetric key)
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

            // Claims (payload) of JWT
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(username)      // sub: identifier user
                    .issuer("jwt.com")      // iss: issuing party token
                    .issueTime(new Date())  // iat: creation time token
                    .expirationTime(        // exp: expiration time
                            Date.from(Instant.now().plus(5, ChronoUnit.MINUTES))
                    )
                    .claim("customClaim", "Custom")
                    .build();

            // Convert claims -> payload JSON
            Payload payload = new Payload(claimsSet.toJSONObject());

            // Create JWS Object (Header + Payload)
            JWSObject jwsObject = new JWSObject(header, payload);

            // Sign token using the secret key
            jwsObject.sign(new MACSigner(secretKey.getBytes(StandardCharsets.UTF_8)));

            //Serialize tokens to String format
            return jwsObject.serialize();

        } catch (JOSEException e) {
            //Log error when unable to create token
            log.error("Cannot create token", e);
            // Throw internal exception
            throw new AppException(ApiMessage.INTERNAL_SERVER_ERROR);
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
            // Extract JWT token from request
            String token = request.getToken();

            // Reject immediately if token is missing or empty
            if (token == null || token.isBlank()) {
                return IntrospectResponse.builder()
                        .valid(false)
                        .build();
            }

            // Parse the serialized JWT string into a SignedJWT object
            SignedJWT signedJWT = SignedJWT.parse(token);

            // Ensure algorithm consistency with token generation
            if (!JWSAlgorithm.HS256.equals(
                    signedJWT.getHeader().getAlgorithm())) {
                return IntrospectResponse.builder()
                        .valid(false)
                        .build();
            }

            // Create verifier using shared secret key (HMAC)
            JWSVerifier jwsVerifier = new MACVerifier(secretKey.getBytes(StandardCharsets.UTF_8));

            // Extract expiration time from token claims
            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();

            // Token is considered valid only if it has not expired
            boolean notExpired = expirationTime != null
                    && expirationTime.after(new Date());

            // Verify JWT signature to ensure token integrity
            boolean signatureValid = signedJWT.verify(jwsVerifier);

            // Token is valid only when signature is correct and token is not expired
            return IntrospectResponse.builder()
                    .valid(signatureValid && notExpired)
                    .build();
        } catch (JOSEException | ParseException e) {
            // Any parsing or verification error means the token is invalid
            log.warn("Invalid JWT token", e);
            return IntrospectResponse.builder()
                    .valid(false)
                    .build();
        }
    }
}
