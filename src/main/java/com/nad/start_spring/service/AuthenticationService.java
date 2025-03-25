package com.nad.start_spring.service;

import com.nad.start_spring.dto.request.AuthenticationRequest;
import com.nad.start_spring.dto.request.IntrospectRequest;
import com.nad.start_spring.dto.request.LogoutRequest;
import com.nad.start_spring.dto.request.RefreshRequest;
import com.nad.start_spring.dto.response.AuthenticationResponse;
import com.nad.start_spring.dto.response.IntrospectResponse;
import com.nad.start_spring.entity.InvalidatedToken;
import com.nad.start_spring.entity.User;
import com.nad.start_spring.exception.AppException;
import com.nad.start_spring.exception.ErrorCode;
import com.nad.start_spring.repository.InvalidatedTokenRepository;
import com.nad.start_spring.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;


@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);
    UserRepository userRepository;
    InvalidatedTokenRepository tokenRepository;
    private final InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGN_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    public IntrospectResponse introspect(IntrospectRequest introspectRequest) throws JOSEException, ParseException {
        var token = introspectRequest.getToken();
        boolean isValid = true;
        try {
            verifyToken(token,false);
        }
        catch (AppException e){
            isValid = false;
        }
        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        var user = userRepository.findByUsername(authenticationRequest.getUsername()).orElseThrow(()
                -> new AppException(ErrorCode.USER_NOT_EXISTED));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean authenticate =  passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword());
        if (!authenticate) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        var token = createAuthenticationToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .success(true)
                .build();


    }
    public  AuthenticationResponse refreshToken(RefreshRequest refreshRequest) throws ParseException, JOSEException {
        SignedJWT signedJWT = verifyToken(refreshRequest.getToken(),true);

        var jid = signedJWT.getJWTClaimsSet().getJWTID();
        Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
        InvalidatedToken invalidatedToken = InvalidatedToken
                .builder()
                .id(jid)
                .expiryDate(expiration)
                .build();

        invalidatedTokenRepository.save(invalidatedToken);

        var username = signedJWT.getJWTClaimsSet().getSubject();
        var user = userRepository.findByUsername(username).orElseThrow(()
                -> new AppException(ErrorCode.UNAUTHENTICATED));

        var token = createAuthenticationToken(user);
        return AuthenticationResponse
                .builder()
                .token(token)
                .success(true)
                .build();
    }
    private SignedJWT verifyToken(String token, boolean isRefresh) throws ParseException, JOSEException {
        JWSVerifier verifier = new MACVerifier(SIGN_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expirationDate = isRefresh
                ? new Date(signedJWT.getJWTClaimsSet()
                .getIssueTime().toInstant().plus(REFRESHABLE_DURATION,ChronoUnit.SECONDS)
                .toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime()
        ;
        var verify = signedJWT.verify(verifier);
        if(!(verify && expirationDate.after(new Date()))) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        if(invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        return signedJWT;
    }

    public void logout(LogoutRequest logoutRequest) throws ParseException, JOSEException {
        try {
            var signToken = verifyToken(logoutRequest.getToken(), true);
            String jid = signToken.getJWTClaimsSet().getJWTID();
            Date expirationDate = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken = InvalidatedToken
                    .builder()
                    .id(jid)
                    .expiryDate(expirationDate)
                    .build();

            invalidatedTokenRepository.save(invalidatedToken);
        }
        catch (AppException e){
            log.error("Token already expired");
        }

    }
    public String createAuthenticationToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("nad")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .claim("scope",buildScope(user))
                .jwtID(String.valueOf(UUID.randomUUID()))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header,payload);

        try {
            jwsObject.sign(new MACSigner(SIGN_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot createAuthentication Token",e);
            throw new RuntimeException(e);
        }

    }
    private String buildScope(User user){
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles())){
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if(!CollectionUtils.isEmpty(role.getPermissions())){
                    role.getPermissions().forEach(permission -> {
                        stringJoiner.add(permission.getName());
                    });
                }
            });
        }
        return stringJoiner.toString();
    }
}
