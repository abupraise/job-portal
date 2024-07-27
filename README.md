# Job Application Submission Portal

This is a CRUD application built with Spring Boot that allows job seekers to submit their applications, including uploading photos and CVs. The application uses PostgreSQL for data persistence and allows for easy management of applicants.

## Features

- Create a new applicant
- Retrieve all applicants with pagination
- Retrieve a specific applicant by ID
- Upload a photo for an applicant
- Upload a CV for an applicant
- Retrieve a photo by filename
- Retrieve a CV by filename
- Delete an applicant by ID

## Requirements

- Java 21
- PostgreSQL
- Maven

## Configuration

The application requires a PostgreSQL database. Update the `application.properties` file with your database credentials.

### `application.properties`

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/CrudDb
spring.datasource.username=${USERNAME}
spring.datasource.password=${PASSWORD}

spring.jpa.database-platform=org.hibernate.dialect.PosgreSQLInnoDBDialect
spring.jpa.generate-ddl=true
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.globally_quoted_identifiers=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true

spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=1000MB
spring.servlet.multipart.max-request-size=1000MB

spring.mvc.throw-exception-if-no-handler-found=true
spring.mvc.async.request-timeout=3600000

server.port=8080
server.error.path=/user/error
server.error.whitelabel.enabled=false
