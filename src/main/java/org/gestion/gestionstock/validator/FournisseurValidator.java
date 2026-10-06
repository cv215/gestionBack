package org.gestion.gestionstock.validator;

import org.gestion.gestionstock.dto.FournisseurDto;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class FournisseurValidator {
    public static List<String> validate(FournisseurDto fournisseurDto){
        List<String> erros = new ArrayList<>();

        if (fournisseurDto == null){
            erros.add("veuillez renseigner le nom du fourniseur ");
            erros.add("veuillez renseigner le prenom du fourniseur ");
            erros.add("veuillez renseigner le numero de téléphone du fourniseur ");
            erros.add("veuillez renseigner email du fourniseur ");
            erros.addAll(AdresseValidator.Validate(null));
            return erros;
        }

        if (!StringUtils.hasLength(fournisseurDto.getNom())){
            erros.add("veuillez renseigner le nom du fourniseur ");
        }
        if (!StringUtils.hasLength(fournisseurDto.getPrenom())){
            erros.add("veuillez renseigner le prenom du fourniseur ");
        }
        if (!StringUtils.hasLength(fournisseurDto.getEmail())){
            erros.add("veuillez renseigner email du fourniseur ");
        }
        if (fournisseurDto.getNumTel() == null){
            erros.add("veuillez renseigner le numero de téléphone du fourniseur ");
        }
        erros.addAll(AdresseValidator.Validate(fournisseurDto.getAdresse()));
        return erros;
    }
}
