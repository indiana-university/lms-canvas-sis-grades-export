package edu.iu.uits.lms.sisgradesexport.controller;

import edu.iu.uits.lms.lti.LTIConstants;
import edu.iu.uits.lms.lti.controller.LtiAuthenticationTokenAwareController;
import edu.iu.uits.lms.lti.security.LtiAuthenticationProvider;
import edu.iu.uits.lms.sisgradesexport.config.ToolConfig;
import edu.iu.uits.lms.sisgradesexport.model.CsvResponse;
import edu.iu.uits.lms.sisgradesexport.service.SisGradesExportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/app")
@Slf4j
public class SisGradesExportController extends LtiAuthenticationTokenAwareController {

   @Autowired
   private ToolConfig toolConfig = null;

   @Autowired
   private SisGradesExportService sisGradesExportService = null;

   @RequestMapping(value = "/index/{courseId}", method = RequestMethod.GET, produces = {"text/csv"})
   @Secured(LTIConstants.INSTRUCTOR_AUTHORITY)
   public @ResponseBody CsvResponse index(@PathVariable("courseId") String courseId) {
      // make sure their context is the course that they are sending in
      getValidatedToken(courseId);

      return sisGradesExportService.exportGradesForSis(courseId);
   }
}
