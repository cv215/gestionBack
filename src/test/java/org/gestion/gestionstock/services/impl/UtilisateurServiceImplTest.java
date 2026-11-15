package org.gestion.gestionstock.services.impl;

import org.gestion.gestionstock.dto.ChangerMotDePasseUtilisateurDto;
import org.gestion.gestionstock.dto.UtilisateurDto;
import org.gestion.gestionstock.model.Utilisateur;
import org.gestion.gestionstock.repository.UtilisateurRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UtilisateurServiceImplTest {

    @Test
    void encodesNewPasswordBeforeSaving() {
        UtilisateurRepository repository = mock(UtilisateurRepository.class);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail("user@example.com");
        when(repository.findUtilisateurByEmail("user@example.com")).thenReturn(Optional.of(utilisateur));
        when(repository.save(utilisateur)).thenReturn(utilisateur);
        UtilisateurServiceImpl service = new UtilisateurServiceImpl(repository, passwordEncoder);
        ChangerMotDePasseUtilisateurDto request = ChangerMotDePasseUtilisateurDto.builder()
                .email("user@example.com")
                .nouveauMotDePasse("new-password")
                .confirmerMotDePasse("new-password")
                .build();

        UtilisateurDto updated = service.changerMotDePasse(request);

        assertTrue(passwordEncoder.matches("new-password", updated.getMotDePasse()));
    }
}
