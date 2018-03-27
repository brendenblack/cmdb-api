# NRS Configuration Management Database

## Quickstart

1. Download Neo4j CE from [https://neo4j.com/download/other-releases/], version 3.3.0 or greater.
1. Launch Neo4j: from the root Neo4j directory run the command `neo4j console`
1. In your browser, visit the address that it provides, and log in using the username and password "*neo4j*"; you will be
   prompted to change this password
1. Modify `src/main/resources/application.properties` to include the newly created password
1. From the project root directory `mvn spring-boot:run`


------------------------

## Technology
### Neo4j Community Edition
A graph database

[project website](https://neo4j.com)

### Neo4j OGM
An object graph mapping library for Neo4j

[project website](https://neo4j.com/docs/ogm-manual/current/)

### Project Lombok
Removes the requirement to write getters and setters, early access to future Java features and more.

[project website](https://projectlombok.org)

### JSch
SSH in Java

[project website](http://www.jcraft.com/jsch/)

includes [JSch Extension](https://github.com/lucastheisen/jsch-extension) and [JSch NIO](https://github.com/lucastheisen/jsch-nio)