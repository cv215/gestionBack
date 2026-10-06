package org.gestion.gestionstock.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ChangerMotDePasseUtilisateurDto {

    private String email;

    private Integer id;

    private String motDePasse;

    private String comfirMotDePasse;
}
