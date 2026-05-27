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
- Spring Security Crypto
- IntelliJ IDEA
- Postman

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