# Repository Filmorate project
![GitHub Actions](https://github.com/Difirton/java-filmorate/actions/workflows/api-tests.yml/badge.svg)
![GitHub contributors](https://img.shields.io/github/contributors/Difirton/java-filmorate?color=green)
![Coveralls](https://img.shields.io/badge/coverage-81%25-green)

## Quick start
### Requirements

- Java Platform (JDK) 11
- Apache Maven 4.x


While in the directory on the command line, type:

`./mvn package`

`java -jar target/filmorate-0.0.1-SNAPSHOT.jar`

## Quick start with Docker
### Requirements

- Java Platform (JDK) 11
- Apache Maven 4.x
- Docker client


While in the directory on the command line, type:

`./mvn package`

`java -jar target/filmorate-0.0.1-SNAPSHOT.jar`

`docker build -t filmorate:0.0.1`

`docker run -d -p 8080:8080 -t filmorate:0.0.1`

## Rest service layer

#### [Link to API swagger-ui documentation and available endpoints](http://localhost:8080/swagger-ui/index.html)


This link will be available after the application starts. Below are examples of methods and endpoints available for the API

- [(GET) get list of all films](http://localhost:8080/films)
- [(POST) create new film sending json info](http://localhost:8080/films)
- [(PUT) update existing film sending json info with specified id](http://localhost:8080/films/{id})
- [(GET) get film with specified id](http://localhost:8080/films/{id}) 
- [(DELETE) remove film with specified id](http://localhost:8080/films/{id})
- [(PUT) updates an existing movie, adds 1 like to it from the user](http://localhost:8080/films/{id}/like/{userId})
- [(DELETE) removes 1 like from the user from the movie](http://localhost:8080/films/{id}/like/{userId})
- [(GET)get list popular films](http://localhost:8080/films/popular)
- [(GET) get list of all users](http://localhost:8080/users)
- [(POST) create new user sending json info](http://localhost:8080/users)
- [(PUT) update existing user sending json info with specified id](http://localhost:8080/users/{id})
- [(GET) get user with specified id](http://localhost:8080/users/{id})
- [(DELETE) remove user with specified id](http://localhost:8080/users/{id})
- [(PUT) updates an existing user, adds friend](http://localhost:8080/users/{id}/friends/{friendId})
- [(DELETE) updates an existing user, removes friend](http://localhost:8080/users/{id}/friends/{friendId})
- [(GET) get all the user's friends](http://localhost:8080/users/{id}/friends)
- [(GET) get all mutual friends of users](http://localhost:8080/users/{id}/friends/{otherId})


## Database dependency diagram

![diagram](./images/diagram.jpg)