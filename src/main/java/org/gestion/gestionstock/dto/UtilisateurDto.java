package org.gestion.gestionstock.dto;


import lombok.Builder;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.gestion.gestionstock.model.Roles;
import org.gestion.gestionstock.model.Utilisateur;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Builder
@Data
public class UtilisateurDto implements UserDetails {
    private Integer id;

    private Integer idEntreprise;

    private String nom;

    private String prenom;

    private Instant dateNaissance;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String motDePasse;

    private AdresseDto adresse;

    private String photo;

    private Roles roles;
    private boolean actif = false;

    private String email;

    private EntrepriseDto entreprise;

    public static UtilisateurDto fromEntity(Utilisateur utilisateur){
        if (utilisateur == null){
            return null;
        }
        return UtilisateurDto.builder()
                .id(utilisateur.getId())
                .nom(utilisateur.getNom())
                .prenom(utilisateur.getPrenom())
                .dateNaissance(utilisateur.getDateNaissance())
                .motDePasse(utilisateur.getMotDePasse())
                .photo(utilisateur.getPhoto())
                .email(utilisateur.getEmail())
                .actif(utilisateur.isActif())
                .adresse(AdresseDto.fromEntity(utilisateur.getAdresse()))
                .build();
    }
    public static Utilisateur toEntity(UtilisateurDto utilisateurDto){
        if (utilisateurDto == null){
            return null;
        }
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(utilisateurDto.getNom());
        utilisateur.setPrenom(utilisateurDto.getPrenom());
        utilisateur.setDateNaissance(utilisateurDto.getDateNaissance());
        utilisateur.setMotDePasse(utilisateurDto.getMotDePasse());
        utilisateur.setPhoto(utilisateurDto.getPhoto());
        utilisateur.setEmail(utilisateurDto.getEmail());
        utilisateur.setAdresse(AdresseDto.toEntity(utilisateurDto.getAdresse()));
        utilisateur.setActif(utilisateurDto.isActif());
        return utilisateur;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE"+this.roles.getLibelle()));
    }

    @Override
    public String getPassword() {
        return this.motDePasse;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.actif;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.actif;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.actif;
    }

    @Override
    public boolean isEnabled() {
        return this.actif;
    }
}
