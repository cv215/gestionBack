package org.gestion.gestionstock.controller.api;

import com.flickr4java.flickr.FlickrException;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static org.gestion.gestionstock.utils.Constants.APP_ROOT;

@Tag(name = APP_ROOT + "/photo")
public interface PhotoApi {
    @PostMapping( APP_ROOT + "/photo/{id}/{title}/{context}")
   Object savePhoto(@PathVariable("context") String context,
                    @PathVariable("id") Integer id,
                    @RequestPart("file") MultipartFile photo,
                    @PathVariable("title") String title) throws IOException, FlickrException;
}
