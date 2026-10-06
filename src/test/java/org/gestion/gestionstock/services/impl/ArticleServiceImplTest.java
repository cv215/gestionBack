package org.gestion.gestionstock.services.impl;

import org.gestion.gestionstock.dto.CathegoryDto;
import org.gestion.gestionstock.exception.EntityNotFoundException;
import org.gestion.gestionstock.exception.ErrorCodes;
import org.gestion.gestionstock.exception.InvalidEntityException;
import org.gestion.gestionstock.services.CathegoryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;
@RunWith(SpringRunner.class)
@SpringBootTest
public class ArticleServiceImplTest {
    @Autowired
    private CathegoryService service;

    @Test
    public void shouldSaveCathegoryWithSuccess(){
        CathegoryDto expectedCathegory = CathegoryDto.builder()
                .code("Cat test")
                .designation("Designation test")
                .idEntreprise(1)
                .build();



        CathegoryDto savedCathegory = service.save(expectedCathegory);

        assertNotNull(savedCathegory);
        assertNotNull(savedCathegory.getId());
        assertEquals(expectedCathegory.getCode(), savedCathegory.getCode());
        assertEquals(expectedCathegory.getDesignation(), savedCathegory.getDesignation());
        assertEquals(expectedCathegory.getIdEntreprise(), savedCathegory.getIdEntreprise());
    }

    @Test
    public void shouldUpdateCathegoryWithSuccess(){
        CathegoryDto expectedCathegory = CathegoryDto.builder()
                .code("Cat test")
                .designation("Designation test")
                .idEntreprise(1)
                .build();

        CathegoryDto savedCathegory = service.save(expectedCathegory);

        CathegoryDto cathegoryToUpdate = savedCathegory;
        cathegoryToUpdate.setCode("Cat update");

        savedCathegory = service.save(cathegoryToUpdate);

        assertNotNull(cathegoryToUpdate);
        assertNotNull(cathegoryToUpdate.getId());
        assertEquals(cathegoryToUpdate.getCode(), cathegoryToUpdate.getCode());
        assertEquals(cathegoryToUpdate.getDesignation(), cathegoryToUpdate.getDesignation());
        assertEquals(cathegoryToUpdate.getIdEntreprise(), cathegoryToUpdate.getIdEntreprise());
    }

    @Test
    public void shouldThrowInvalidEntityException(){
        CathegoryDto expectedCathegory = CathegoryDto.builder().build();

       InvalidEntityException expectedException = assertThrows(InvalidEntityException.class, () -> service.save(expectedCathegory));

       assertEquals(ErrorCodes.CATHEGORY_NOT_VALID, expectedException.getErrorCodes());
        assertEquals(1 , expectedException.getErrors().size());
        assertEquals("Veuillez renseigner le code de la cathegory" , expectedException.getErrors().get(0));
    }

    @Test
    public void shouldThrowEntityNotFoundException(){

        EntityNotFoundException expectedException = assertThrows(EntityNotFoundException.class, () -> service.findById(0));

        assertEquals(ErrorCodes.CATHEGORY_NOT_FOUND, expectedException.getErrorCodes());
        assertEquals("Aucune cathegory avec l'ID = 0 n'a ete trouve dans la BDD" , expectedException.getMessage());
    }
}