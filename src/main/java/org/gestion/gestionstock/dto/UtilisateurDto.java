package org.gestion.gestionstock.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.gestion.gestionstock.model.Roles;
import org.gestion.gestionstock.model.Utilisateur;

import java.time.LocalDate;
import java.util.List;

@Builder
@Data
public class UtilisateurDto {
    private Integer id;

    private Integer idEntreprise;

    private String nom;

    private String prenom;

    private LocalDate dateNaissance;

    @lombok.ToString.Exclude
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String motDePasse;

    private AdresseDto adresse;

    private String photo;

    private List<Roles> roles;

    @Builder.Default
    private boolean actif = false;

    private String email;

    /** Téléphone : utilisé uniquement à l'inscription pour créer la fiche client ou fournisseur. */
    private String numTel;

    private EntrepriseDto entreprise;

    public static UtilisateurDto fromEntity(Utilisateur utilisateur){
        if (utilisateur == null){
            return null;
        }
        return UtilisateurDto.builder()
                .id(utilisateur.getId())
                .idEntreprise(utilisateur.getEntreprise() != null ? utilisateur.getEntreprise().getId() : null)
                .entreprise(EntrepriseDto.fromEntity(utilisateur.getEntreprise()))
                .nom(utilisateur.getNom())
                .prenom(utilisateur.getPrenom())
                .dateNaissance(utilisateur.getDateNaissance())
                .motDePasse(utilisateur.getMotDePasse())
                .photo(utilisateur.getPhoto())
                .roles(utilisateur.getRoles())
                .email(utilisateur.getEmail())
                .actif(utilisateur.isActif())
                .adresse(AdresseDto.fromEntity(utilisateur.getAdresse()))
                .build();
    }

    public static Utilisateur toEntity(UtilisateurDto utilisateurDto){
        if (utilisateurDto == null){
            return null;
        }
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(utilisateurDto.getId());
        utilisateur.setNom(utilisateurDto.getNom());
        utilisateur.setPrenom(utilisateurDto.getPrenom());
        utilisateur.setDateNaissance(utilisateurDto.getDateNaissance());
        utilisateur.setMotDePasse(utilisateurDto.getMotDePasse());
        utilisateur.setPhoto(utilisateurDto.getPhoto());
        utilisateur.setEmail(utilisateurDto.getEmail());
        utilisateur.setAdresse(AdresseDto.toEntity(utilisateurDto.getAdresse()));
        utilisateur.setActif(utilisateurDto.isActif());
        return utilisateur;
    }
}
