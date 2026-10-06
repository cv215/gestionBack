package org.gestion.gestionstock.services;

import org.gestion.gestionstock.dto.CathegoryDto;

import java.util.List;

public interface CathegoryService {
    CathegoryDto save(CathegoryDto dto);

    CathegoryDto findById(Integer id);

    CathegoryDto findByCode(String code);

    List<CathegoryDto> findAll();

    void delete(Integer id);
}
