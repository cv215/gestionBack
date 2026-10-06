package org.gestion.gestionstock.controller;

import org.gestion.gestionstock.controller.api.MvstkApi;
import org.gestion.gestionstock.dto.MvstkDto;
import org.gestion.gestionstock.services.MvstkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
@RestController
public class MvstkController implements MvstkApi {

    private MvstkService service;

    @Autowired
    public MvstkController(MvstkService service) {
        this.service = service;
    }

    @Override
    public BigDecimal stockReelArticle(Integer idArticle) {
        return service.stockReelArticle(idArticle);
    }

    @Override
    public List<MvstkDto> mvstkArticle(Integer idArticle) {
        return service.mvstkArticle(idArticle);
    }

    @Override
    public MvstkDto entreeStock(MvstkDto dto) {
        return service.entreeStock(dto);
    }

    @Override
    public MvstkDto sortieStock(MvstkDto dto) {
        return service.sortieStock(dto);
    }

    @Override
    public MvstkDto correctionStockPos(MvstkDto dto) {
        return service.correctionStockPos(dto);
    }

    @Override
    public MvstkDto correctionStockNeg(MvstkDto dto) {
        return service.correctionStockNeg(dto);
    }
}
