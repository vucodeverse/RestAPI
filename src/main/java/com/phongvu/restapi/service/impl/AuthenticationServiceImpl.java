package com.phongvu.restapi.service.impl;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.phongvu.restapi.constraint.ErrorCode;
import com.phongvu.restapi.dto.request.AuthenticationRequest;
import com.phongvu.restapi.dto.request.IntrospectRequest;
import com.phongvu.restapi.dto.request.LogoutRequest;
import com.phongvu.restapi.dto.request.RefreshTokenRequest;
import com.phongvu.restapi.dto.response.AuthenticationResponse;
import com.phongvu.restapi.dto.response.IntrospectResponse;
import com.phongvu.restapi.model.InvalidatedToken;
import com.phongvu.restapi.model.User;
import com.phongvu.restapi.repository.InvalidatedTokenRepo;
import com.phongvu.restapi.repository.UserRepo;
import com.phongvu.restapi.service.AuthenticationService;
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
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final InvalidatedTokenRepo invalidatedTokenRepository;

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
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepo.findUserByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        boolean auth = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!auth) throw new AppException(ErrorCode.UNAUTHENTICATED);
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
//            user.getRoles().forEach(stringJoiner::add);
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
                    .expirationTime(Date.from(Instant.now().plus(validDuration, ChronoUnit.SECONDS)))
                    .jwtID(UUID.randomUUID().toString())
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

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(secretKey.getBytes(StandardCharsets.UTF_8));
        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = (isRefresh)
                ? new Date(signedJWT.getJWTClaimsSet().getIssueTime().toInstant().plus(refreshableDuration, ChronoUnit.SECONDS).toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);
        if (!(verified && expiryTime.after(new Date())))
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

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
    public AuthenticationResponse refreshToken(RefreshTokenRequest request) {
        try {
            var signedJWT = verifyToken(request.getToken(), true);
            String jwtId = signedJWT.getJWTClaimsSet().getJWTID();
            Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

            // Save token in Blacklist to vô hiệu hóa old token
            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(jwtId)
                    .expiryTime(expiryTime)
                    .build();
            invalidatedTokenRepository.save(invalidatedToken);

            var username = signedJWT.getJWTClaimsSet().getSubject();
            var user = userRepo.findUserByUsername(username)
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

            var token = genToken(user);
            return AuthenticationResponse.builder()
                    .token(token)
                    .isAuthenticated(true)
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
            String jwtId = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

            // Save token in Blacklist
            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(jwtId)
                    .expiryTime(expiryTime)
                    .build();
            invalidatedTokenRepository.save(invalidatedToken);
            log.info("Token đã được logout thành công!");
        } catch (AppException | JOSEException | ParseException e) {
            log.warn("Token already expired or invalid", e);
        }
    }
}
