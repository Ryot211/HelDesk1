# HelpDesk Backend

Backend para un sistema HelpDesk interno orientado a la gestión de tickets de soporte técnico dentro de una organización.

El proyecto está desarrollado con Spring Boot y PostgreSQL, siguiendo una arquitectura por capas para separar entidades, DTOs, repositorios, servicios, mappers y controladores REST.

---

## Objetivo del proyecto

El objetivo principal es construir una API backend que permita gestionar solicitudes de soporte técnico mediante tickets, usuarios, roles, departamentos y categorías.

Este proyecto forma parte de una práctica de desarrollo backend para reforzar conceptos como:

- Spring Boot
- Spring Data JPA
- PostgreSQL
- Relaciones entre entidades
- DTOs
- MapStruct
- Servicios de negocio
- API REST
- Manejo de contraseñas con hash
- Buenas prácticas de organización por capas

---

## Tecnologías utilizadas

- Java 17
- Spring Boot 4
- Spring Web MVC
- Spring Data JPA
- PostgreSQL
- Maven
- Lombok
- MapStruct
- Spring Security
- Spring Security Crypto
- JWT (io.jsonwebtoken / jjwt)
- Flyway
- springdoc-openapi (Swagger UI)
- Docker / Docker Compose
- IntelliJ IDEA
- Postman

---

## Cómo levantar con Docker

Requisito previo: tener Docker y Docker Compose instalados.

1. Copiar el archivo `.env.example` a `.env` y completar `POSTGRES_PASSWORD` y `APP_JWT_SECRET` con valores propios.
2. Ejecutar:

```bash
docker compose up --build
```

> **Nota:** en este punto el contenedor de Postgres arranca vacío (solo con el schema `helpdesk` creado, sin tablas), por lo que el backend va a fallar la validación de Hibernate (`ddl-auto: validate`) hasta que exista la migración inicial de base de datos. Esto es esperado y se resuelve en el siguiente paso del proyecto (migraciones Flyway); no es un error de la configuración Docker.

Las tablas se crean automáticamente al levantar el backend mediante las migraciones Flyway versionadas (`V1__esquema_inicial.sql`), lo que hace que el entorno sea reproducible tanto en local como en Docker.

---

## Autenticación y seguridad

El backend implementa autenticación real basada en JWT:

- El login se realiza mediante `POST /api/auth/login`, enviando email y contraseña. El backend valida las credenciales y devuelve un token JWT.
- Un filtro JWT intercepta las peticiones protegidas, valida el token y establece el contexto de seguridad para cada request.
- Existen 3 roles de usuario: `ADMIN`, `SOPORTE` y `USUARIO_FINAL`. Los endpoints están protegidos según el rol correspondiente, configurado en `SecurityConfig.java`.
- Las contraseñas de los usuarios se almacenan siempre hasheadas, nunca en texto plano.

---

## Documentación de la API

Con el backend corriendo, la documentación interactiva de la API (Swagger UI / OpenAPI) está disponible en:

```txt
http://localhost:8080/swagger-ui/index.html
```

Desde ahí se pueden explorar y probar todos los endpoints disponibles.

---

## Estructura del proyecto

```txt
src/main/java/com/ryot/helpdesk
│
├── config
│   └── Configuraciones generales del proyecto
│
├── controller
│   └── Controladores REST
│
├── dto
│   └── Objetos para entrada y salida de datos
│
├── entity
│   └── Entidades JPA que representan tablas de la base de datos
│
├── mapper
│   └── Mappers de MapStruct para convertir Entity ↔ DTO
│
├── repository
│   └── Repositorios JPA para acceso a datos
│
├── service
│   └── Lógica de negocio
│
├── utils
│   └── Constantes y utilidades generales
│
└── HelpdeskApplication.java