package com.devmam.zalomini.utils;

import com.devmam.zalomini.constant.ConstantVariables;
import com.devmam.zalomini.entities.Role;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;
import java.util.Set;
import java.util.StringJoiner;

@Component
public class JwtUtil {
    public String generateToken(Long id, String email, Set<Role> roles) {
        JWSHeader jwtHeader = new JWSHeader(JWSAlgorithm.HS256);
        Date now = new Date();
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(email)
                .issuer("VSLearn")
                .issueTime(now)
                .expirationTime(new Date(now.getTime() + ConstantVariables.KEY_TIME_OUT))
                .claim("id", id)
                .claim("scope", buildScope(roles))
                .build();
        Payload jwtPayload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwtHeader, jwtPayload);
        try {
            jwsObject.sign(new MACSigner(ConstantVariables.SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    public JWTClaimsSet getClaimsFromToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public String buildScope(Set<Role> roles) {
        StringJoiner scopeJoiner = new StringJoiner(" ");
        roles.forEach(role -> {
            scopeJoiner.add(role.getName());
        });
        return scopeJoiner.toString();
    }

    public String getTokenFromAuthHeader(String authHeader) {
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);
    }
}
