package org.gestion.gestionstock.validator;

import org.gestion.gestionstock.dto.AdresseDto;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class AdresseValidator {

    /**
     * L'adresse 1, la ville et le pays sont obligatoires.
     * L'adresse 2 (complément) et le code postal sont facultatifs.
     */
    public static List<String> Validate(AdresseDto adresseDto){
        List<String> erros = new ArrayList<>();
        if (adresseDto == null){
            erros.add("veuillez renseigner l'adresse 1");
            erros.add("veuillez renseigner le nom de votre ville");
            erros.add("veuillez renseigner le nom de votre pays");
            return erros;
        }
        if (!StringUtils.hasText(adresseDto.getAdresse1())) {
            erros.add("veuillez renseigner l'adresse 1");
        }
        if (!StringUtils.hasText(adresseDto.getVille())) {
            erros.add("veuillez renseigner le nom de votre ville");
        }
        if (!StringUtils.hasText(adresseDto.getPays())) {
            erros.add("veuillez renseigner le nom de votre pays");
        }
        return erros;
    }
}
