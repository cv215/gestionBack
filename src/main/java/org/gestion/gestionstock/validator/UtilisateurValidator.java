package org.gestion.gestionstock.validator;

import org.gestion.gestionstock.dto.UtilisateurDto;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class UtilisateurValidator {

    public  static List<String> validate(UtilisateurDto utilisateurDto){
        List<String> erros = new ArrayList<>();
        if (utilisateurDto == null){
            erros.add("veuillez renseigner le nom de l'utilisateur ");
            erros.add("veuillez renseigner le prenom de l'utilisateur ");
            erros.add("veuillez renseigner le mot de passe de l'utilisateur ");
            erros.add("veuillez renseigner email de l'utilisateur ");
            erros.add("veuillez renseigner la date de naissance de l'utilisateur ");
            erros.addAll(AdresseValidator.Validate(null));
            return erros;
        }

        if (!StringUtils.hasLength(utilisateurDto.getNom())){
            erros.add("veuillez renseigner le nom de l'utilisateur ");
        }
        if (!StringUtils.hasLength(utilisateurDto.getPrenom())){
            erros.add("veuillez renseigner le prenom de l'utilisateur ");
        }
        if (!StringUtils.hasLength(utilisateurDto.getMotDePasse())){
            erros.add("veuillez renseigner le mot de passe de l'utilisateur ");
        }
        if (!StringUtils.hasLength(utilisateurDto.getEmail())){
            erros.add("veuillez renseigner email de l'utilisateur ");
        }
        if (utilisateurDto.getDateNaissance() == null){
            erros.add("veuillez renseigner la date de naissance de l'utilisateur ");
        }
        erros.addAll(AdresseValidator.Validate(utilisateurDto.getAdresse()));
        return erros;
    }
}
