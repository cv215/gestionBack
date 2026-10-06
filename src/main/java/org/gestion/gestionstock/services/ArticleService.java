package org.gestion.gestionstock.services;

import org.gestion.gestionstock.dto.ArticleDto;
import org.gestion.gestionstock.dto.LigneCommandeClientDto;
import org.gestion.gestionstock.dto.LigneCommandeFournisseurDto;
import org.gestion.gestionstock.dto.LigneVenteDto;

import java.util.List;
public interface ArticleService {
    ArticleDto save(ArticleDto dto);

    ArticleDto findById(Integer id);

    ArticleDto findByCodeArticle(String codeArticle);

    List<ArticleDto> findAll();

    List<LigneVenteDto> findHistoriqueVentes(Integer idArticle);

    List<LigneCommandeClientDto> findHistoriqueCommandeClient(Integer idArticle);

    List<LigneCommandeFournisseurDto> findHistoriqueCommandeFournisseur(Integer idArticle);

    List<ArticleDto> findAllArticleByIdCathegory(Integer idCathegory);

    void delete(Integer id);
}
