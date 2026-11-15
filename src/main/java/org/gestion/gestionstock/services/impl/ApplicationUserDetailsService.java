package org.gestion.gestionstock.services.impl;

import org.gestion.gestionstock.model.Utilisateur;
import org.gestion.gestionstock.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class ApplicationUserDetailsService implements UserDetailsService {

    @Autowired
    private UtilisateurRepository repository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String normalizedEmail = email == null ? null : email.trim().toLowerCase(Locale.ROOT);
        Utilisateur utilisateur = repository.findUtilisateurByEmail(normalizedEmail).orElseThrow(()->
                new UsernameNotFoundException("Aucun utilisateur n'existe avec le email fournit"));

        return utilisateur;
    }
}
