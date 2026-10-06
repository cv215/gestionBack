package org.gestion.gestionstock.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.gestion.gestionstock.dto.MvstkDto;
import org.gestion.gestionstock.exception.ErrorCodes;
import org.gestion.gestionstock.exception.InvalidEntityException;
import org.gestion.gestionstock.model.TypeMvstk;
import org.gestion.gestionstock.repository.MvstkRepository;
import org.gestion.gestionstock.services.ArticleService;
import org.gestion.gestionstock.services.MvstkService;
import org.gestion.gestionstock.validator.MvstkValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MvstkServiceImpl implements MvstkService {

    private MvstkRepository repository;
    private ArticleService articleService;

    @Autowired
    public MvstkServiceImpl(ArticleService articleService, MvstkRepository repository) {
        this.articleService = articleService;
        this.repository = repository;
    }

    @Override
    public BigDecimal stockReelArticle(Integer idArticle) {
        if (idArticle == null){
            log.warn("ID article is null");
            return BigDecimal.valueOf(-1);
        }
        articleService.findById(idArticle);
        return repository.stockReelArticle(idArticle);
    }

    @Override
    public List<MvstkDto> mvstkArticle(Integer idArticle) {
        return repository.findAllByArticleId(idArticle).stream()
                .map(MvstkDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public MvstkDto entreeStock(MvstkDto dto) {
        List<String> errors = MvstkValidator.Validate(dto);
        if (!errors.isEmpty()){
            log.error("mouvement stock is not valid {}", dto);
            throw new InvalidEntityException("le mouvement de stock n'est pas valide", ErrorCodes.MVT_STK_NOT_VALID, errors);
        }
        dto.setQuantite(
                BigDecimal.valueOf(
                        Math.abs(dto.getQuantite().doubleValue()))
        );
        dto.setTypeMvstkt(TypeMvstk.ENTREE);
        return MvstkDto.fromEntity(
                repository.save(MvstkDto.toEntity(dto))
        );
    }

    @Override
    public MvstkDto sortieStock(MvstkDto dto) {
        List<String> errors = MvstkValidator.Validate(dto);
        if (!errors.isEmpty()){
            log.error("mouvement stock is not valid {}", dto);
            throw new InvalidEntityException("le mouvement de stock n'est pas valide", ErrorCodes.MVT_STK_NOT_VALID, errors);
        }
        dto.setQuantite(
                BigDecimal.valueOf(
                        Math.abs(dto.getQuantite().doubleValue())*-1)
        );
        dto.setTypeMvstkt(TypeMvstk.SORTIE);
        return MvstkDto.fromEntity(
                repository.save(MvstkDto.toEntity(dto))
        );
    }

    @Override
    public MvstkDto correctionStockPos(MvstkDto dto) {
        List<String> errors = MvstkValidator.Validate(dto);
        if (!errors.isEmpty()){
            log.error("mouvement stock is not valid {}", dto);
            throw new InvalidEntityException("le mouvement de stock n'est pas valide", ErrorCodes.MVT_STK_NOT_VALID, errors);
        }
        dto.setQuantite(
                BigDecimal.valueOf(
                        Math.abs(dto.getQuantite().doubleValue()))
        );
        dto.setTypeMvstkt(TypeMvstk.CORRECTION_POS);
        return MvstkDto.fromEntity(
                repository.save(MvstkDto.toEntity(dto))
        );
    }

    @Override
    public MvstkDto correctionStockNeg(MvstkDto dto) {
        List<String> errors = MvstkValidator.Validate(dto);
        if (!errors.isEmpty()){
            log.error("mouvement stock is not valid {}", dto);
            throw new InvalidEntityException("le mouvement de stock n'est pas valide", ErrorCodes.MVT_STK_NOT_VALID, errors);
        }
        dto.setQuantite(
                BigDecimal.valueOf(
                        Math.abs(dto.getQuantite().doubleValue())*-1)
        );
        dto.setTypeMvstkt(TypeMvstk.CORRECTION_NEG);
        return MvstkDto.fromEntity(
                repository.save(MvstkDto.toEntity(dto))
        );
    }

    private MvstkDto entreePositive(MvstkDto dto, TypeMvstk typeMvstk) {
        List<String> errors = MvstkValidator.Validate(dto);
        if (!errors.isEmpty()){
            log.error("mouvement stock is not valid {}", dto);
            throw new InvalidEntityException("le mouvement de stock n'est pas valide", ErrorCodes.MVT_STK_NOT_VALID, errors);
        }
        dto.setQuantite(
                BigDecimal.valueOf(
                        Math.abs(dto.getQuantite().doubleValue()))
        );
        dto.setTypeMvstkt(typeMvstk);
        return MvstkDto.fromEntity(
                repository.save(MvstkDto.toEntity(dto))
        );
    }

    private MvstkDto sortieNegative(MvstkDto dto, TypeMvstk typeMvstk) {
        List<String> errors = MvstkValidator.Validate(dto);
        if (!errors.isEmpty()){
            log.error("mouvement stock is not valid {}", dto);
            throw new InvalidEntityException("le mouvement de stock n'est pas valide", ErrorCodes.MVT_STK_NOT_VALID, errors);
        }
        dto.setQuantite(
                BigDecimal.valueOf(
                        Math.abs(dto.getQuantite().doubleValue())*-1)
        );
        dto.setTypeMvstkt(typeMvstk);
        return MvstkDto.fromEntity(
                repository.save(MvstkDto.toEntity(dto))
        );
    }
}
