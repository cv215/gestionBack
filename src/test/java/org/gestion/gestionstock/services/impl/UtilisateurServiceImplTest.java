package org.gestion.gestionstock.services.impl;

import org.gestion.gestionstock.dto.AdresseDto;
import org.gestion.gestionstock.dto.ChangerMotDePasseUtilisateurDto;
import org.gestion.gestionstock.dto.UtilisateurDto;
import org.gestion.gestionstock.exception.InvalidOperationException;
import org.gestion.gestionstock.model.Utilisateur;
import org.gestion.gestionstock.repository.UtilisateurRepository;
import org.gestion.gestionstock.utils.TypeRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UtilisateurServiceImplTest {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private static void authenticateAs(String email) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(email, null, List.of()));
    }

    @Test
    void encodesNewPasswordBeforeSaving() {
        UtilisateurRepository repository = mock(UtilisateurRepository.class);
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail("user@example.com");
        utilisateur.setMotDePasse(passwordEncoder.encode("old-password"));
        when(repository.findUtilisateurByEmail("user@example.com")).thenReturn(Optional.of(utilisateur));
        when(repository.save(utilisateur)).thenReturn(utilisateur);
        authenticateAs("user@example.com");
        UtilisateurServiceImpl service = new UtilisateurServiceImpl(repository, passwordEncoder);
        ChangerMotDePasseUtilisateurDto request = ChangerMotDePasseUtilisateurDto.builder()
                .ancienMotDePasse("old-password")
                .nouveauMotDePasse("new-password")
                .confirmerMotDePasse("new-password")
                .build();

        UtilisateurDto updated = service.changerMotDePasse(request);

        assertTrue(passwordEncoder.matches("new-password", updated.getMotDePasse()));
    }

    @Test
    void refusesPasswordChangeWhenOldPasswordIsWrong() {
        UtilisateurRepository repository = mock(UtilisateurRepository.class);
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail("user@example.com");
        utilisateur.setMotDePasse(passwordEncoder.encode("old-password"));
        when(repository.findUtilisateurByEmail("user@example.com")).thenReturn(Optional.of(utilisateur));
        authenticateAs("user@example.com");
        UtilisateurServiceImpl service = new UtilisateurServiceImpl(repository, passwordEncoder);
        ChangerMotDePasseUtilisateurDto request = ChangerMotDePasseUtilisateurDto.builder()
                .ancienMotDePasse("wrong-password")
                .nouveauMotDePasse("new-password")
                .confirmerMotDePasse("new-password")
                .build();

        assertThrows(InvalidOperationException.class, () -> service.changerMotDePasse(request));
    }

    @Test
    void publicSignupAlwaysCreatesAClientAccount() {
        UtilisateurRepository repository = mock(UtilisateurRepository.class);
        when(repository.findUtilisateurByEmail("new@example.com")).thenReturn(Optional.empty());
        when(repository.save(any(Utilisateur.class))).thenAnswer(invocation -> invocation.getArgument(0));
        UtilisateurServiceImpl service = new UtilisateurServiceImpl(repository, passwordEncoder);
        UtilisateurDto request = UtilisateurDto.builder()
                .nom("Doe")
                .prenom("Jane")
                .email(" New@Example.com ")
                .motDePasse("password123")
                .adresse(AdresseDto.builder().adresse1("1 rue").ville("Paris").pays("France").build())
                .build();

        UtilisateurDto saved = service.save(request);

        assertEquals("new@example.com", saved.getEmail());
        assertTrue(saved.isActif());
        assertEquals(1, saved.getRoles().size());
        assertEquals(TypeRole.CLIENT, saved.getRoles().get(0).getLibelle());
    }
}
