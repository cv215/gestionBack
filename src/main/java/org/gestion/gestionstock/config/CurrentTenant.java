package org.gestion.gestionstock.config;

import org.gestion.gestionstock.model.Entreprise;
import org.gestion.gestionstock.model.Roles;
import org.gestion.gestionstock.model.Utilisateur;
import org.gestion.gestionstock.repository.UtilisateurRepository;
import org.gestion.gestionstock.utils.TypeRole;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Informations sur l'utilisateur connecté : son entreprise (pour que chaque entreprise ne voie
 * que ses propres données) et ses rôles (pour les contrôles d'accès métier).
 * Un compte sans entreprise n'est pas filtré.
 */
@Component
public class CurrentTenant {

    private final UtilisateurRepository utilisateurRepository;

    public CurrentTenant(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    public Optional<Utilisateur> currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken
                || !StringUtils.hasText(authentication.getName())) {
            return Optional.empty();
        }
        return utilisateurRepository.findUtilisateurByEmail(authentication.getName().trim().toLowerCase(Locale.ROOT));
    }

    public Optional<Integer> idEntreprise() {
        return currentUser()
                .map(Utilisateur::getEntreprise)
                .map(Entreprise::getId);
    }

    public Optional<String> email() {
        return currentUser().map(Utilisateur::getEmail);
    }

    /** Vrai si l'utilisateur connecté possède au moins un des rôles donnés. */
    public boolean hasAnyRole(TypeRole... roles) {
        return currentUser().map(u -> hasAnyRole(u, roles)).orElse(false);
    }

    public static boolean hasAnyRole(Utilisateur utilisateur, TypeRole... roles) {
        if (utilisateur == null || utilisateur.getRoles() == null) {
            return false;
        }
        return utilisateur.getRoles().stream()
                .filter(Objects::nonNull)
                .map(Roles::getLibelle)
                .filter(Objects::nonNull)
                .anyMatch(libelle -> Arrays.asList(roles).contains(libelle));
    }

    /** Vrai si l'utilisateur connecté est un client ou un fournisseur (accès limité à ses propres données). */
    public boolean isPartenaireExterne() {
        return hasAnyRole(TypeRole.CLIENT, TypeRole.FOURNISSEUR)
                && !hasAnyRole(TypeRole.ADMIN_ENTREPRISE, TypeRole.GESTIONNAIRE_STOCK, TypeRole.COMMERCIAL,
                TypeRole.CAISSIER, TypeRole.UTILISATEUR);
    }

    /** Vrai si l'élément appartient à l'entreprise courante (ou s'il n'y a pas d'entreprise courante). */
    public boolean owns(Integer idEntrepriseElement) {
        return idEntreprise().map(id -> Objects.equals(id, idEntrepriseElement)).orElse(true);
    }

    /** Entreprise à enregistrer sur un nouvel élément : celle de l'utilisateur connecté si elle existe. */
    public Integer resolve(Integer idEntrepriseDemande) {
        return idEntreprise().orElse(idEntrepriseDemande);
    }
}
