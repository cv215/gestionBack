package org.gestion.gestionstock.config;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecurityController {

    private final AuthenticationManager authenticationManager;

    private final JwtEncoder jwtEncoder;

    @Autowired
    public SecurityController(AuthenticationManager authenticationManager, JwtEncoder jwtEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
    }

    @GetMapping("/profile")
    public Authentication getProfile(Authentication authentication) {
        return authentication;
    }

    @PostMapping("/login")
    public Map<String, String> login(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        Instant instant = Instant.now();
        String scope = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(" "));
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .subject(username)
                .expiresAt(instant.plusSeconds(3600))
                .claim("scope", scope)
                .issuedAt(instant)
                .build();

        JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), jwtClaimsSet);
        String jwt = jwtEncoder.encode(jwtEncoderParameters).getTokenValue();
        return Map.of("token", jwt);
    }
}
