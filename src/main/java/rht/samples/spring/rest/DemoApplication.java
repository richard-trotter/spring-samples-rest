package rht.samples.spring.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/*
 * This is the application entrypoint. See: main(). It is also the principal source of 
 * application @Bean declarations.  
 * 
 * Note that there is an audit logger that needs a log dir path specified as a Java
 * system property:
 * 
 *   -Ddemo.audit.log.home=
 * 
 * To enable application level debug logging, include this command line parameter:
 * 
 *   --logging.level.rht.samples.spring.rest=DEBUG 
 *   
 * To enable Spring Web debug logging:
 * 
 *   --logging.level.org.springframework.web=DEBUG
 *   
 * To enable Spring Security debug logging:
 * 
 *   --logging.level.org.springframework.security=DEBUG 
 *   
 * To see a Spring @Bean configuration report:
 * 
 *   --logging.level.org.springframework.boot.autoconfigure=DEBUG
 * 
 * To see the active environment, in formatted JSON, use the configured Spring Boot actuator.
 * 
 *   curl -s http://localhost:9001/demo/actuator/env | python -m json.tool
 */
@SpringBootApplication
@EnableSwagger2
public class DemoApplication {

  static final Logger logger = LoggerFactory.getLogger(DemoApplication.class);
  public static final String AUDIT_LOGGER = "demo.audit.logger";

  public static void main(String[] args) {

    logger.info("Starting spring rest sample 'DemoApplication'");

    SpringApplication.run(DemoApplication.class, args);
  }

  @Value("${demo.api.prefix}")
  String api_pfx;

  /*
   *  This bean serves as a data access object
   */
  @Bean
  public ICatalogService getLibraryCatalog() {
    return new CatalogService();
  }

  /*
   *  This bean establishes configuration for rendering a Swagger UI
   */
  @Bean
  public Docket swagger() {
    
    String pathPattern = ".*"+api_pfx+"/catalog/entry($|/.*)";
  
    logger.info("Setting Swagger UI path selection pattern = ["+pathPattern+"]");
    
    return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.basePackage("rht.samples.spring.rest"))
            .paths(PathSelectors.regex(pathPattern))
            .build();
  }

  /*
   *  Setup debug request logging
   */
  @Bean
  @ConditionalOnProperty(name="logging.level.rht.samples.spring.rest", havingValue="DEBUG")
  public CommonsRequestLoggingFilter requestLoggingFilter() {
      CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
      loggingFilter.setIncludeClientInfo(true);
      loggingFilter.setIncludeQueryString(true);
      loggingFilter.setIncludePayload(false);
      loggingFilter.setIncludeHeaders(false);
      return loggingFilter;
  }

}
