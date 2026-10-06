package org.gestion.gestionstock.validator;

import org.gestion.gestionstock.dto.CommandeClientDto;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class CommandeClientValodator {
    public static List<String> validate(CommandeClientDto dto){
        List<String> errors = new ArrayList<>();
        if (dto == null){
            errors.add("veuillez renseigner le code de la commande");
            errors.add("veuillez renseigner la date de la commande");
            errors.add("veuillez renseigner l'état de la commande");
            errors.add("veuillez renseigner le client de la commande");
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
        if (dto.getClient()== null || dto.getClient().getId() == null){
            errors.add("veuillez renseigner le client de la commande");
        }
        return errors;
    }
}
