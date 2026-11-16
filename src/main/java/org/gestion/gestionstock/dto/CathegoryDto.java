package org.gestion.gestionstock.dto;


import lombok.Builder;
import lombok.Data;
import org.gestion.gestionstock.model.Cathegory;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Data
public class CathegoryDto {
    private Integer id;

    private Integer idEntreprise;

    private String code;

    private  String designation;

    @com.fasterxml.jackson.annotation.JsonProperty(access = com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY)
    private List<ArticleDto> articles;

    public static CathegoryDto fromEntity(Cathegory cathegory){
        if (cathegory == null){
            return null;
        }
        return CathegoryDto.builder()
                .id(cathegory.getId())
                .code(cathegory.getCode())
                .idEntreprise(cathegory.getIdEntreprise())
                .designation(cathegory.getDesignation())
                .build();
    }
    public static Cathegory toEntity(CathegoryDto dto){
        if (dto == null){
            return null;
        }
        Cathegory cathegory = new Cathegory();
        cathegory.setId(dto.getId());
        cathegory.setIdEntreprise(dto.getIdEntreprise());
        cathegory.setCode(dto.getCode());
        cathegory.setDesignation(dto.getDesignation());
        return cathegory;
    }
}
