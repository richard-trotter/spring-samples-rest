package rht.samples.spring.rest.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.Properties;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

/*
 * Build a JWT to be used for test. 
 * 
 * Command line arguments for and Eclipse launch config:
 * 
 *   ${project_loc}/src/test/resources/SampleJwtConfig.properties ${project_loc}/src/test/resources/private_key.der
 *   
 * The output can be copied into a curl command Authorizaton header. Example:
 * 
 *   curl -H 'Authorization: Bearer eyJhbGciOiJSUz...' http://localhost:8081/demo/api/v1/catalog/entry
 *   
 * Note that the Nimbus library used here is pulled into the app runtime as a spring-security dependency.
 *   org.springframework.security:spring-security-oauth2-jose
 *     com.nimbusds:nimbus-jose-jwt
 */
public class JWTBuilder {

  public static void main(String[] args) {

    String propertiesPath = args[0];
    String privateKeyPath = args[1];

    Properties config;
    RSAPrivateKey privateKey;

    try (FileInputStream fis = new FileInputStream(propertiesPath)) {

      // load jwt properties config
      (config = new Properties()).load(fis);

      // read private key DER file
      byte[] keyBytes = Files.readAllBytes(Paths.get(privateKeyPath));

      // Instantiate and record the RSAPrivateKey
      PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(keyBytes);
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      privateKey = (RSAPrivateKey) keyFactory.generatePrivate(privSpec);

      // Generate and print the JWT, in the form of an HTTP Authorization header
      String token = createJwt(config, privateKey);
      String header = "Authorization: Bearer " + token;
      System.out.println("\n" + header + "\n");

    } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException | JOSEException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

  public static String createJwt(Properties config, RSAPrivateKey privateKey) throws JOSEException {

    Date now = new Date();

    long jwtTTL = Long.parseLong(config.getProperty("jwt.ttl"));

    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
            .audience(config.getProperty("jwt.audience"))
            .subject(config.getProperty("jwt.subject"))
            .issuer(config.getProperty("jwt.issuer"))
            .issueTime(now)
            .notBeforeTime(new Date(now.getTime() - 10000))
            .expirationTime(new Date(now.getTime() + jwtTTL))
            .jwtID("395ea007-237c-4209-8cae-9f78a3c4e57f")
            .build();

    SignedJWT signedJwt = new SignedJWT(
            new JWSHeader(JWSAlgorithm.RS256), 
            claimsSet);

    signedJwt.sign(new RSASSASigner(privateKey));

    return signedJwt.serialize();
  }
}
