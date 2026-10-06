package org.gestion.gestionstock.controller;

import org.gestion.gestionstock.controller.api.ClientApi;
import org.gestion.gestionstock.dto.ClientDto;
import org.gestion.gestionstock.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
public class ClientController implements ClientApi {
    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public void delete(Integer id) {
        clientService.delete(id);
    }

    @Override
    public ClientDto save(ClientDto clientDto) {
        return clientService.save(clientDto);
    }

    @Override
    public ClientDto findById(Integer id) {
        return clientService.findById(id);
    }

    @Override
    public List<ClientDto> findAll() {
        return clientService.findAll();
    }
}
