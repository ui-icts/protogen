# Protogen: Code Generator for Java, Spring MVC, and Hibernate
===============================================

[![Build Status](https://travis-ci.org/ui-icts/Protogen.svg?branch=master)](https://travis-ci.org/ui-icts/Protogen)

## Background: Two starting points

### Clay schema 
* Simple UML diagram
* .clay files are actually just XML
* Clay loader reads XML and creates a POJO to represent the database

### Relational database
* Read database metadata
* Look at exported keys to build relationships

## Generated Code
* Uses database object created by clay or JDBC loader
* Creates boilerplate code for CRUD operations
* For each database table, the following are generated:
  * A domain object, DAO, and JUnit test
  * A controller, a REST resource, views (with Boostrap CSS), and corresponding MVC JUnit tests

## Build and Installation

The is a maven project that requires Java 1.7 and should be built using:

    mvn clean install
    
## Execute Protogen:generate
1. Create a Maven Java Web Application project with Spring Framework 4.1.6, Tiles 3.0.5, Jetty 9.2.10, Spring Security 4.0.1, Hibernate 4.3.9.Final, and Jackson 2.5.2.
2. Open src/test/resources/Model.clay and edit tables as necessary
3. Run Protogen goals: mvn edu.uiowa.icts:protogen:2.0.5-SNAPSHOT:generate
4. Run JUnit, all tests should pass
5. export create table script from Clay as ${artifactId}.sql and run liquibase
6. start Jetty with JRE VM arguments: -Xms2000m -Xmx2000m -XX:MaxPermSize=2000m

## Logging

Update [log4j.properties](src/main/resources/log4j.properties) to change location or log levels

## License

The project is released under version 2.0 of the [Apache License](http://www.apache.org/licenses/LICENSE-2.0).
