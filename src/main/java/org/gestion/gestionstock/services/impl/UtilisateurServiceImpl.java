package org.gestion.gestionstock.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.gestion.gestionstock.dto.AdresseDto;
import org.gestion.gestionstock.dto.ChangerMotDePasseUtilisateurDto;
import org.gestion.gestionstock.dto.UtilisateurDto;
import org.gestion.gestionstock.exception.EntityNotFoundException;
import org.gestion.gestionstock.exception.ErrorCodes;
import org.gestion.gestionstock.exception.InvalidEntityException;
import org.gestion.gestionstock.exception.InvalidOperationException;
import org.gestion.gestionstock.model.Client;
import org.gestion.gestionstock.model.Entreprise;
import org.gestion.gestionstock.model.Fournisseur;
import org.gestion.gestionstock.model.Roles;
import org.gestion.gestionstock.model.Utilisateur;
import org.gestion.gestionstock.repository.UtilisateurRepository;
import org.gestion.gestionstock.services.UtilisateurService;
import org.gestion.gestionstock.utils.TypeRole;
import org.gestion.gestionstock.validator.UtilisateurValidator;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UtilisateurServiceImpl implements UtilisateurService{

    /** Rôles qu'un administrateur peut attribuer à un membre de son équipe. */
    private static final java.util.Set<TypeRole> ROLES_EQUIPE = java.util.EnumSet.of(
            TypeRole.ADMIN_ENTREPRISE, TypeRole.GESTIONNAIRE_STOCK, TypeRole.COMMERCIAL,
            TypeRole.CAISSIER, TypeRole.UTILISATEUR);

    private final UtilisateurRepository utilisateurRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final org.gestion.gestionstock.repository.EntrepriseRepository entrepriseRepository;
    private final org.gestion.gestionstock.repository.ClientRepository clientRepository;
    private final org.gestion.gestionstock.repository.FournisseurRepository fournisseurRepository;

    @org.springframework.beans.factory.annotation.Autowired
    public UtilisateurServiceImpl(UtilisateurRepository utilisateurRepository, BCryptPasswordEncoder passwordEncoder,
                                  org.gestion.gestionstock.repository.EntrepriseRepository entrepriseRepository,
                                  org.gestion.gestionstock.repository.ClientRepository clientRepository,
                                  org.gestion.gestionstock.repository.FournisseurRepository fournisseurRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.entrepriseRepository = entrepriseRepository;
        this.clientRepository = clientRepository;
        this.fournisseurRepository = fournisseurRepository;
    }

    /** Constructeur simplifié (tests unitaires sans inscription de partenaire). */
    public UtilisateurServiceImpl(UtilisateurRepository utilisateurRepository, BCryptPasswordEncoder passwordEncoder) {
        this(utilisateurRepository, passwordEncoder, null, null, null);
    }

    @Override
    @Transactional
    public UtilisateurDto save(UtilisateurDto utilisateurDto) {
        boolean creation = utilisateurDto == null || utilisateurDto.getId() == null;
        List<String> errors = UtilisateurValidator.validate(utilisateurDto, creation);
        if (!errors.isEmpty()) {
            log.error("utilisateur is not valid {}", utilisateurDto);
            throw new InvalidEntityException("L'utilisateur n'est pas valide", ErrorCodes.UTILISATEUR_NOT_VALID, errors);
        }
        String email = normalizeEmail(utilisateurDto.getEmail());
        Optional<Utilisateur> existingWithEmail = this.utilisateurRepository.findUtilisateurByEmail(email);
        if (existingWithEmail.isPresent() && !Objects.equals(existingWithEmail.get().getId(), utilisateurDto.getId())) {
            throw new InvalidEntityException(
                    "Cette adresse e-mail est déjà utilisée",
                    ErrorCodes.UTILISATEUR_ALREADY_EXISTS,
                    List.of("Cette adresse e-mail est déjà utilisée")
            );
        }

        Optional<Utilisateur> requester = currentUser();

        if (!creation) {
            if (requester.isEmpty()) {
                throw new InvalidOperationException("Vous devez être connecté pour modifier un utilisateur",
                        ErrorCodes.UTILISATEUR_NOT_VALID);
            }
            boolean soiMeme = utilisateurDto.getId().equals(requester.get().getId());
            if (!soiMeme && !hasRole(requester.get(), TypeRole.ADMIN_ENTREPRISE)) {
                throw new org.springframework.security.access.AccessDeniedException(
                        "Vous ne pouvez modifier que votre propre compte");
            }
            Utilisateur utilisateur = utilisateurRepository.findById(utilisateurDto.getId())
                    .filter(u -> soiMeme || sameEntreprise(u, requester.get()))
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Aucun utilisateur avec l'ID = " + utilisateurDto.getId() + " n'a été trouvé dans la BDD",
                            ErrorCodes.UTILISATEUR_NOT_FOUND));
            utilisateur.setNom(utilisateurDto.getNom());
            utilisateur.setPrenom(utilisateurDto.getPrenom());
            utilisateur.setEmail(email);
            utilisateur.setDateNaissance(utilisateurDto.getDateNaissance());
            utilisateur.setPhoto(utilisateurDto.getPhoto());
            utilisateur.setAdresse(AdresseDto.toEntity(utilisateurDto.getAdresse()));
            if (StringUtils.hasText(utilisateurDto.getMotDePasse())) {
                utilisateur.setMotDePasse(passwordEncoder.encode(utilisateurDto.getMotDePasse()));
            }
            return UtilisateurDto.fromEntity(utilisateurRepository.save(utilisateur));
        }

        utilisateurDto.setEmail(email);
        utilisateurDto.setMotDePasse(this.passwordEncoder.encode(utilisateurDto.getMotDePasse()));
        utilisateurDto.setActif(true);
        Utilisateur utilisateur = UtilisateurDto.toEntity(utilisateurDto);

        TypeRole role;
        if (requester.isPresent()) {
            // Création d'un membre de l'équipe : réservée à l'administrateur, dans sa propre entreprise.
            if (!hasRole(requester.get(), TypeRole.ADMIN_ENTREPRISE)) {
                throw new org.springframework.security.access.AccessDeniedException(
                        "Seul un administrateur peut créer des utilisateurs");
            }
            utilisateur.setEntreprise(requester.get().getEntreprise());
            role = requestedRole(utilisateurDto)
                    .filter(ROLES_EQUIPE::contains)
                    .orElse(TypeRole.UTILISATEUR);
        } else {
            // Inscription publique : compte client ou fournisseur rattaché à une entreprise existante,
            // jamais un rôle interne.
            role = requestedRole(utilisateurDto).filter(r -> r == TypeRole.FOURNISSEUR).orElse(TypeRole.CLIENT);
            Integer idEntreprise = utilisateurDto.getIdEntreprise() != null
                    ? utilisateurDto.getIdEntreprise()
                    : (utilisateurDto.getEntreprise() != null ? utilisateurDto.getEntreprise().getId() : null);
            List<String> partnerErrors = new ArrayList<>();
            Entreprise entreprise = idEntreprise == null ? null : entrepriseRepository.findById(idEntreprise).orElse(null);
            if (entreprise == null) {
                partnerErrors.add("veuillez choisir l'entreprise avec laquelle vous travaillez");
            }
            if (!StringUtils.hasText(utilisateurDto.getNumTel())) {
                partnerErrors.add("veuillez renseigner votre numéro de téléphone");
            }
            if (!partnerErrors.isEmpty()) {
                throw new InvalidEntityException("L'utilisateur n'est pas valide", ErrorCodes.UTILISATEUR_NOT_VALID, partnerErrors);
            }
            utilisateur.setEntreprise(entreprise);
        }

        Roles roles = new Roles();
        roles.setLibelle(role);
        roles.setRoleName(role.name());
        roles.setIdEntreprise(utilisateur.getEntreprise() != null ? utilisateur.getEntreprise().getId() : null);
        roles.setUtilisateur(utilisateur);
        utilisateur.setRoles(new ArrayList<>(List.of(roles)));

        Utilisateur saved = utilisateurRepository.save(utilisateur);
        if (role == TypeRole.CLIENT || role == TypeRole.FOURNISSEUR) {
            creerFichePartenaire(saved, role, utilisateurDto.getNumTel());
        }
        return UtilisateurDto.fromEntity(saved);
    }

    /**
     * Crée la fiche client ou fournisseur correspondant au compte, dans l'entreprise choisie,
     * pour que l'entreprise le voie dans ses listes et puisse lui associer des commandes.
     * Une fiche existante avec le même e-mail est réutilisée.
     */
    private void creerFichePartenaire(Utilisateur utilisateur, TypeRole role, String numTel) {
        if (utilisateur.getEntreprise() == null || clientRepository == null || fournisseurRepository == null) {
            return;
        }
        Integer idEntreprise = utilisateur.getEntreprise().getId();
        String telephone = numTel == null ? null : numTel.trim();
        if (role == TypeRole.CLIENT) {
            if (clientRepository.findFirstByEmailIgnoreCaseAndIdEntreprise(utilisateur.getEmail(), idEntreprise).isEmpty()) {
                Client client = new Client();
                client.setIdEntreprise(idEntreprise);
                client.setNom(utilisateur.getNom());
                client.setPrenom(utilisateur.getPrenom());
                client.setEmail(utilisateur.getEmail());
                client.setNumTel(telephone);
                client.setAdresse(utilisateur.getAdresse());
                clientRepository.save(client);
            }
        } else if (fournisseurRepository.findFirstByEmailIgnoreCaseAndIdEntreprise(utilisateur.getEmail(), idEntreprise).isEmpty()) {
            Fournisseur fournisseur = new Fournisseur();
            fournisseur.setIdEntreprise(idEntreprise);
            fournisseur.setNom(utilisateur.getNom());
            fournisseur.setPrenom(utilisateur.getPrenom());
            fournisseur.setEmail(utilisateur.getEmail());
            fournisseur.setNumTel(telephone);
            fournisseur.setAdresse(utilisateur.getAdresse());
            fournisseurRepository.save(fournisseur);
        }
    }

    @Override
    public UtilisateurDto findById(Integer id) {
        if (id == null){
            log.error("utilisateur ID is null");
            return null;
        }
        return utilisateurRepository.findById(id)
                .map(UtilisateurDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Aucun utilisateur avec l'ID = " + id + " n'a été trouvé dans la BDD",
                        ErrorCodes.UTILISATEUR_NOT_FOUND));
    }

    @Override
    public List<UtilisateurDto> findAll() {
        // Un utilisateur rattaché à une entreprise ne voit que les comptes de son entreprise.
        Integer idEntreprise = currentUser()
                .map(Utilisateur::getEntreprise)
                .map(Entreprise::getId)
                .orElse(null);
        return utilisateurRepository.findAll().stream()
                .filter(u -> idEntreprise == null
                        || (u.getEntreprise() != null && idEntreprise.equals(u.getEntreprise().getId())))
                .map(UtilisateurDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        if (id== null){
            log.error("utilisateur ID is null");
            return ;
        }
        Optional<Utilisateur> requester = currentUser();
        if (requester.isPresent() && id.equals(requester.get().getId())) {
            throw new InvalidOperationException("Vous ne pouvez pas supprimer votre propre compte",
                    ErrorCodes.UTILISATEUR_NOT_VALID);
        }
        if (requester.isPresent() && utilisateurRepository.findById(id)
                .filter(u -> sameEntreprise(u, requester.get())).isEmpty()) {
            throw new EntityNotFoundException("Aucun utilisateur avec l'ID = " + id, ErrorCodes.UTILISATEUR_NOT_FOUND);
        }
        utilisateurRepository.deleteById(id);
    }

    @Override
    public UtilisateurDto findByEmail(String email) {
        Optional<Utilisateur> requester = currentUser();
        return utilisateurRepository.findUtilisateurByEmail(normalizeEmail(email))
                // Chacun peut lire son propre profil ; l'administrateur, ceux de son entreprise.
                .filter(u -> requester.isEmpty()
                        || u.getId().equals(requester.get().getId())
                        || (hasRole(requester.get(), TypeRole.ADMIN_ENTREPRISE) && sameEntreprise(u, requester.get())))
                .map(UtilisateurDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Aucun utilisateur avec l'email = " + email + " n'a été trouvé dans la BDD",ErrorCodes.UTILISATEUR_NOT_FOUND)
                );
    }

    @Override
    @Transactional
    public UtilisateurDto changerMotDePasse(ChangerMotDePasseUtilisateurDto dto) {
        validate(dto);
        // L'utilisateur connecté ne peut changer que son propre mot de passe.
        Utilisateur utilisateur = currentUser().orElseThrow(() -> new InvalidOperationException(
                "Vous devez être connecté pour changer votre mot de passe",
                ErrorCodes.UTILISATEUR_CHANGE_PASSWORD_OBJECT_NOT_VALID));
        if (!passwordEncoder.matches(dto.getAncienMotDePasse(), utilisateur.getMotDePasse())) {
            throw new InvalidOperationException("L'ancien mot de passe est incorrect",
                    ErrorCodes.UTILISATEUR_CHANGE_PASSWORD_OBJECT_NOT_VALID);
        }
        utilisateur.setMotDePasse(this.passwordEncoder.encode(dto.getNouveauMotDePasse()));
        return UtilisateurDto.fromEntity(utilisateurRepository.save(utilisateur));
    }

    private void validate(ChangerMotDePasseUtilisateurDto dto) {
        if (dto == null){
            throw new InvalidOperationException("Aucune information n'a été fournie pour pouvoir changer le mot de passe",
                    ErrorCodes.UTILISATEUR_CHANGE_PASSWORD_OBJECT_NOT_VALID);
        }
        if (!StringUtils.hasLength(dto.getAncienMotDePasse())){
            throw new InvalidOperationException("Veuillez renseigner votre mot de passe actuel",
                    ErrorCodes.UTILISATEUR_CHANGE_PASSWORD_OBJECT_NOT_VALID);
        }
        if (!StringUtils.hasLength(dto.getNouveauMotDePasse()) || !StringUtils.hasLength(dto.getConfirmerMotDePasse())){
            throw new InvalidOperationException("Veuillez renseigner et confirmer le nouveau mot de passe",
                    ErrorCodes.UTILISATEUR_CHANGE_PASSWORD_OBJECT_NOT_VALID);
        }
        if (dto.getNouveauMotDePasse().length() < 8){
            throw new InvalidOperationException("Le nouveau mot de passe doit comporter au moins 8 caractères",
                    ErrorCodes.UTILISATEUR_CHANGE_PASSWORD_OBJECT_NOT_VALID);
        }
        if (!dto.getNouveauMotDePasse().equals(dto.getConfirmerMotDePasse())){
            throw new InvalidOperationException("Les deux nouveaux mots de passe ne correspondent pas",
                    ErrorCodes.UTILISATEUR_CHANGE_PASSWORD_OBJECT_NOT_VALID);
        }
    }

    private Optional<Utilisateur> currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken
                || !StringUtils.hasText(authentication.getName())) {
            return Optional.empty();
        }
        return utilisateurRepository.findUtilisateurByEmail(normalizeEmail(authentication.getName()));
    }

    private static Optional<TypeRole> requestedRole(UtilisateurDto dto) {
        if (dto.getRoles() == null) {
            return Optional.empty();
        }
        return dto.getRoles().stream()
                .filter(Objects::nonNull)
                .map(r -> r.getLibelle() != null ? r.getLibelle() : parseRole(r.getRoleName()))
                .filter(Objects::nonNull)
                .findFirst();
    }

    private static TypeRole parseRole(String roleName) {
        if (!StringUtils.hasText(roleName)) {
            return null;
        }
        try {
            return TypeRole.valueOf(roleName.trim().toUpperCase(Locale.ROOT).replaceFirst("^ROLE_", ""));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static boolean hasRole(Utilisateur utilisateur, TypeRole role) {
        return utilisateur.getRoles() != null && utilisateur.getRoles().stream()
                .anyMatch(r -> r != null && role == r.getLibelle());
    }

    private static boolean sameEntreprise(Utilisateur a, Utilisateur b) {
        return a.getEntreprise() != null && b.getEntreprise() != null
                && Objects.equals(a.getEntreprise().getId(), b.getEntreprise().getId());
    }

    private static String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }
}
