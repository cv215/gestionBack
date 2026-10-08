package org.gestion.gestionstock.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.gestion.gestionstock.dto.ChangerMotDePasseUtilisateurDto;
import org.gestion.gestionstock.dto.UtilisateurDto;
import org.gestion.gestionstock.exception.EntityNotFoundException;
import org.gestion.gestionstock.exception.ErrorCodes;
import org.gestion.gestionstock.exception.InvalidEntityException;
import org.gestion.gestionstock.exception.InvalidOperationException;
import org.gestion.gestionstock.model.Utilisateur;
import org.gestion.gestionstock.repository.UtilisateurRepository;
import org.gestion.gestionstock.services.UtilisateurService;
import org.gestion.gestionstock.validator.UtilisateurValidator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UtilisateurServiceImpl implements UtilisateurService{

    private final UtilisateurRepository utilisateurRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UtilisateurServiceImpl(UtilisateurRepository utilisateurRepository, BCryptPasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UtilisateurDto save(UtilisateurDto utilisateurDto) {
        List<String> errors = UtilisateurValidator.validate(utilisateurDto);
        if (!errors.isEmpty()) {
            log.error("fournisseur is not valid {}", utilisateurDto);
            throw new InvalidEntityException("L'utilisateur n'est pas valide", ErrorCodes.UTILISATEUR_NOT_VALID, errors);
        }
        String email = utilisateurDto.getEmail().trim().toLowerCase(Locale.ROOT);
        Optional<Utilisateur> utilisateurDtoOptional = this.utilisateurRepository.findUtilisateurByEmail(email);
        if (utilisateurDtoOptional.isPresent()) {
            throw new InvalidEntityException(
                    "Cette adresse e-mail est déjà utilisée",
                    ErrorCodes.UTILISATEUR_ALREADY_EXISTS,
                    List.of("Cette adresse e-mail est déjà utilisée")
            );
        }

        utilisateurDto.setEmail(email);
        utilisateurDto.setMotDePasse(this.passwordEncoder.encode(utilisateurDto.getMotDePasse()));
        utilisateurDto.setActif(true);

        return UtilisateurDto.fromEntity(
                utilisateurRepository.save(
                        UtilisateurDto.toEntity(utilisateurDto)
                )
        );
    }

    @Override
    public UtilisateurDto findById(Integer id) {
        if (id == null){
            log.error("utilisateur ID is null");
            return null;
        }
        Optional<Utilisateur> utilisateur = utilisateurRepository.findById(id);
        return Optional.of(UtilisateurDto.fromEntity(utilisateur.get())).orElseThrow(
                () -> new EntityNotFoundException(
                        "Aucun utilisateur avec l'ID = " + id + "n'a été trouvé dans la BBD",
                        ErrorCodes.UTILISATEUR_NOT_FOUND)
        );
    }

    @Override
    public List<UtilisateurDto> findAll() {

        return utilisateurRepository.findAll().stream()
                .map(UtilisateurDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        if (id== null){
            log.error("utilisateur ID is null");
            return ;
        }
        utilisateurRepository.deleteById(id);
    }

    @Override
    public UtilisateurDto findByEmail(String email) {
        return utilisateurRepository.findUtilisateurByEmail(email)
                .map(UtilisateurDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Aucun utilisateur avec l'email = " +email + "n'a été trouvé dans la BDD",ErrorCodes.UTILISATEUR_NOT_FOUND)
                );
    }

    @Override
    public UtilisateurDto changerMotDePasse(ChangerMotDePasseUtilisateurDto dto) {
       validate(dto);
       Optional<Utilisateur> utilisateurOptional = utilisateurRepository.findUtilisateurByEmail(dto.getEmail());
       if (utilisateurOptional.isEmpty()){
           log.warn("Aucun utilisateur n'a été trouvé avec l'Email" +dto.getEmail());
           throw new EntityNotFoundException("Aucun utilisateur n'a été trouvé avec l'Email" +dto.getEmail(), ErrorCodes.UTILISATEUR_NOT_FOUND);
       }
       Utilisateur utilisateur = utilisateurOptional.get();
       utilisateur.setMotDePasse(dto.getNouveauMotDePasse());
        return UtilisateurDto.fromEntity(
                utilisateurRepository.save(utilisateur)
        );
    }

    private void validate(ChangerMotDePasseUtilisateurDto dto) {
        if (dto == null){
            log.warn("Impossible de modifier le mot de passe avec un object null");
            throw new InvalidOperationException("Aucune information n'a été fourni pour pouvoir changer le mot de passe",
                    ErrorCodes.UTILISATEUR_CHANGE_PASSWORD_OBJECT_NOT_VALID);
        }
        if (dto.getEmail() == null){
            log.warn("Impossible de modifier le mot de passe avec un email null");
            throw new InvalidOperationException("Aucun email n'a été fournit impossible de changer le mot de passe",
                    ErrorCodes.UTILISATEUR_CHANGE_PASSWORD_OBJECT_NOT_VALID);
        }
        if (!StringUtils.hasLength(dto.getNouveauMotDePasse()) || !StringUtils.hasLength(dto.getConfirmerMotDePasse())){
            log.warn("Impossible de modifier le mot de passe avec un mot de passe vide ou null");
            throw new InvalidOperationException("Mot de passe utilisateur null:: Impossible de modifier le mot de passe",
                    ErrorCodes.UTILISATEUR_CHANGE_PASSWORD_OBJECT_NOT_VALID);
        }
        if (!dto.getNouveauMotDePasse().equals(dto.getConfirmerMotDePasse())){
            log.warn("Impossible de modifier le mot de passe avec deux mot de passe différent");
            throw new InvalidOperationException("Mot de passe utilisateur non conforme:: Impossible de modifier le mot de passe",
                    ErrorCodes.UTILISATEUR_CHANGE_PASSWORD_OBJECT_NOT_VALID);
        }
    }

}
