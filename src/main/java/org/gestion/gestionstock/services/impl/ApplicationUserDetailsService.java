package org.gestion.gestionstock.services.impl;

import org.gestion.gestionstock.exception.EntityNotFoundException;
import org.gestion.gestionstock.exception.ErrorCodes;
import org.gestion.gestionstock.model.Utilisateur;
import org.gestion.gestionstock.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class ApplicationUserDetailsService implements UserDetailsService {

    @Autowired
    private UtilisateurRepository repository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Utilisateur utilisateur = repository.findUtilisateurByEmail(email).orElseThrow(()->
                new EntityNotFoundException("Aucun utilisateur n'existe avec le email fournit", ErrorCodes.UTILISATEUR_NOT_FOUND));

        return new User(utilisateur.getEmail(), utilisateur.getMotDePasse(), Collections.emptyList());
    }
}
