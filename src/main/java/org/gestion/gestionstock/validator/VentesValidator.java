package org.gestion.gestionstock.validator;

import org.gestion.gestionstock.dto.VentesDto;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class VentesValidator {

    public static List<String> validate(VentesDto dto){

        List<String> errors = new ArrayList<>();

        if (dto == null){

            errors.add("veuillez renseigner le code de la commande");

            errors.add("veuillez renseigner la date de la commande");

            return errors;
        }

        if (!StringUtils.hasLength(dto.getCode())){
            errors.add("veuillez renseigner le code de la commande");
        }

        if (dto.getDateVente() == null){
            errors.add("veuillez renseigner la date de la commande");
        }

        if (dto.getLigneVentes() == null || dto.getLigneVentes().isEmpty()){
            errors.add("veuillez ajouter au moins un article à la vente");
        } else {
            dto.getLigneVentes().forEach(ligne -> {
                if (ligne == null || ligne.getIdArticle() == null){
                    errors.add("chaque ligne de vente doit référencer un article");
                } else if (ligne.getQuantite() == null || ligne.getQuantite().signum() <= 0){
                    errors.add("la quantité de chaque ligne de vente doit être supérieure à zéro");
                }
            });
        }

        return errors;
    }

}
