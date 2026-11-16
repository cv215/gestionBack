package org.gestion.gestionstock.config;

import org.gestion.gestionstock.model.Entreprise;
import org.gestion.gestionstock.model.Utilisateur;
import org.gestion.gestionstock.repository.UtilisateurRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Donne l'entreprise de l'utilisateur connecté, pour que chaque entreprise ne voie que ses propres données.
 * Un compte sans entreprise (ex. client inscrit seul) n'est pas filtré.
 */
@Component
public class CurrentTenant {

    private final UtilisateurRepository utilisateurRepository;

    public CurrentTenant(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    public Optional<Integer> idEntreprise() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken
                || !StringUtils.hasText(authentication.getName())) {
            return Optional.empty();
        }
        return utilisateurRepository.findUtilisateurByEmail(authentication.getName().trim().toLowerCase(Locale.ROOT))
                .map(Utilisateur::getEntreprise)
                .map(Entreprise::getId);
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
