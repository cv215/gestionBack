package org.gestion.gestionstock.services;

import org.gestion.gestionstock.dto.ClientDto;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface ClientService {
    ClientDto save(ClientDto clientDto);

    ClientDto findById(Integer id);

    List<ClientDto> findAll();

    void delete(Integer id);
}
