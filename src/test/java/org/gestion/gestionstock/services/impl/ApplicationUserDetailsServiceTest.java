package org.gestion.gestionstock.services.impl;

import org.gestion.gestionstock.repository.UtilisateurRepository;
import org.gestion.gestionstock.model.Utilisateur;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationUserDetailsServiceTest {

    @Mock
    private UtilisateurRepository repository;

    @InjectMocks
    private ApplicationUserDetailsService userDetailsService;

    @Test
    void throwsUsernameNotFoundExceptionWhenEmailDoesNotExist() {
        when(repository.findUtilisateurByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(" Missing@Example.com "));
    }

    @Test
    void normalizesEmailAndReturnsUtilisateurUserDetails() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail("user@example.com");
        when(repository.findUtilisateurByEmail("user@example.com")).thenReturn(Optional.of(utilisateur));

        assertSame(utilisateur, userDetailsService.loadUserByUsername(" User@Example.com "));
    }
}
