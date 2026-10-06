package org.gestion.gestionstock.validator;

import org.gestion.gestionstock.dto.CommandeFournisseurDto;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class CommandeFournisseurValidator {
    public static List<String> validate(CommandeFournisseurDto dto) {
        List<String> errors = new ArrayList<>();
        if (dto == null){
            errors.add("veuillez renseigner le code de la commande");
            errors.add("veuillez renseigner la date de la commande");
            errors.add("veuillez renseigner l'état de la commande");
            errors.add("veuillez renseigner le fournisseur de la commande");
            return errors;
        }
        if (!StringUtils.hasLength(dto.getCode())){
            errors.add("veuillez renseigner le code de la commande");
        }
        if (dto.getDateCommande() == null){
            errors.add("veuillez renseigner la date de la commande");
        }
        if (!StringUtils.hasLength(dto.getEtatCommande().toString())){
            errors.add("veuillez renseigner l'état de la commande");
        }
        if (dto.getFournisseur()== null || dto.getFournisseur().getId() == null){
            errors.add("veuillez renseigner le fournisseur de la commande");
        }
        return errors;
    }
}
