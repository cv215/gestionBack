package org.gestion.gestionstock.repository;

import org.gestion.gestionstock.model.Ventes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface VentesRepository extends JpaRepository<Ventes, Integer> {

    Optional<Ventes> findVentesByCode(String code);

    void deleteById(@NonNull Integer id);

     @NonNull
     Optional<Ventes> findById(@NonNull  Integer id);

}
