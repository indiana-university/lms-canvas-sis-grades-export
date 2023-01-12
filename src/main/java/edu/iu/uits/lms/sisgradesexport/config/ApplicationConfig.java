package edu.iu.uits.lms.sisgradesexport.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableWebMvc
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@Slf4j
public class ApplicationConfig implements WebMvcConfigurer {

   public ApplicationConfig() {
      log.debug("ApplicationConfig()");
   }

   @Override
   public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
      converters.add(csvMessageConverter());
   }

   @Bean
   public CsvMessageConverter csvMessageConverter() {
      CsvMessageConverter csvMessageConverter = new CsvMessageConverter();
      return csvMessageConverter;
   }
}
