package edu.iu.uits.lms.sisgradesexport.controller.rest;

import edu.iu.uits.lms.sisgradesexport.service.SisGradesExportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rest/sis")
@Slf4j
public class SisGradesRestController {

   @Autowired
   private SisGradesExportService sisGradesExportService = null;

   @GetMapping(value = "/grades/{sisSectionId}",
         produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_XML_VALUE, MediaType.TEXT_PLAIN_VALUE, MediaType.TEXT_HTML_VALUE })
   public ResponseEntity getInfoForSisFromSisSectionId(@PathVariable String sisSectionId) {
      return sisGradesExportService.getInfoForSisFromSisSectionId(sisSectionId);
   }

}
