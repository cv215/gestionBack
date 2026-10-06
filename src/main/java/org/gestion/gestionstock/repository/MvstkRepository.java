package org.gestion.gestionstock.repository;

import org.gestion.gestionstock.model.Mvstk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface MvstkRepository extends JpaRepository<Mvstk, Integer> {

    @Query("select sum(m.quantite) from Mvstk m where m.article.id = :idArticle")
    BigDecimal stockReelArticle(@Param("idArticle") Integer idArticle);

    List<Mvstk> findAllByArticleId(Integer idArticle);
}
