package org.gestion.gestionstock.validator;

import org.gestion.gestionstock.dto.AdresseDto;
import org.gestion.gestionstock.dto.CathegoryDto;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class AdresseValidator {


    public static List<String> Validate(AdresseDto adresseDto){
        List<String> erros = new ArrayList<>();
        if (adresseDto == null){
            erros.add("veuillez renseigner l'adresse 1");
            erros.add("veuillez renseigner l'adresse 2");
            erros.add("veuillez renseigner le nom de votre ville");
            erros.add("veuillez renseigner le nom de votre pays");
            erros.add("veuillez renseigner votre code postal");
            return erros;
        }
        if (!StringUtils.hasLength(adresseDto.getAdresse1())) {
            erros.add("veuillez renseigner l'adresse 1");
        }
        if (!StringUtils.hasLength(adresseDto.getAdresse2())) {
            erros.add("veuillez renseigner l'adresse 2");
        }
        if (!StringUtils.hasLength(adresseDto.getVille())) {
            erros.add("veuillez renseigner le nom de votre ville");
        }
        if (!StringUtils.hasLength(adresseDto.getPays())) {
            erros.add("veuillez renseigner le nom de votre pays");
        }
        if (!StringUtils.hasLength(adresseDto.getCodePostale())) {
            erros.add("veuillez renseigner votre code postal");
        }
        return erros;
    }
}
