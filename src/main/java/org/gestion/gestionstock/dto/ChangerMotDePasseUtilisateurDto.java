package org.gestion.gestionstock.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangerMotDePasseUtilisateurDto {
    private String email;
    private Integer id;
    @lombok.ToString.Exclude
    private String ancienMotDePasse;
    @lombok.ToString.Exclude
    private String nouveauMotDePasse;
    @lombok.ToString.Exclude
    private String confirmerMotDePasse;
}
