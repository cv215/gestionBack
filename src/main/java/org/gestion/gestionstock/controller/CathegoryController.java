package org.gestion.gestionstock.controller;

import org.gestion.gestionstock.controller.api.CathegoryApi;
import org.gestion.gestionstock.dto.CathegoryDto;
import org.gestion.gestionstock.services.CathegoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CathegoryController implements CathegoryApi {

    private final CathegoryService cathegoryService;

    @Autowired
    public CathegoryController(CathegoryService cathegoryService) {
        this.cathegoryService = cathegoryService;
    }

    @Override
    public void delete(Integer id) {
        cathegoryService.delete(id);
    }

    @Override
    public CathegoryDto save(CathegoryDto dto) {
        return cathegoryService.save(dto);
    }

    @Override
    public CathegoryDto findById(Integer idCathegory) {
        return cathegoryService.findById(idCathegory);
    }

    @Override
    public CathegoryDto findByCode(String code) {
        return cathegoryService.findByCode(code);
    }

    @Override
    public List<CathegoryDto> findAll() {
        return List.of();
    }
}
