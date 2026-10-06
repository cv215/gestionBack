package org.gestion.gestionstock.dto;

import lombok.Builder;
import lombok.Data;
import org.gestion.gestionstock.model.Ventes;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Data
public class VentesDto {

    private Integer id;

    private Integer idEntreprise;

    private String code;

    private Instant dateVente;

    private String commentaire;

    private List<LigneVenteDto> ligneVentes;


    public static VentesDto fromEntity(Ventes ventes){
        if (ventes == null){
            return null;
        }
        return VentesDto.builder()
                .id(ventes.getId())
                .code(ventes.getCode())
                .idEntreprise(ventes.getIdEntreprise())
                .dateVente(ventes.getDateVente())
                .commentaire(ventes.getCommentaire())
                .ligneVentes(ventes.getLigneVentes().stream()
                        .map(LigneVenteDto::fromEntity).collect(Collectors.toList()))
                .build();
    }
    public static Ventes toEntity(VentesDto ventesDto){
        if (ventesDto == null){
            return null;
        }
        Ventes ventes = new Ventes();
        ventes.setCode(ventes.getCode());
        ventes.setDateVente(ventesDto.getDateVente());
        ventes.setCommentaire(ventesDto.getCommentaire());
        ventes.setLigneVentes(ventes.getLigneVentes());
        return ventes;
    }
}
