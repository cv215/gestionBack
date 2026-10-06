package org.gestion.gestionstock.services;

import org.gestion.gestionstock.dto.MvstkDto;

import java.math.BigDecimal;
import java.util.List;

public interface MvstkService {

    BigDecimal stockReelArticle(Integer idArticle);

    List<MvstkDto> mvstkArticle(Integer idArticle);

    MvstkDto entreeStock(MvstkDto dto);

    MvstkDto sortieStock(MvstkDto dto);

    MvstkDto correctionStockPos(MvstkDto dto);

    MvstkDto correctionStockNeg(MvstkDto dto);
}
