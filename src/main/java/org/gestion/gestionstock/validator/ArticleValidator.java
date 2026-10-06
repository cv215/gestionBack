package org.gestion.gestionstock.validator;

import org.gestion.gestionstock.dto.ArticleDto;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ArticleValidator {
    public static List<String> validate(ArticleDto articleDto){
        List<String> erros = new ArrayList<>();
        if (articleDto == null){
            erros.add("veuillez renseigner la designation de l'article");
            erros.add("veuillez renseigner le prix unitaire  de l'article");
            erros.add("veuillez renseigner le code de l'article");
            erros.add("veuillez renseigner le taux TVA de l'article");
            erros.add("veuillez renseigner le prix unitaire TTC de l'article");
            erros.add("veuillez selectionner une  cathegory");
            return erros;
        }

        if (!StringUtils.hasLength(articleDto.getDesignation())){
            erros.add("veuillez renseigner la designation de l'article");
        }
        if (articleDto.getPrixUnitaireHt() == null){
            erros.add("veuillez renseigner le prix unitaire  de l'article");
        }
        if (!StringUtils.hasLength(articleDto.getCodeArticle())){
            erros.add("veuillez renseigner le code de l'article");
        }
        if (articleDto.getTauxTva() == null){
            erros.add("veuillez renseigner le taux TVA de l'article");
        }
        if (articleDto.getPrixUnitaireTtc() == null){
            erros.add("veuillez renseigner le prix unitaire TTC de l'article");
        }
        if (articleDto.getCathegory() == null){
            erros.add("veuillez selectionner une  cathegory");
        }
        return erros;
    }
}
