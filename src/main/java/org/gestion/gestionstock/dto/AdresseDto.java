package org.gestion.gestionstock.dto;

import lombok.Builder;
import lombok.Data;
import org.gestion.gestionstock.model.Adresse;

@Builder
@Data
public class AdresseDto {

    private String adresse1;

    private String adresse2;

    private String ville;

    private String pays;

    private String codePostale;

    public static AdresseDto fromEntity(Adresse adresse){
        if (adresse == null){
            return null;
        }
        return AdresseDto.builder()
                .adresse1(adresse.getAdresse1())
                .adresse2(adresse.getAdresse2())
                .ville(adresse.getVille())
                .pays(adresse.getPays())
                .codePostale(adresse.getCodePostale())
                .build();
    }
    public static Adresse toEntity(AdresseDto adresseDto){
        if (adresseDto == null){
            return null;
        }
        Adresse adresse = new Adresse();
        adresse.setAdresse1(adresseDto.getAdresse1());
        adresse.setAdresse2(adresseDto.getAdresse2());
        adresse.setVille(adresseDto.getVille());
        adresse.setPays(adresseDto.getPays());
        adresse.setCodePostale(adresseDto.getCodePostale());
        return adresse;
    }
}
