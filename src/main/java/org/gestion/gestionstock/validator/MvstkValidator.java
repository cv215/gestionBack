package org.gestion.gestionstock.validator;

import org.gestion.gestionstock.dto.CathegoryDto;
import org.gestion.gestionstock.dto.MvstkDto;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MvstkValidator {

    public static List<String> Validate(MvstkDto mvstkDto){
        List<String> erros = new ArrayList<>();
        if (mvstkDto == null){
            erros.add("veuillez renseigner la date du mouvement de stock");
            erros.add("veuillez renseigner la quantité du mouvement de stock");
            erros.add("veuillez renseigner l'article du mouvement de stock");
            erros.add("veuillez renseigner le type du mouvement de stock");
            return erros;
        }

        if (mvstkDto.getDateMvt() == null){
            erros.add("veuillez renseigner la date du mouvement de stock");
        }

        if (mvstkDto.getQuantite() == null || mvstkDto.getQuantite().compareTo(BigDecimal.ZERO) == 0){
            erros.add("veuillez renseigner la quantité du mouvement de stock");
        }

        if (mvstkDto.getArticle() == null || mvstkDto.getArticle().getId() == null){
            erros.add("veuillez renseigner l'article du mouvement de stock");
        }

        if (!StringUtils.hasLength(mvstkDto.getTypeMvstkt().name())){
            erros.add("veuillez renseigner le type du mouvement de stock");
        }
        
        return erros;
    }
}
