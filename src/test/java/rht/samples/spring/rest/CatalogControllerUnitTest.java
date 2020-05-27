package rht.samples.spring.rest;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/*
 * This is a unit test suite for CatalogController.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes=DemoApplication.class)
@AutoConfigureMockMvc
public class CatalogControllerUnitTest {
  
  static final Logger logger = LoggerFactory.getLogger(CatalogControllerUnitTest.class);

  /*
  {
    "jti": "395ea007-237c-4209-8cae-9f78a3c4e57f",
    "iss": "http://example.com/authorization",
    "iat": 1568834794,
    "exp": 1726514794,
    "nbf": 1568834784,
    "sub": "test client",
    "aud": "oauth2-resource"
  }
  */
  static final String SampleJwtBearerToken = 
          "eyJhbGciOiJSUzI1NiJ9.eyJqdGkiOiIzOTVlYTAwNy0yMzdjLTQyMDktOGNhZS05Zjc4YTNjNGU1N2YiL"
          +"CJpc3MiOiJodHRwOi8vZXhhbXBsZS5jb20vYXV0aG9yaXphdGlvbiIsImlhdCI6MTU2ODgzNDc5NCwiZXh"
          +"wIjoxNzI2NTE0Nzk0LCJuYmYiOjE1Njg4MzQ3ODQsInN1YiI6InRlc3QgY2xpZW50IiwiYXVkIjoib2F1d"
          +"GgyLXJlc291cmNlIn0.fyWkI0Vs7zJElyP-MNmOy0LOlwJQL_K9NgB2WtN7Xwtv2YC_F9WKXWNJUlpSt7n"
          +"B-hhgx5sc_6YoeL8JmOHRxWmlZD4XBAUNTEGaoRmdrASvH3-EgVGDrJDPT30FLUrha-bEXNbX2PZHKCUIk"
          +"et5WZIZ_53_a7-PtwZm60lUQD-oEcdpKfoAsHWegSJ6x4MH8kf0Fzn9LXhbeq0Gd2AtaEUhD0tt-SnuMOM"
          +"06VUcFlVfJNCpknLmtEVXzo22PBKWlirH193XLf7Z6GRECWMAi-H8Q-JpsPa53cBlv20Y7yiT5AZ35VYtu"
          +"khGqmJiEj1fK1GmTKyXt1KKzKNh4DxQmA";
  
  
  @Autowired
  private ICatalogService catalog;

  @Value("${demo.api.prefix}")
  String api_pfx;
  
  @Autowired
  private WebApplicationContext context;
  
  
  private MockMvc mvc;

  @Before
  public void setup() {
      mvc = MockMvcBuilders
        .webAppContextSetup(context)
        .apply(springSecurity())
        .build();
  }
  
  
  /*
   * Verify result for "all entries"
   */
  @Test
  public void testAllCatalogEntries() throws Exception {

    List<CatalogEntry> allEntries = catalog.getAllEntries();

    CatalogEntry expectedEntry = allEntries.get(0);

    mvc.perform(get(api_pfx+"/catalog/entry")
            .contentType(MediaType.APPLICATION_JSON)
            .with(authentication(new BearerTokenAuthenticationToken(SampleJwtBearerToken))))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$", hasSize(1)))
              .andExpect(jsonPath("$[0].releaseDate", equalTo(expectedEntry.getReleaseDate())));
  }

  /*
   * Verify result for "entry by ISBN"
   */
  @Test
  public void testCatalogEntryByID() throws Exception {

    List<CatalogEntry> allEntries = catalog.getAllEntries();

    CatalogEntry expectedEntry = allEntries.get(0);

    mvc.perform(get(api_pfx+"/catalog/entry/9781449311605")
            .contentType(MediaType.APPLICATION_JSON)
            .with(authentication(new BearerTokenAuthenticationToken(SampleJwtBearerToken))))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.releaseDate", equalTo(expectedEntry.getReleaseDate())));
  }

  /*
   * Verify result for "entry not found for ISBN"
   */
  @Test
  public void tesISBNNotFound() throws Exception {

    mvc.perform(get(api_pfx+"/catalog/entry/1234")
            .contentType(MediaType.APPLICATION_JSON)
            .with(authentication(new BearerTokenAuthenticationToken(SampleJwtBearerToken))))
              .andExpect(status().isNotFound())
              .andExpect(jsonPath("$.errorMessage", containsString("1234")));
  }

  /*
   * Verify result for "unexpected exception"
   */
  @Test
  public void testThrownNPE() throws Exception {

    mvc.perform(get(api_pfx+"/catalog/npe")
            .contentType(MediaType.APPLICATION_JSON)
            .with(authentication(new BearerTokenAuthenticationToken(SampleJwtBearerToken))))
              .andExpect(status().isInternalServerError())
              .andExpect(jsonPath("$.errorMessage", containsString("NullPointer")));
  }

}
