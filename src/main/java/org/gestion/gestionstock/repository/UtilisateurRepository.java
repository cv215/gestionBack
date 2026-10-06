package org.gestion.gestionstock.repository;

import aj.org.objectweb.asm.commons.Remapper;
import org.gestion.gestionstock.dto.UtilisateurDto;
import org.gestion.gestionstock.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UtilisateurRepository extends JpaRepository< Utilisateur, Integer> {

    Optional<Utilisateur> findUtilisateurByEmail(String email);
}
