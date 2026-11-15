package org.gestion.gestionstock.repository;


import java.util.Optional;

import org.gestion.gestionstock.model.Entreprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface EntrepriseRepository extends JpaRepository< Entreprise, Integer> {

    @NonNull
    Optional<Entreprise> findById(@NonNull Integer id);

    void deleteById( @NonNull Integer id);
}
