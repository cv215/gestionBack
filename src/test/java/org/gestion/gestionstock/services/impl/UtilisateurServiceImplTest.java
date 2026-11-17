package org.gestion.gestionstock.services.impl;

import org.gestion.gestionstock.dto.AdresseDto;
import org.gestion.gestionstock.dto.ChangerMotDePasseUtilisateurDto;
import org.gestion.gestionstock.dto.UtilisateurDto;
import org.gestion.gestionstock.exception.InvalidOperationException;
import org.gestion.gestionstock.exception.InvalidEntityException;
import org.gestion.gestionstock.model.Client;
import org.gestion.gestionstock.model.Entreprise;
import org.gestion.gestionstock.model.Fournisseur;
import org.gestion.gestionstock.model.Roles;
import org.gestion.gestionstock.model.Utilisateur;
import org.gestion.gestionstock.repository.ClientRepository;
import org.gestion.gestionstock.repository.EntrepriseRepository;
import org.gestion.gestionstock.repository.FournisseurRepository;
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
import static org.mockito.Mockito.verify;
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

    private static UtilisateurDto.UtilisateurDtoBuilder publicSignup(String role) {
        return UtilisateurDto.builder()
                .nom("Doe")
                .prenom("Jane")
                .email(" New@Example.com ")
                .motDePasse("password123")
                .numTel("0600000000")
                .idEntreprise(7)
                .roles(List.of(roleOf(role)))
                .adresse(AdresseDto.builder().adresse1("1 rue").ville("Paris").pays("France").build());
    }

    private static Roles roleOf(String libelle) {
        Roles r = new Roles();
        r.setLibelle(TypeRole.valueOf(libelle));
        return r;
    }

    @Test
    void publicSignupNeverGrantsAnInternalRoleAndCreatesTheClientRecord() {
        UtilisateurRepository repository = mock(UtilisateurRepository.class);
        EntrepriseRepository entrepriseRepository = mock(EntrepriseRepository.class);
        ClientRepository clientRepository = mock(ClientRepository.class);
        FournisseurRepository fournisseurRepository = mock(FournisseurRepository.class);
        Entreprise entreprise = new Entreprise();
        entreprise.setId(7);
        when(entrepriseRepository.findById(7)).thenReturn(Optional.of(entreprise));
        when(repository.findUtilisateurByEmail("new@example.com")).thenReturn(Optional.empty());
        when(repository.save(any(Utilisateur.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(clientRepository.findFirstByEmailIgnoreCaseAndIdEntreprise("new@example.com", 7)).thenReturn(Optional.empty());
        UtilisateurServiceImpl service = new UtilisateurServiceImpl(repository, passwordEncoder,
                entrepriseRepository, clientRepository, fournisseurRepository);

        UtilisateurDto saved = service.save(publicSignup("ADMIN_ENTREPRISE").build());

        assertEquals("new@example.com", saved.getEmail());
        assertTrue(saved.isActif());
        assertEquals(1, saved.getRoles().size());
        assertEquals(TypeRole.CLIENT, saved.getRoles().get(0).getLibelle());
        assertEquals(7, saved.getIdEntreprise());
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    void publicSupplierSignupCreatesTheSupplierRecord() {
        UtilisateurRepository repository = mock(UtilisateurRepository.class);
        EntrepriseRepository entrepriseRepository = mock(EntrepriseRepository.class);
        ClientRepository clientRepository = mock(ClientRepository.class);
        FournisseurRepository fournisseurRepository = mock(FournisseurRepository.class);
        Entreprise entreprise = new Entreprise();
        entreprise.setId(7);
        when(entrepriseRepository.findById(7)).thenReturn(Optional.of(entreprise));
        when(repository.findUtilisateurByEmail("new@example.com")).thenReturn(Optional.empty());
        when(repository.save(any(Utilisateur.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(fournisseurRepository.findFirstByEmailIgnoreCaseAndIdEntreprise("new@example.com", 7)).thenReturn(Optional.empty());
        UtilisateurServiceImpl service = new UtilisateurServiceImpl(repository, passwordEncoder,
                entrepriseRepository, clientRepository, fournisseurRepository);

        UtilisateurDto saved = service.save(publicSignup("FOURNISSEUR").build());

        assertEquals(TypeRole.FOURNISSEUR, saved.getRoles().get(0).getLibelle());
        verify(fournisseurRepository).save(any(Fournisseur.class));
    }

    @Test
    void publicSignupRequiresACompanyAndAPhone() {
        UtilisateurRepository repository = mock(UtilisateurRepository.class);
        EntrepriseRepository entrepriseRepository = mock(EntrepriseRepository.class);
        when(repository.findUtilisateurByEmail("new@example.com")).thenReturn(Optional.empty());
        when(entrepriseRepository.findById(any())).thenReturn(Optional.empty());
        UtilisateurServiceImpl service = new UtilisateurServiceImpl(repository, passwordEncoder,
                entrepriseRepository, mock(ClientRepository.class), mock(FournisseurRepository.class));

        InvalidEntityException error = assertThrows(InvalidEntityException.class,
                () -> service.save(publicSignup("CLIENT").idEntreprise(null).numTel(" ").build()));
        assertEquals(2, error.getErrors().size());
    }
}
