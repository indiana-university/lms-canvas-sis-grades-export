package edu.iu.uits.lms.sisgradesexport.services.swagger;

import edu.iu.uits.lms.iuonly.config.IuCustomRestConfiguration;
import edu.iu.uits.lms.lti.config.LtiClientTestConfig;
import edu.iu.uits.lms.lti.config.LtiRestConfiguration;
import edu.iu.uits.lms.lti.service.LmsDefaultGrantedAuthoritiesMapper;
import edu.iu.uits.lms.lti.swagger.SwaggerTestingBean;
import edu.iu.uits.lms.sisgradesexport.config.SecurityConfig;
import edu.iu.uits.lms.sisgradesexport.config.SwaggerConfig;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import java.util.ArrayList;
import java.util.List;

import static edu.iu.uits.lms.iuonly.IuCustomConstants.IUCUSTOM_GROUP_CODE_PATH;

@Import({
        SecurityConfig.class,
        SwaggerConfig.class,
        edu.iu.uits.lms.lti.config.SwaggerConfig.class,
        LtiRestConfiguration.class,
        edu.iu.uits.lms.iuonly.config.SwaggerConfig.class,
        IuCustomRestConfiguration.class,
        LtiClientTestConfig.class
})
public class SwaggerTestConfig {

   @MockBean
   private BufferingApplicationStartup bufferingApplicationStartup;

   @MockBean
   private LmsDefaultGrantedAuthoritiesMapper defaultGrantedAuthoritiesMapper;

   @MockBean
   private ClientRegistrationRepository clientRegistrationRepository;

   @MockBean
   private OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

   @Bean
   public SwaggerTestingBean swaggerTestingBean() {
      SwaggerTestingBean stb = new SwaggerTestingBean();

      List<String> expandedList = new ArrayList<>();
      expandedList.add(IUCUSTOM_GROUP_CODE_PATH);

      stb.setEmbeddedSwaggerToolPaths(expandedList);
      return stb;
   }

}
