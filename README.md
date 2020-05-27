# Spring Web: sample REST app

This is a pretty basic REST app. The principle goal is to demo key features of `spring-web` that support REST apps generally. 

The app provides read access to a trivial library catalog, consisting of one catalog entry. 

The app source is managed as an Eclipse project, and the app can be run from within Eclipse as a Spring Boot app.  The maven build is configured for the `package` goal to produce a Spring Boot jar. 

There is a request audit logger in place, configured via the file `logback.xml`, and the audit log location must be specified as the value of the Java system property `demo.audit.log.home`. A configuration for the `spring-boot-maven-plugin` specifies a value for this property as `target`.   
 
So, the app can also be run from command line as shown below. 

    $ mvn spring-boot:run

And access log records can be accessed as:

    % cat target/demo-audit.log
    2020-05-27 08:59:21,776 AUDIT - test client - 200 GET /demo/api/v1/catalog/entry

The HTTP access log for the embedded Tomcat server is enabled, and the location of this log file is:

    ${user.dir}/tomcat-runtime/logs/

Server properties are set in the `application.yml` configuration for application access using the URL below.

    http://localhost:8081/demo

A Swagger UI, identifying public endpoints, is available here:

    http://localhost:8081/demo/swagger-ui.html

The list of entries in the catalog is retrieved with:

    http://localhost:8081/demo/catalog/entry 

and the response is provided as JSON text.

    [{"title":"Getting Started with OAuth 2.0",...,"isbn":"9781449311605"}]

Catalog entries are keyed by ISBN, and the specific entry can be retrieved by providing its ISBN. 

    http://localhost:8081/demo/catalog/entry/9781449311605

If a request is made with an ISBN that does not match, then a JSON error message is returned and the response status is set to 404.

    {"errorMessage":"Catalog Entry for isbn='978144931160' Not Found"}

The app behaves as an OAuth2 Resource Server. As such it expects an Authorization header and JWT bearer token with each HTTP request. A suitable header can be generated using the utility class JWTBuilder. Example output:

    Authorization: Bearer eyJhbGciOiJSUzI1N...

It's useful to add that text to a file. Example: `src/test/resources/api_headers.txt`. Invocation then is as follows. 

    % curl -w "\nSTATUS: %{http_code}\n" -H @src/test/resources/api_headers.txt http://localhost:8081/demo/api/v1/catalog/entry 
    [{"title":"Getting Started with OAuth 2.0","author":"Ryan Boyd","publisher":"O'Reilly Media, Inc.","releaseDate":"February 2012","isbn":"9781449311605"}]
    STATUS: 200

To see the active environment, use the configured Spring Boot actuator 'env' endpoint. Example:

    % curl -s -H @src/test/resources/api_headers.txt http://localhost:9001/demo/actuator/env| python -m json.tool | grep -A1 -B1 application.yml
    
Additional debug logging tips can be seen in the class comment for DemoApplication. 
