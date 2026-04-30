package com.middle_bucket.middlebucket.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;

@Component
public class JwtUtil {

    private final byte[] signingKey;
    private final long expiration;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") long expiration) {
        this.signingKey = secret.getBytes();
        this.expiration = expiration;
    }

    public String generateToken(String email) {
        try {
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(email)
                    .issueTime(new Date())
                    .expirationTime(new Date(System.currentTimeMillis() + expiration))
                    .build();

            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS256),
                    claimsSet
            );
            signedJWT.sign(new MACSigner(signingKey));

            return signedJWT.serialize();

        } catch (JOSEException e) {
            throw new RuntimeException("Error creating JWT token", e);
        }
    }

    public boolean validateToken(String token) {
        try {
            SignedJWT signedJWT = parseAndVerify(token);
            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            return expirationTime != null && expirationTime.after(new Date());

        } catch (ParseException | JOSEException e) {
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        try {
            SignedJWT signedJWT = parseAndVerify(token);
            return signedJWT.getJWTClaimsSet().getSubject();

        } catch (ParseException | JOSEException e) {
            return null;
        }
    }
    private SignedJWT parseAndVerify(String token) throws ParseException, JOSEException {
        if (token == null || token.isBlank()) {
            throw new ParseException("Token kosong", 0);
        }

        SignedJWT signedJWT = SignedJWT.parse(token);
        JWSVerifier verifier = new MACVerifier(signingKey);

        if (!signedJWT.verify(verifier)) {
            throw new JOSEException("Signature token tidak valid");
        }

        return signedJWT;
    }
}