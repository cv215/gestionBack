package org.gestion.gestionstock.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.gestion.gestionstock.dto.ClientDto;
import org.gestion.gestionstock.exception.EntityNotFoundException;
import org.gestion.gestionstock.exception.ErrorCodes;
import org.gestion.gestionstock.exception.InvalidEntityException;
import org.gestion.gestionstock.exception.InvalidOperationException;
import org.gestion.gestionstock.model.Client;
import org.gestion.gestionstock.model.CommandeClient;
import org.gestion.gestionstock.repository.ClientRepository;
import org.gestion.gestionstock.repository.CommandeClientRepository;
import org.gestion.gestionstock.services.ClientService;
import org.gestion.gestionstock.validator.ClientValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private CommandeClientRepository commandeClientRepository;
    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository, CommandeClientRepository commandeClientRepository) {

        this.clientRepository = clientRepository;
        this.commandeClientRepository = commandeClientRepository;
    }

    @Override
    public ClientDto save(ClientDto clientDto) {
        List<String> errors = ClientValidator.validate(clientDto);
        if (!errors.isEmpty()){
            log.error("client is not valid {}", clientDto);
            throw new InvalidEntityException("l'article n'est pas valide", ErrorCodes.CLIENT_NOT_FOUND, errors);
        }

        return ClientDto.fromEntity(
                clientRepository.save(
                        ClientDto.toEntity(clientDto)
                )
        );
    }

    @Override
    public ClientDto findById(Integer id) {
        if (id == null){
            log.error("client ID is null");
            return null;
        }
        Optional<Client> client = clientRepository.findById(id);
        return Optional.of(ClientDto.fromEntity(client.get())).orElseThrow(
                () -> new EntityNotFoundException(
                        "aucun client avec ID = "+ id + "n'a été rouvé dans la BDD", ErrorCodes.CLIENT_NOT_FOUND
                )
        );
    }


    @Override
    public List<ClientDto> findAll() {

        return clientRepository.findAll().stream()
                .map(ClientDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        if (id == null){
            log.error("client ID is null");
            return;
        }
        List<CommandeClient> commandeClients = commandeClientRepository.findAllByClientId(id);
        if (!commandeClients.isEmpty()){
            throw new InvalidOperationException("Impossible de supprimer ce client qui a déjà les commandes ",ErrorCodes.CLIENT_ALREADY_IN_USE);
        }
        clientRepository.deleteById( id);
    }
}
