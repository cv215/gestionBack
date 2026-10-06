package org.gestion.gestionstock.dto;

import lombok.Builder;
import lombok.Data;
import org.gestion.gestionstock.model.Roles;


@Builder
@Data
public class RolesDto {
    private Integer id;

    private Integer idEntreprise;

    private String roleName;

    private UtilisateurDto utilisateur;

    public static RolesDto fromEntity(Roles roles){
        if (roles == null){
            return null;
        }
        return RolesDto.builder()
                .id(roles.getId())
                .roleName(roles.getRoleName())
                .idEntreprise(roles.getIdEntreprise())
                .utilisateur(UtilisateurDto.fromEntity(roles.getUtilisateur()))
                .build();
    }
    public static Roles toEntity(RolesDto rolesDto){
        if (rolesDto == null){
            return null;
        }
        Roles roles = new Roles();
        roles.setRoleName(rolesDto.getRoleName());
        return roles;
    }
}
