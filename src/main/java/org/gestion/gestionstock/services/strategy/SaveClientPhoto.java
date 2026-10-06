package org.gestion.gestionstock.services.strategy;

import com.flickr4java.flickr.FlickrException;
import lombok.extern.slf4j.Slf4j;
import org.gestion.gestionstock.dto.ClientDto;
import org.gestion.gestionstock.exception.ErrorCodes;
import org.gestion.gestionstock.exception.InvalidOperationException;
import org.gestion.gestionstock.services.ClientService;
import org.gestion.gestionstock.services.FlickrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.InputStream;
@Service("clientStrategy")
@Slf4j
public class SaveClientPhoto implements Strategy<ClientDto>{

    private FlickrService flickrService;
    private ClientService clientService;

    @Autowired
    public SaveClientPhoto(ClientService clientService, FlickrService flickrService) {
        this.clientService = clientService;
        this.flickrService = flickrService;
    }

    @Override
    public ClientDto savePhoto(Integer id,InputStream photo, String titre) throws FlickrException {
        ClientDto clientDto = clientService.findById(id);
        String urlPhoto = flickrService.savePhoto(photo,titre);
        if (!StringUtils.hasLength(urlPhoto)){
            throw new InvalidOperationException("Erreur lors de l'enregistrement de la photo du client", ErrorCodes.UPDATE_PHOTO_EXCEPTION);
        }
        clientDto.setPhoto(urlPhoto);
        return clientService.save(clientDto);
    }
}
