package org.gestion.gestionstock.controller;

import com.flickr4java.flickr.FlickrException;
import org.gestion.gestionstock.controller.api.PhotoApi;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.gestion.gestionstock.services.strategy.StrategyPhotoContext;
import java.io.IOException;

@RestController
public class PhotoController implements PhotoApi {

    private StrategyPhotoContext strategyPhotoContext;

    public PhotoController(StrategyPhotoContext strategyPhotoContext) {
        this.strategyPhotoContext = strategyPhotoContext;
    }

    @Override
    public Object savePhoto(String context, Integer id, MultipartFile photo, String title) throws IOException, FlickrException {
        return strategyPhotoContext.savePhoto(context,id,photo.getInputStream(),title);
    }
}
