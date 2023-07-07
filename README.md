# InstaZoo

Training project inspired by InstaZoo examples;

Backend Service for Insta Zoo Application; 
We can start it using docker compose.

<b> "docker-compose up" </b> will build and start Spring backend service on port 6060,
allowing connections from frontend "http/localhost:4200",
together with PostgreSQL database;

Used:

- Java 17;
- Spring Boot 3.0.6;
- Spring Data JPA;
- Spring Security with custom Jwt;
- Validation API;
- Flyway for DB migration
- Lombok to reduce boilerplate code;
- Mapstruct;
- WebTestClient for testing;
- TestContainers

Tested:
![image](https://github.com/Veselovnd88/InstaZoo/assets/63257041/86d27cb9-4358-46c2-946d-63e4bf7ef1ef)
![image](https://github.com/Veselovnd88/InstaZoo/assets/63257041/02e85732-f257-4dc0-ac32-6f6251a12a5d)


Angular frontend here:
https://github.com/Veselovnd88/instazooFrontApp
