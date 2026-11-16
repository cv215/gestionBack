package org.gestion.gestionstock.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.gestion.gestionstock.dto.FournisseurDto;
import org.gestion.gestionstock.exception.EntityNotFoundException;
import org.gestion.gestionstock.exception.ErrorCodes;
import org.gestion.gestionstock.exception.InvalidEntityException;
import org.gestion.gestionstock.exception.InvalidOperationException;
import org.gestion.gestionstock.model.CommandeFournisseur;
import org.gestion.gestionstock.model.Fournisseur;
import org.gestion.gestionstock.repository.CommandeFournisseurRepository;
import org.gestion.gestionstock.repository.FournisseurRepository;
import org.gestion.gestionstock.services.FournisseurService;
import org.gestion.gestionstock.validator.FournisseurValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FournisseurServiceImpl implements FournisseurService {

    @org.springframework.beans.factory.annotation.Autowired
    private org.gestion.gestionstock.config.CurrentTenant currentTenant;


    private final FournisseurRepository fournisseurRepository;
    private final CommandeFournisseurRepository commandeFournisseurRepository;

    @Autowired
    public FournisseurServiceImpl(FournisseurRepository fournisseurRepository, CommandeFournisseurRepository commandeFournisseurRepository) {
        this.fournisseurRepository = fournisseurRepository;
        this.commandeFournisseurRepository = commandeFournisseurRepository;
    }

    @Override
    public FournisseurDto save(FournisseurDto fournisseurDto) {
        if (fournisseurDto != null) {
            fournisseurDto.setIdEntreprise(currentTenant.resolve(fournisseurDto.getIdEntreprise()));
        }
        List<String> errors = FournisseurValidator.validate(fournisseurDto);
        if (!errors.isEmpty()){
            log.error("fournisseur is not valid {}", fournisseurDto);
            throw new InvalidEntityException("le fournisseur n'est pas valide", ErrorCodes.FOURNISSEUR_NOT_VALID, errors);
        }
        return FournisseurDto.fromEntity(
                fournisseurRepository.save(
                        FournisseurDto.toEntity(fournisseurDto)
                )
        );
    }

    @Override
    public FournisseurDto findById(Integer id) {
        if (id == null){
            log.error("fournisseur ID is null");
            return null;
        }
        return fournisseurRepository.findById(id)
                .map(FournisseurDto::fromEntity)
                .orElseThrow(
                        () -> new EntityNotFoundException("aucun fournisseur avec ID = "+ id + "n'a été trouvé dans la BDD",
                                ErrorCodes.FOURNISSEUR_NOT_FOUND)
                );
    }

    @Override
    public List<FournisseurDto> findAll() {
        List<FournisseurDto> fournisseurDtos = fournisseurRepository.findAll().stream()
                .filter(e -> currentTenant.owns(e.getIdEntreprise()))
                .map((Object fournisseur) -> FournisseurDto.fromEntity((Fournisseur) fournisseur))
                .collect(Collectors.toList());
        return fournisseurDtos;
    }

    @Override
    public void delete(Integer id) {
        if (id == null){
            log.error(" ID du fournisseur est null");
            return ;
        }
        List<CommandeFournisseur> commandeFournisseurs = commandeFournisseurRepository.findAllByFournisseurId(id);
        if (!commandeFournisseurs.isEmpty()){
            throw new InvalidOperationException("Impossible de supprimer ce fournisseur qui a déjà les commandes ",ErrorCodes.FOURNISSEUR_ALREADY_IN_USE);
        }
        fournisseurRepository.deleteById(id);
    }
}
