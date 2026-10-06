package org.gestion.gestionstock.dto;



import lombok.Builder;
import lombok.Data;
import org.gestion.gestionstock.model.Article;
import org.gestion.gestionstock.model.LigneCommandeClient;
import org.gestion.gestionstock.model.LigneCommandeFournisseur;
import org.gestion.gestionstock.model.LigneVente;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Data
public class ArticleDto {

    private Integer id;

    private Integer idEntreprise;

    private String codeArticle;

    private String designation;

    private BigDecimal prixUnitaireHt;

    private BigDecimal prixUnitaireTtc;

    private BigDecimal tauxTva;

    private String photo;

    private CathegoryDto cathegory;

    private List<LigneVente> ligneVentes;

    private List<LigneCommandeClient> ligneCommandeClients;

    private List<LigneCommandeFournisseur> ligneCommandeFournisseurs;

    public static ArticleDto fromEntity(Article article){
        if (article == null){
            return null;
        }
        return ArticleDto.builder()
                .id(article.getId())
                .codeArticle(article.getCodeArticle())
                .designation(article.getDesignation())
                .prixUnitaireHt(article.getPrixUnitaireHt())
                .tauxTva(article.getTauxTva())
                .idEntreprise(article.getIdEntreprise())
                .prixUnitaireTtc(article.getPrixUnitaireTtc())
                .photo(article.getPhoto())
                .cathegory(CathegoryDto.fromEntity(article.getCathegory()))
                .build();
    }
    public static Article toEntity(ArticleDto articleDto){
        if (articleDto == null){
            return  null;
        }
        Article article = new Article();
        article.setCodeArticle(articleDto.getCodeArticle());
        article.setDesignation(articleDto.getDesignation());
        article.setPrixUnitaireHt(articleDto.getPrixUnitaireHt());
        article.setTauxTva(articleDto.getTauxTva());
        article.setPrixUnitaireTtc(articleDto.getPrixUnitaireTtc());
        article.setPhoto(articleDto.getPhoto());
        article.setCathegory(CathegoryDto.toEntity(articleDto.getCathegory()));
        return article;
    }
}
