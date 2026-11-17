package org.gestion.gestionstock.controller;

import lombok.extern.slf4j.Slf4j;
import org.gestion.gestionstock.config.JetUtils;
import org.gestion.gestionstock.dto.auth.AuthentificationRequest;
import org.gestion.gestionstock.dto.auth.AuthentificationResponse;
import org.gestion.gestionstock.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping( "/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private JetUtils jetUtils;

    @PostMapping("/connexion")
    public ResponseEntity<AuthentificationResponse> connexion(@RequestBody AuthentificationRequest authentificationRequest){
        String email = authentificationRequest.getEmail() == null
                ? null
                : authentificationRequest.getEmail().trim().toLowerCase(Locale.ROOT);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, authentificationRequest.getPassword())
        );
        return ResponseEntity.ok(AuthentificationResponse.builder()
            .accessToken(jetUtils.generateToken(authentication.getName(),
                    authentication.getAuthorities().stream()
                            .map(org.springframework.security.core.GrantedAuthority::getAuthority)
                            .toList()))
            .build());
    }

    /**
     * Demande de réinitialisation du mot de passe.
     * La réponse est toujours identique, que le compte existe ou non, pour ne pas révéler les adresses inscrites.
     * Aucun e-mail n'est envoyé tant qu'un serveur SMTP n'est pas configuré : la demande est seulement journalisée.
     * (L'ancien endpoint /auth/token, qui délivrait un jeton valide pour n'importe quel e-mail, a été supprimé.)
     */
    @PostMapping("/mot-de-passe-oublie")
    public ResponseEntity<Map<String, String>> motDePasseOublie(@RequestParam("email") String email){
        if (StringUtils.hasText(email)) {
            boolean exists = utilisateurRepository.findUtilisateurByEmail(email.trim().toLowerCase(Locale.ROOT)).isPresent();
            log.info("Demande de réinitialisation du mot de passe (compte existant : {})", exists);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                "message", "Si un compte existe pour cette adresse, un administrateur pourra réinitialiser le mot de passe."));
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validate(@RequestParam String token){
        boolean isValid = jetUtils.validateToken(token);
        return ResponseEntity.ok(isValid?"Token valide": "Token invalide");
    }
}
