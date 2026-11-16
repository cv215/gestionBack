package org.gestion.gestionstock.dto;

import lombok.Builder;
import lombok.Data;
import org.gestion.gestionstock.model.LigneVente;


import java.math.BigDecimal;

@Builder
@Data
public class LigneVenteDto {
    private Integer id;

    private Integer idEntreprise;

    private Integer idArticle;

    private VentesDto vente;

    private BigDecimal quantite;

    private BigDecimal prixUnitaire;

    public static LigneVenteDto fromEntity(LigneVente ligneVente){
        if (ligneVente == null){
            return  null;
        }
         return LigneVenteDto.builder()
                .id(ligneVente.getId())
                .quantite(ligneVente.getQuantite())
                 .idEntreprise(ligneVente.getIdEntreprise())
                .prixUnitaire(ligneVente.getPrixUnitaire())
                 .idArticle(ligneVente.getArticle() != null ? ligneVente.getArticle().getId() : null)
                 .vente(VentesDto.fromEntity(ligneVente.getVente()))
                .build();
    }
    /** Mapping sans la vente parente, pour eviter la recursion Vente -> Lignes -> Vente. */
    public static LigneVenteDto fromEntityWithoutVente(LigneVente ligneVente){
        if (ligneVente == null){
            return null;
        }
        return LigneVenteDto.builder()
                .id(ligneVente.getId())
                .quantite(ligneVente.getQuantite())
                .idEntreprise(ligneVente.getIdEntreprise())
                .prixUnitaire(ligneVente.getPrixUnitaire())
                .idArticle(ligneVente.getArticle() != null ? ligneVente.getArticle().getId() : null)
                .build();
    }

    public static LigneVente toEntity(LigneVenteDto ligneVenteDto){
        if (ligneVenteDto == null){
            return null;
        }
        LigneVente ligneVente = new LigneVente();
        ligneVente.setId(ligneVenteDto.getId());
        ligneVente.setIdEntreprise(ligneVenteDto.getIdEntreprise());
        if (ligneVenteDto.getIdArticle() != null) {
            org.gestion.gestionstock.model.Article article = new org.gestion.gestionstock.model.Article();
            article.setId(ligneVenteDto.getIdArticle());
            ligneVente.setArticle(article);
        }
        ligneVente.setQuantite(ligneVenteDto.getQuantite());
        ligneVente.setPrixUnitaire(ligneVenteDto.getPrixUnitaire());
        ligneVente.setVente(VentesDto.toEntity(ligneVenteDto.getVente()));
        return ligneVente;
    }

}
