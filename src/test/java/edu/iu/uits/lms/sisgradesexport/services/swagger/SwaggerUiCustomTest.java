package edu.iu.uits.lms.sisgradesexport.services.swagger;

import edu.iu.uits.lms.lti.swagger.AbstractSwaggerUiCustomTest;
import edu.iu.uits.lms.sisgradesexport.WebApplication;
import edu.iu.uits.lms.sisgradesexport.config.SecurityConfig;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(classes = {WebApplication.class, SecurityConfig.class})
public class SwaggerUiCustomTest extends AbstractSwaggerUiCustomTest {

   @Override
   protected List<String> getEmbeddedSwaggerToolPaths() {
      return SwaggerTestUtil.getEmbeddedSwaggerToolPaths(super.getEmbeddedSwaggerToolPaths());
   }
}
