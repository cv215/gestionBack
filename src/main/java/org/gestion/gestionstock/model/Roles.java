package org.gestion.gestionstock.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.gestion.gestionstock.utils.TypeRole;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "roles")
public class Roles extends AbstractEntity{
    @Column(name = "identreprise")
    private Integer idEntreprise;

    @Column(name = "rolename")
    private String roleName;

    @ManyToOne
    @JoinColumn(name = "idutilisateur")
    @JsonIgnore
    private Utilisateur utilisateur;

    @Enumerated(EnumType.STRING)
    private TypeRole libelle;

    public String getAuthority() {
        String role = libelle != null ? libelle.name() : roleName;
        if (role == null || role.isBlank()) {
            return null;
        }
        return role.startsWith("ROLE_") ? role : "ROLE_" + role;
    }
}
