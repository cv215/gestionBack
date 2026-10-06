package org.gestion.gestionstock.validator;

import org.gestion.gestionstock.dto.CathegoryDto;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class CathegoryValidator {

    public static List<String> Validate(CathegoryDto cathegoryDto){
        List<String> erros = new ArrayList<>();
        if (cathegoryDto == null || !StringUtils.hasLength(cathegoryDto.getCode())){
            erros.add("veuillez renseigner le code de la cathegory !");
        }
        return erros;
    }
}
