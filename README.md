# NRS Configuration Management Database

## Quickstart

A Vagrantfile has been included to assist in development efforts. This file will create a CentOS box and install &
configure Keycloak 4.0.0.Beta2 ~~and Neo4j Community 3.3.4~~ [not yet implemented].

**Vagrant & VirtualBox are notoriously finicky.** 
- This is tested using Vagrant 2.1.1 , Vagrant VBGuest 0.15.1 and VirtualBox 5.2.12 r122591.
- Went with [vagrant-sshfs](https://github.com/dustymabe/vagrant-sshfs) for synced folders as recommended in 
 [this blog post](https://blog.centos.org/2018/04/updated-centos-vagrant-images-available-v1803-01/)

1. Install Vagrant ([download page](https://www.vagrantup.com/downloads.html))
1. Install VirtualBox and Extension Pack ([download page](https://www.virtualbox.org/wiki/Downloads))
1. Open a command prompt at cmdb-api/vagrant
1. Install Vagrant VGGuest plugin `vagrant plugin install vagrant-vbguest`
1. Install Vagrant SSHFS plugin `vagrant plugin install vagrant-sshfs`
1. `vagrant up`
1. Fingers crossed

This will make Keycloak available at [http://localhost:8180/auth](http://localhost:8180/auth)
## Fetching a token

```
curl -X POST \
  http://localhost:8280/auth/realms/CmdbRealm/protocol/openid-connect/token \
  -H 'Cache-Control: no-cache' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'username=nrsci&password=password&grant_type=password&client_id=login-app'
```

If you need to log in to the box, SSH to localhost:2222 and use *vagrant*/*vagrant* to log in.

### Keycloak setup
While the Vagrantfile should take care of configuring Keycloak, the specific details are provided here in case you do
not want or are unable to use it.

* create a realm called CmdbRealm
* 


### Neo4j setup

Neo4j is not currently supported in the automated Vagrant setup due to the requirement to accept their license before 
downloading the installation package.

1. Download Neo4j CE from [https://neo4j.com/download/other-releases/], version 3.3.0 or higher
1. Launch Neo4j: from the root Neo4j directory run the command `neo4j console`
1. In your browser, visit the address that it provides, and log in using the username and password "*neo4j*"; you will be
   prompted to change this password
1. Modify `src/main/resources/application.properties` to include the newly created password
1. Download Keycloak from [https://www.keycloak.org/downloads.html], version 4.0.0.Beta2 or higher

1. From the project root directory `mvn spring-boot:run`

## Testing
Testing expects a file at `test/resources/application-test.properties` to exist with the following contents:
* ssh.username: the username that should be used to connect to the test server
* ssh.password: the password for the provided username

The `ut.ca.bc.gov.nrs.cmdb.*` package is meant for fast, in-memory tests, while the `it.ca.bc.gov.nrs.cmdb.*` package is 
the place for tests that rely on out-of-process services.
------------------------

## Technology
### Neo4j Community Edition
The most popular graph database

[project website](https://neo4j.com)

### Neo4j OGM
An object graph mapping library for Neo4j

[project website](https://neo4j.com/docs/ogm-manual/current/)

### Keycloak

Keycloak is an open source Identity and Access Management solution aimed at modern applications and services. 

[project website](https://www.keycloak.org/index.html)

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

