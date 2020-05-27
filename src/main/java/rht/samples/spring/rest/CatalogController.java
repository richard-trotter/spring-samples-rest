package rht.samples.spring.rest;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "${demo.api.prefix}/catalog", produces = { MediaType.APPLICATION_JSON_VALUE })
public class CatalogController {

  static final Logger logger = LoggerFactory.getLogger(CatalogController.class);
  
  @Autowired
  private Environment env;

  @Autowired
  private ICatalogService catalogService;

  @GetMapping("/npe")
  public List<CatalogEntry> produceRuntimeException() {

    throw new NullPointerException("simulated");
  }

  @GetMapping("/entry")
  public List<CatalogEntry> getAllEntries() {

    return catalogService.getAllEntries();
  }

  @GetMapping("/entry/{isbn}")
  public CatalogEntry getEntry(@PathVariable(value = "isbn") String isbn) throws EntryNotFoundException {

    logger.info("Entering getEntry()");

    return catalogService.getEntry(isbn);
  }

  @PostConstruct
  public void afterInit() {
    // tomcat access log, if enabled, will be under here
    logger.info("server.tomcat.basedir=" + env.getProperty("server.tomcat.basedir"));
  }
}
