package rht.samples.spring.rest;

import static rht.samples.spring.rest.DemoApplication.AUDIT_LOGGER;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
/*
 * This is a servlet filter that performs audit logging of each request.
 * See also "demo.audit.logger" configuration in logback.xml
 */
public class AuditLoggingFilter implements Filter {

  static final Logger auditLogger = LoggerFactory.getLogger(AUDIT_LOGGER);
  static final Logger logger = LoggerFactory.getLogger(AuditLoggingFilter.class);

  public static final String USER_KEY = "principal";

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
          throws IOException, ServletException {

    if( logger.isDebugEnabled() )
      log_credentials();
    
    HttpServletRequest httpRequest = (HttpServletRequest) request;

    String sub = "anonymous";
    Principal principal = httpRequest.getUserPrincipal();
    if (principal != null)
      sub = principal.getName();

    MDC.put(USER_KEY, sub);

    chain.doFilter(request, response);

    HttpServletResponse httpResponse = (HttpServletResponse) response;    
    String auditMessage = String.format("%d %s %s", 
              httpResponse.getStatus(),
              httpRequest.getMethod(),
              httpRequest.getRequestURI());
    
    auditLogger.info(auditMessage);

  }

  /*
   *  Log confirmation of JWT identity assertion
   */
  private void log_credentials() {

    JwtAuthenticationToken auth = (JwtAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || auth.getName() == null)
      return;

    Jwt jwt = auth.getToken();
    Map <String,Object>claims = jwt.getClaims();
    
    StringBuffer buf = new StringBuffer();
    buf.append("JWT Authentication claims{");
    claims.forEach(
            (pkey, pval) -> buf.append("\n\t").append(pkey).append(" = ").append(pval));
    
    logger.info(buf+"\n}");    
  }

}
