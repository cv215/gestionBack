package org.gestion.gestionstock.dto;

import lombok.Builder;
import lombok.Data;
import org.gestion.gestionstock.model.Mvstk;
import org.gestion.gestionstock.model.SourceMvstk;
import org.gestion.gestionstock.model.TypeMvstk;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
@Data
public class MvstkDto {

    private Integer id;

    private Integer idEntreprise;

    private Instant dateMvt;

    private BigDecimal quantite;

    private ArticleDto article;

    private TypeMvstk typeMvstkt;

    private SourceMvstk sourceMvstk;

    public static MvstkDto fromEntity(Mvstk mvstk){
        if (mvstk == null){
            return null;
        }
        return MvstkDto.builder()
                .id(mvstk.getId())
                .dateMvt(mvstk.getDateMvt())
                .idEntreprise(mvstk.getIdEntreprise())
                .quantite(mvstk.getQuantite())
                .article(ArticleDto.fromEntity(mvstk.getArticle()))
                .typeMvstkt(mvstk.getTypeMvt())
                .sourceMvstk(mvstk.getSourceMvstk())
                .build();
    }
    public static Mvstk toEntity(MvstkDto mvstkDto){
        if (mvstkDto == null){
            return null;
        }
        Mvstk mvstk = new Mvstk();
        mvstk.setId(mvstkDto.getId());
        mvstk.setDateMvt(mvstkDto.getDateMvt());
        mvstk.setQuantite(mvstkDto.getQuantite());
        mvstk.setArticle(ArticleDto.toEntity(mvstkDto.getArticle()));
        mvstk.setTypeMvt(mvstkDto.getTypeMvstkt());
        mvstk.setIdEntreprise(mvstkDto.getIdEntreprise());
        mvstk.setSourceMvstk(mvstkDto.getSourceMvstk());
        return mvstk;
    }
}
