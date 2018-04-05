# NRS Configuration Management Database

## Quickstart

1. Download Neo4j CE from [https://neo4j.com/download/other-releases/], version 3.3.0 or higher
1. Launch Neo4j: from the root Neo4j directory run the command `neo4j console`
1. In your browser, visit the address that it provides, and log in using the username and password "*neo4j*"; you will be
   prompted to change this password
1. Modify `src/main/resources/application.properties` to include the newly created password
1. Download Keycloak from [https://www.keycloak.org/downloads.html], version 4.0.0.Beta1 or higher

1. From the project root directory `mvn spring-boot:run`

## Testing
Testing expects a file at `test/resources/application-test.properties` to exist with the following contents:
* ssh.username: the username that should be used to connect to the test server
* ssh.password: the password for the provided username


------------------------

## Technology
### Neo4j Community Edition
A graph database

[project website](https://neo4j.com)

### Neo4j OGM
An object graph mapping library for Neo4j

[project website](https://neo4j.com/docs/ogm-manual/current/)

### Keycloak
Using [this guide](http://www.baeldung.com/spring-boot-keycloak) to install 
http://127.0.0.1:8180/auth/

* [Spring Boot Adapter](http://www.keycloak.org/docs/3.3/securing_apps/topics/oidc/java/spring-boot-adapter.html)
* [Spring Security Adapter](http://www.keycloak.org/docs/3.3/securing_apps/topics/oidc/java/spring-security-adapter.html)
* [Keycloak Spring Security Examples](https://github.com/foo4u/keycloak-spring-demo)

### Project Lombok
Removes the requirement to write getters and setters, early access to future Java features and more.

[project website](https://projectlombok.org)

### JSch
SSH in Java

[project website](http://www.jcraft.com/jsch/)

includes [JSch Extension](https://github.com/lucastheisen/jsch-extension) and [JSch NIO](https://github.com/lucastheisen/jsch-nio)