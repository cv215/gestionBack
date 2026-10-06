package org.gestion.gestionstock.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.gestion.gestionstock.dto.ArticleDto;
import org.gestion.gestionstock.dto.LigneVenteDto;
import org.gestion.gestionstock.dto.MvstkDto;
import org.gestion.gestionstock.dto.VentesDto;
import org.gestion.gestionstock.exception.EntityNotFoundException;
import org.gestion.gestionstock.exception.ErrorCodes;
import org.gestion.gestionstock.exception.InvalidEntityException;
import org.gestion.gestionstock.exception.InvalidOperationException;
import org.gestion.gestionstock.model.*;
import org.gestion.gestionstock.repository.ArticleRepository;
import org.gestion.gestionstock.repository.LigneVenteRepository;
import org.gestion.gestionstock.repository.VentesRepository;
import org.gestion.gestionstock.services.MvstkService;
import org.gestion.gestionstock.services.VentesService;
import org.gestion.gestionstock.validator.VentesValidator;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class VentesServiceImpl implements VentesService {
    private final ArticleRepository articleRepository;
    private final VentesRepository ventesRepository;
    private final LigneVenteRepository ligneVenteRepository;
    private MvstkService mvstkService;

    @Autowired
    public VentesServiceImpl(ArticleRepository articleRepository, LigneVenteRepository ligneVenteRepository, VentesRepository ventesRepository, MvstkService mvstkService) {
        this.articleRepository = articleRepository;
        this.ligneVenteRepository = ligneVenteRepository;
        this.ventesRepository = ventesRepository;
        this.mvstkService = mvstkService;
    }

    @Override
    public void delete(Integer id) {
        if (id == null){
            log.error("fournisseur ID is null");
            return;
        }
        List<LigneVente> ligneVentes = ligneVenteRepository.findAllByVenteId(id);
        if (!ligneVentes.isEmpty()){
            throw new InvalidOperationException("Impossible de supprimer une vente déjà utilisé dans les commandes",ErrorCodes.VENTES_ALREADY_IN_USE);
        }
        ventesRepository.deleteById(id);
    }

    @Override
    public VentesDto save(VentesDto dto) {
        List<String> errors = VentesValidator.validate(dto);
        if (!errors.isEmpty()){
            log.error("ventes n'est pas valide");
            throw new InvalidEntityException("l'object vente n'est pas valide", ErrorCodes.VENTES_NOT_VALID, errors);
        }
        List<String> articleErrors = new ArrayList<>();
        dto.getLigneVentes().forEach(ligneVenteDto -> {
            Optional<Article> article = articleRepository.findById(ligneVenteDto.getIdArticle());
            if (article.isEmpty()){
                articleErrors.add("aucun article avec l'ID" + ligneVenteDto.getIdArticle() + "n'a ete trouve dans la BDD");
            }
        });
        if (!articleErrors.isEmpty()){
            log.error("one or more articles were not found in the DB, {}", errors);
            throw new InvalidEntityException("plusieurs articles non pas ete trouvé dans la BDD",ErrorCodes.VENTES_NOT_VALID, errors);
        }
        Ventes saveVentes = ventesRepository.save(VentesDto.toEntity(dto));
        dto.getLigneVentes().forEach(ligneVenteDto -> {
            LigneVente ligneVente = LigneVenteDto.toEntity(ligneVenteDto);
            ligneVente.setVente(saveVentes);
            ligneVenteRepository.save(ligneVente);
            updateMvstk(ligneVente);
        });
        return VentesDto.fromEntity(saveVentes);
    }

    @Override
    public VentesDto findById(Integer id) {
        if (id == null){
            log.error("ventes ID is null");
            return null;
        }
        return ventesRepository.findById(id)
                .map(VentesDto::fromEntity)
                .orElseThrow(()-> new EntityNotFoundException("aucun vente n'a été trouvé dans la BDD", ErrorCodes.VENTES_NOT_FOUND));
    }

    @Override
    public VentesDto findByCode(String code) {
        if (!StringUtils.hasLength(code)){
            return null;
        }
        return ventesRepository.findVentesByCode(code)
                .map(VentesDto::fromEntity)
                .orElseThrow(()-> new EntityNotFoundException(
                        "aucune vente n'a ete trouve avec le code" +code, ErrorCodes.VENTES_NOT_FOUND
                ));
    }

    @Override
    public List<VentesDto> findAll() {
        return ventesRepository.findAll().stream()
                .map(VentesDto::fromEntity)
                .collect(Collectors.toList());
    }

    private void updateMvstk(LigneVente lig) {

            MvstkDto mvstkDto = MvstkDto.builder()
                    .article(ArticleDto.fromEntity(lig.getArticle()))
                    .dateMvt(Instant.now())
                    .typeMvstkt(TypeMvstk.SORTIE)
                    .sourceMvstk(SourceMvstk.VENTE)
                    .quantite(lig.getQuantite())
                    .idEntreprise(lig.getIdEntreprise())
                    .build();
            mvstkService.sortieStock(mvstkDto);
    };

}
