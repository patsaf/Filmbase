# Filmbase
Backend of an application for film enthusiasts.

### Overview
The Filmbase web application was built using the tools of the Spring Framework and SQL databases. It provides a RESTful API to make the creation of a potential front-end as easy as possible - any necessary data is stored as a JSON string.
### Features
Within the app, anyone can find essential information about any film, actor or director they seek. Also, keeping a list of favourites, film wishlists and rating films, actors or directors is enabled for logged-in users. They may also update an object's information if necessary, as well as add new objects to the database. 

The objects processed inside the app were divided into 3 categories:
- models - entities stored in the database, managed by JPA repositories
- DTOs - Data Transfer Objects - objects accessed and viewed by the actual user, passed as a JSON string
- requests - made to create/update objects in the database, to be handled by front-end forms

### Technologies used
- **Java SE 8** - lambda expressions, streams, LocalDate, etc.
- **Spring Framework** - Spring MVC, Spring Boot, Spring Data, basics of Spring Security, Spring Boot Test, Spring MVC Test
- **JPA** and **Hibernate**
- **MySQL**
- **HTTP** - to meet the demands of a RESTful API
- **Lombok** - for boilerplate reduction
- **Google Gson** - for testing purposes
- **Faster XML Jackson** - to parse LocalDate objects passed within JSON strings
### Design patterns
- **Builder Pattern** - to make the creation of objects easier, cleaner and to simplify adding more details to the Filmbase objects in the future
### Software stack
- **IntelliJ IDEA 2017.2.6**
- **Gradle 4.3.1**
- **Postman**
- **MySQL Workbench 6.3**
