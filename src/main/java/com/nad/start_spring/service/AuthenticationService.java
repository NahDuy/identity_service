package com.nad.start_spring.service;

import com.nad.start_spring.dto.request.AuthenticationRequest;
import com.nad.start_spring.dto.request.IntrospectRequest;
import com.nad.start_spring.dto.response.AuthenticationResponse;
import com.nad.start_spring.dto.response.IntrospectResponse;
import com.nad.start_spring.entity.User;
import com.nad.start_spring.exception.AppException;
import com.nad.start_spring.exception.ErrorCode;
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


@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);
    UserRepository userRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGN_KEY;

    public IntrospectResponse introspect(IntrospectRequest introspectRequest) throws JOSEException, ParseException {
        var token = introspectRequest.getToken();
        JWSVerifier verifier = new MACVerifier(SIGN_KEY);
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
        Date expirationDate = claimsSet.getExpirationTime();
        var verify = signedJWT.verify(verifier);
        return IntrospectResponse.builder()
                .valid(verify && expirationDate.after(new Date()))
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
    public String createAuthenticationToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("nad")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()))
                .claim("scope",buildScope(user))
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
//        if (!CollectionUtils.isEmpty(user.getRoles())){
//            user.getRoles().forEach(stringJoiner::add);
//        }
        return stringJoiner.toString();
    }
}
