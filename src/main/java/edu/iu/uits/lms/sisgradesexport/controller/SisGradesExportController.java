package edu.iu.uits.lms.sisgradesexport.controller;

import edu.iu.uits.lms.lti.LTIConstants;
import edu.iu.uits.lms.lti.controller.OidcTokenAwareController;
import edu.iu.uits.lms.lti.service.OidcTokenUtils;
import edu.iu.uits.lms.sisgradesexport.config.ToolConfig;
import edu.iu.uits.lms.sisgradesexport.model.CsvResponse;
import edu.iu.uits.lms.sisgradesexport.service.SisGradesExportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import uk.ac.ox.ctl.lti13.security.oauth2.client.lti.authentication.OidcAuthenticationToken;

@Controller
@RequestMapping("/app")
@Slf4j
public class SisGradesExportController extends OidcTokenAwareController {

   @Autowired
   private ToolConfig toolConfig = null;

   @Autowired
   private SisGradesExportService sisGradesExportService = null;

   @GetMapping(value = "/launch", produces = {"text/csv"})
   @Secured(LTIConstants.INSTRUCTOR_AUTHORITY)
   public @ResponseBody CsvResponse launch(Model model, SecurityContextHolderAwareRequestWrapper request) {
      OidcAuthenticationToken token = getTokenWithoutContext();
      OidcTokenUtils oidcTokenUtils = new OidcTokenUtils(token);
      String courseId = oidcTokenUtils.getCourseId();

      return index(courseId);
   }

   @GetMapping(value = "/index/{courseId}", produces = {"text/csv"})
   @Secured(LTIConstants.INSTRUCTOR_AUTHORITY)
   public @ResponseBody CsvResponse index(@PathVariable("courseId") String courseId) {
      // make sure their context is the course that they are sending in
      getValidatedToken(courseId);

      return sisGradesExportService.exportGradesForSis(courseId);
   }
}
