package org.gestion.gestionstock.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.gestion.gestionstock.dto.EntrepriseDto;
import org.gestion.gestionstock.exception.EntityNotFoundException;
import org.gestion.gestionstock.exception.ErrorCodes;
import org.gestion.gestionstock.exception.InvalidEntityException;
import org.gestion.gestionstock.model.Entreprise;
import org.gestion.gestionstock.model.Roles;
import org.gestion.gestionstock.model.Utilisateur;
import org.gestion.gestionstock.repository.EntrepriseRepository;
import org.gestion.gestionstock.repository.UtilisateurRepository;
import org.gestion.gestionstock.services.EntrepriseService;
import org.gestion.gestionstock.utils.TypeRole;
import org.gestion.gestionstock.validator.EntrepriseValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EntrepriseServiceImpl implements EntrepriseService {
    private final EntrepriseRepository entrepriseRepository;
    private final UtilisateurRepository utilisateurRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public EntrepriseServiceImpl(EntrepriseRepository entrepriseRepository,UtilisateurRepository utilisateurRepository, BCryptPasswordEncoder passwordEncoder) {
        this.entrepriseRepository = entrepriseRepository;
        this.passwordEncoder = passwordEncoder;
        this.utilisateurRepository = utilisateurRepository;
    }


    @Override
    @Transactional
    public EntrepriseDto save(EntrepriseDto dto) {
        List<String> errors = EntrepriseValidator.validate(dto);
        if (!errors.isEmpty()){
            log.error("l'entreprise is not valid {}", dto);
            throw new InvalidEntityException("l'entreprise n'est pas valide", ErrorCodes.ENTREPRISE_NOT_VALID, errors);
        }
        if (!org.springframework.util.StringUtils.hasText(dto.getMotDePasse()) || dto.getMotDePasse().length() < 8) {
            throw new InvalidEntityException("l'entreprise n'est pas valide", ErrorCodes.ENTREPRISE_NOT_VALID,
                    List.of("Le mot de passe doit contenir au moins 8 caractères"));
        }
        String email = dto.getEmail().trim().toLowerCase(java.util.Locale.ROOT);
        if (utilisateurRepository.findUtilisateurByEmail(email).isPresent()) {
            throw new InvalidEntityException("Cette adresse e-mail est déjà utilisée", ErrorCodes.UTILISATEUR_ALREADY_EXISTS,
                    List.of("Cette adresse e-mail est déjà utilisée"));
        }
        dto.setEmail(email);
        dto.setMotDePasse(this.passwordEncoder.encode(dto.getMotDePasse()));
        Entreprise entreprise = EntrepriseDto.toEntity(dto);
        entreprise = entrepriseRepository.save(entreprise);

        Utilisateur  utilisateur = new Utilisateur();
        utilisateur.setEmail(dto.getEmail());
        utilisateur.setMotDePasse(dto.getMotDePasse());
        utilisateur.setNom(dto.getNomResponsable());
        utilisateur.setAdresse(entreprise.getAdresse());
        utilisateur.setEntreprise(entreprise);
        utilisateur.setActif(true);

        Roles adminRole = new Roles();
        adminRole.setIdEntreprise(entreprise.getId());
        adminRole.setRoleName(TypeRole.ADMIN_ENTREPRISE.name());
        adminRole.setLibelle(TypeRole.ADMIN_ENTREPRISE);
        adminRole.setUtilisateur(utilisateur);
        utilisateur.setRoles(new java.util.ArrayList<>(List.of(adminRole)));

        utilisateurRepository.save(utilisateur);

        return EntrepriseDto.fromEntity(entreprise);

        /*dto.setEmail(dto.getEmail());

        return EntrepriseDto.fromEntity(
                entrepriseRepository.save(
                        EntrepriseDto.toEntity(dto)
                )
        );
        */
    }

    @Override
    public EntrepriseDto findById(Integer id) {
        if (id== null){
            log.error("entreprise ID is null");
            return null;
        }

        return entrepriseRepository.findById(id)
                .map(EntrepriseDto::fromEntity)
                .orElseThrow(
                () -> new EntityNotFoundException(
                        "Aucune entreprise avec l'ID = " + id + "n'a été trouvé dans la BBD",
                        ErrorCodes.ENTREPRISE_NOT_FOUND)
        );
    }

    @Override
    public List<EntrepriseDto> findAll() {
        return entrepriseRepository.findAll().stream()
                .map(EntrepriseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        if (id== null){
            log.error("Entreprise ID is null");
            return ;
        }
        entrepriseRepository.deleteById(id);
    }
    }
