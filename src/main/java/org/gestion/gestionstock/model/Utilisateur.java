package org.gestion.gestionstock.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "utilisateur")
public class Utilisateur extends AbstractEntity implements UserDetails {

    @Column(name = "nom")
    private String nom;

    @Column(name = "prenom")
    private String prenom;

    @Column(name = "datenaissance")
    private LocalDate dateNaissance;

    @Column(name = "motdepasse")
    private String motDePasse;

    @Embedded
    private Adresse adresse;

    @Column(name = "photo")
    private String photo;

    @Column(name = "email")
    private String email;

    @Column(name = "état")
    private boolean actif= false;

    @ManyToOne
    @JoinColumn(name = "identreprise")
    private Entreprise entreprise;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Roles> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles == null) {
            return Collections.emptyList();
        }
        return roles.stream()
                .filter(Objects::nonNull)
                .map(Roles::getAuthority)
                .filter(Objects::nonNull)
                .map(SimpleGrantedAuthority::new)
                .toList();
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
