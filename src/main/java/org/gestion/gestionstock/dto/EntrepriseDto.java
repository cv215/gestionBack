package org.gestion.gestionstock.dto;

import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Data;
import org.gestion.gestionstock.model.Entreprise;
import org.gestion.gestionstock.model.Utilisateur;

import java.util.List;

@Builder
@Data
public class EntrepriseDto {
    private Integer id;

    private String nom;

    private String description;

    private AdresseDto adresse;

    private String codeFiscale;

    private  String photo;

    private String email;

    private String numTel;

    private String siteWeb;

    private List<Utilisateur> utilisateurs;

    public static EntrepriseDto fromEntity(Entreprise entreprise){
        if (entreprise == null){
            return null;
        }
        return EntrepriseDto.builder()
                .id(entreprise.getId())
                .nom(entreprise.getNom())
                .description(entreprise.getDescription())
                .codeFiscale(entreprise.getCodeFiscale())
                .photo(entreprise.getPhoto())
                .email(entreprise.getEmail())
                .numTel(entreprise.getNumTel())
                .siteWeb(entreprise.getSiteWeb())
                .adresse(AdresseDto.fromEntity(entreprise.getAdresse()))
                .build();
    }
    public static Entreprise toEntity(EntrepriseDto entrepriseDto){
        if (entrepriseDto == null){
            return null;
        }
        Entreprise entreprise = new Entreprise();
        entreprise.setNom(entrepriseDto.getNom());
        entreprise.setDescription(entrepriseDto.getDescription());
        entreprise.setCodeFiscale(entrepriseDto.getCodeFiscale());
        entreprise.setPhoto(entrepriseDto.getPhoto());
        entreprise.setEmail(entrepriseDto.getEmail());
        entreprise.setNumTel(entrepriseDto.getNumTel());
        entreprise.setSiteWeb(entrepriseDto.getSiteWeb());
        entreprise.setAdresse(AdresseDto.toEntity(entrepriseDto.getAdresse()));
        return entreprise;
    }
}
