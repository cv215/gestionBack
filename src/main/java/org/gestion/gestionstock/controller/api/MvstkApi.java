package org.gestion.gestionstock.controller.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.gestion.gestionstock.dto.MvstkDto;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

import static org.gestion.gestionstock.utils.Constants.APP_ROOT;

@Tag(name = APP_ROOT + "/mvstk")
public interface MvstkApi {

    @GetMapping(APP_ROOT + "/mvstk/stockreel/{idArticle}")
    BigDecimal stockReelArticle(@PathVariable("idArticle") Integer idArticle);

    @GetMapping(APP_ROOT + "/mvstk/filter/article/{idArticle}")
    List<MvstkDto> mvstkArticle(@PathVariable("idArticle") Integer idArticle);

    @PostMapping(APP_ROOT + "/mvstk/entree")
    MvstkDto entreeStock(@RequestBody MvstkDto dto);

    @PostMapping(APP_ROOT + "/mvstk/sortie")
    MvstkDto sortieStock(@RequestBody MvstkDto dto);

    @PostMapping(APP_ROOT + "/mvstk/correctionpos")
    MvstkDto correctionStockPos(@RequestBody MvstkDto dto);

    @PostMapping(APP_ROOT + "/mvstk/correctionneg")
    MvstkDto correctionStockNeg(@RequestBody MvstkDto dto);
}
