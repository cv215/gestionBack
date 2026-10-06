package org.gestion.gestionstock.dto;

import lombok.Builder;
import lombok.Data;
import org.gestion.gestionstock.model.Fournisseur;


import java.util.List;

@Builder
@Data
public class FournisseurDto {
    private Integer id;

    private Integer idEntreprise;

    private String nom;

    private String prenom;

    private AdresseDto adresse;

    private String photo;

    private String numTel;

    private String email;

    private List<CommandeFournisseurDto> commandeFournisseurs;

    public static FournisseurDto fromEntity(Fournisseur fournisseur){
        if (fournisseur == null){
            return null;
        }
        return FournisseurDto.builder()
                .id(fournisseur.getId())
                .nom(fournisseur.getNom())
                .idEntreprise(fournisseur.getIdEntreprise())
                .prenom(fournisseur.getPrenom())
                .photo(fournisseur.getPhoto())
                .numTel(fournisseur.getNumTel())
                .email(fournisseur.getEmail())
                .adresse(AdresseDto.fromEntity(fournisseur.getAdresse()))
                .build();
    }
    public static Fournisseur toEntity(FournisseurDto fournisseurDto){
        if (fournisseurDto == null){
            return null;
        }
        Fournisseur fournisseur = new Fournisseur();
        fournisseur.setNom(fournisseurDto.getNom());
        fournisseur.setPrenom(fournisseurDto.getPrenom());
        fournisseur.setPhoto(fournisseurDto.getPhoto());
        fournisseur.setNumTel(fournisseurDto.getNumTel());
        fournisseur.setEmail(fournisseurDto.getEmail());
        fournisseur.setAdresse(AdresseDto.toEntity(fournisseurDto.getAdresse()));
        return fournisseur;
    }
}
