package org.gestion.gestionstock.validator;

import org.gestion.gestionstock.dto.ClientDto;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ClientValidator {
    public static List<String> validate(ClientDto clientDto){
        List<String> erros = new ArrayList<>();

        if (clientDto == null){
            erros.add("veuiller renseigner le nom du client");
            erros.add("veuiller renseigner le prenom du client");
            erros.add("veuiller renseigner l'email du client");
            erros.add("veuiller renseigner le numero de téléphone du client");
            erros.addAll(AdresseValidator.Validate(null));
            return erros;
        }

        if (!StringUtils.hasLength(clientDto.getNom())){
            erros.add("veuiller renseigner le nom du client");
        }
        if (!StringUtils.hasLength(clientDto.getPrenom())){
            erros.add("veuiller renseigner le prenom du client");
        }
        if (!StringUtils.hasLength(clientDto.getEmail())){
            erros.add("veuiller renseigner l'email du client");
        }
        if (!StringUtils.hasLength(clientDto.getNumTel())){
            erros.add("veuiller renseigner le numero de téléphone du client");
        }
        erros.addAll(AdresseValidator.Validate(clientDto.getAdresse()));
     return erros;
    }
}
