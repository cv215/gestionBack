package org.gestion.gestionstock.validator;

import org.gestion.gestionstock.dto.EntrepriseDto;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class EntrepriseValidator {
    public static List<String> validate(EntrepriseDto entrepriseDto){
        List<String> erros = new ArrayList<>();
        if (entrepriseDto == null){
            erros.add("veuilez renseigner le nom de l'entreprise ");
            erros.add("veuilez renseigner le codefiscale de l'entreprise ");
            erros.add("veuilez renseigner l'email de l'entreprise ");
            erros.add("veuilez renseigner le numero de téléphone de l'entreprise ");
            erros.add("veuilez renseigner l'adresse de l'entreprise ");
            erros.addAll(AdresseValidator.Validate(null));
            return erros;
        }

        if (!StringUtils.hasLength(entrepriseDto.getNom())){
            erros.add("veuilez renseigner le nom de l'entreprise ");
        }
        if (!StringUtils.hasLength(entrepriseDto.getCodeFiscale())){
            erros.add("veuilez renseigner le codefiscale de l'entreprise ");
        }
        if (!StringUtils.hasLength(entrepriseDto.getEmail())){
            erros.add("veuilez renseigner l'email de l'entreprise ");
        }
        if (!StringUtils.hasLength(entrepriseDto.getNumTel())){
            erros.add("veuilez renseigner le numero de téléphone de l'entreprise ");
        }
        erros.addAll(AdresseValidator.Validate(entrepriseDto.getAdresse()));

        return erros;
    }
}
