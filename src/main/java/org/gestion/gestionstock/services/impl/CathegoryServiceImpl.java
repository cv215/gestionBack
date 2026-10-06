package org.gestion.gestionstock.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.gestion.gestionstock.dto.CathegoryDto;
import org.gestion.gestionstock.exception.EntityNotFoundException;
import org.gestion.gestionstock.exception.ErrorCodes;
import org.gestion.gestionstock.exception.InvalidEntityException;
import org.gestion.gestionstock.exception.InvalidOperationException;
import org.gestion.gestionstock.model.*;
import org.gestion.gestionstock.repository.ArticleRepository;
import org.gestion.gestionstock.repository.CathegoryRepository;
import org.gestion.gestionstock.services.CathegoryService;
import org.gestion.gestionstock.validator.CathegoryValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CathegoryServiceImpl implements CathegoryService {
    private final CathegoryRepository cathegoryRepository;
    private ArticleRepository articleRepository;
    @Autowired
    public CathegoryServiceImpl(CathegoryRepository cathegoryRepository, ArticleRepository articleRepository) {
        this.cathegoryRepository = cathegoryRepository;
        this.articleRepository = articleRepository;
    }

    @Override
    public CathegoryDto save(CathegoryDto dto) {
        List<String> errors = CathegoryValidator.Validate(dto);
        if (!errors.isEmpty()){
            log.error("cathegory is not valid {}", dto);
            throw new InvalidEntityException("la cathegory n'est pas valide", ErrorCodes.CATHEGORY_NOT_FOUND, errors);
        }
        return CathegoryDto.fromEntity(
                cathegoryRepository.save(
                        CathegoryDto.toEntity(dto)
                )
        );
    }

    @Override
    public CathegoryDto findById(Integer id) {
        if (id== null){
            log.error("cathegory ID is null");
            return null;
        }
        return cathegoryRepository.findById(id)
                .map(CathegoryDto::fromEntity)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                "Aucune cathegory avec l'ID = " + id + "n'a été trouvé dans la BBD",
                                ErrorCodes.CATHEGORY_NOT_FOUND)
                );
    }

    @Override
    public CathegoryDto findByCode(String code) {
        if (!StringUtils.hasLength(code)){
            log.error("cathegory CODE is null");
            return null;
        }
        Optional<Cathegory> cathegory = cathegoryRepository.findByCode(code);

        return Optional.of(CathegoryDto.fromEntity(cathegory.get())).orElseThrow(
                () -> new EntityNotFoundException(
                        "Aucune cathegory avec le CODE = " + code + "n'a été trouvé dans la BBD",
                        ErrorCodes.CATHEGORY_NOT_FOUND)
        );
    }

    @Override
    public List<CathegoryDto> findAll() {
        return cathegoryRepository.findAll().stream()
                .map(CathegoryDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        if (id== null){
            log.error("article ID is null");
            return ;
        }
        List<Article> articles = articleRepository.findAllByCathegoryId(id);
        if (!articles.isEmpty()){
            throw new InvalidOperationException("Impossible de supprimer cette cathegory qui est déjà utilisé",ErrorCodes.CATHEGORY_ALREADY_IN_USE);
        }
        cathegoryRepository.deleteById(id);
    }

}
