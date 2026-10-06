package org.gestion.gestionstock.dto;

import lombok.Builder;
import lombok.Data;
import org.gestion.gestionstock.model.Client;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@Data
public class ClientDto {
    private Integer id;

    private Integer idEntreprise;

    private String nom;

    private String prenom;

    private AdresseDto adresse;

    private String photo;

    private String numTel;

    private String email;

    private List<CommandeClientDto> commandeClients;

    public static ClientDto fromEntity(Client client){
        if (client == null){
            return  null;
        }
        return ClientDto.builder()
                .id(client.getId())
                .nom(client.getNom())
                .idEntreprise(client.getIdEntreprise())
                .prenom(client.getPrenom())
                .photo(client.getPhoto())
                .numTel(client.getNumTel())
                .email(client.getEmail())
                .adresse(AdresseDto.fromEntity(client.getAdresse()))
                .commandeClients(client.getCommandeClients().stream()
                        .map(CommandeClientDto::fromEntity).collect(Collectors.toList()))
                .build();
    }
    public static Client toEntity(ClientDto clientDto){
        if (clientDto == null){
            return null;
        }
        Client client = new Client();
        client.setNom(clientDto.getNom());
        client.setPrenom(clientDto.getPrenom());
        client.setPhoto(clientDto.getPhoto());
        client.setNumTel(clientDto.getNumTel());
        client.setEmail(clientDto.getEmail());
        client.setAdresse(AdresseDto.toEntity(clientDto.getAdresse()));
        client.setCommandeClients(client.getCommandeClients());
        return client;
    }
}
