package org.gestion.gestionstock.repository;


import org.gestion.gestionstock.model.Entreprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;
@Repository
public interface EntrepriseRepository extends JpaRepository< Entreprise, Integer> {

    Optional<Entreprise> findById(Integer id);

    void deleteById(Integer id);
}
