package org.gestion.gestionstock.controller;

import lombok.extern.slf4j.Slf4j;
import org.gestion.gestionstock.config.JetUtils;
import org.gestion.gestionstock.dto.auth.AuthentificationRequest;
import org.gestion.gestionstock.dto.auth.AuthentificationResponse;
import org.gestion.gestionstock.services.impl.ApplicationUserDetailsService;
import org.gestion.gestionstock.services.impl.UtilisateurServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import static org.gestion.gestionstock.utils.Constants.APP_ROOT;

@Slf4j
@RestController
@RequestMapping(APP_ROOT + "/auth")

public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ApplicationUserDetailsService applicationUserDetailsService;

    @Autowired
    private UtilisateurServiceImpl utilisateurService;

    @Autowired
    private JetUtils jetUtils;

    @PostMapping("/connexion")
    public ResponseEntity<AuthentificationResponse> connexion(@RequestBody AuthentificationRequest authentificationRequest){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authentificationRequest.getEmail(), authentificationRequest.getPassword())
        );
        return ResponseEntity.ok(AuthentificationResponse.builder()
            .accessToken(jetUtils.generateToken(authentificationRequest.getEmail()))
            .build());
    }

    @PostMapping("/token")
    public ResponseEntity<String> generate(@RequestParam String username){
        String token = jetUtils.generateToken(username);
        return ResponseEntity.ok(token);
    }
    @GetMapping("/validate") public ResponseEntity<String> validate(@RequestParam String token){
        boolean isValid = jetUtils.validateToken(token);
        return ResponseEntity.ok(isValid?"Token valide": "Token invalide");
    }

    public UtilisateurServiceImpl getUtilisateurService() {
        return utilisateurService;
    }

    public void setUtilisateurService(UtilisateurServiceImpl utilisateurService) {
        this.utilisateurService = utilisateurService;
    }
}
