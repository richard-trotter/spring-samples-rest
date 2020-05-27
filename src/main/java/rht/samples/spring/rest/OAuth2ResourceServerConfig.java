package rht.samples.spring.rest;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;


@Configuration
@EnableWebSecurity
public class OAuth2ResourceServerConfig extends WebSecurityConfigurerAdapter {

  @Value("${spring.security.oauth2.resourceserver.jwt.public-key-location}") 
  String publicKeyLocation;

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    // Allow GET requests for any authenticated user.
    http.authorizeRequests(authorizeRequests -> authorizeRequests
                                                  .antMatchers(HttpMethod.GET, "/**")
                                                  .authenticated());
    
    /*
     *  Add Jwt-encoded bearer token support
     *  
     *  This will result in the authentication token for an authenticated principal for the 
     *  active RestController security context to be a JwtAuthenticationToken. 
     */
    //  
    http
      .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
    
  }
  
  
  @Bean 
  JwtDecoder jwtDecoder() {
    
    String path = publicKeyLocation.split(":")[1];
    
    try( InputStream is = this.getClass().getResourceAsStream(path) ) {
            
      int len = is.available();
      byte[] keyBytes = new byte[len];
      is.read(keyBytes,0,len);
  
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
      RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
      
      return NimbusJwtDecoder.withPublicKey(publicKey).build();
      
    } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      return null;
    }
  }
}