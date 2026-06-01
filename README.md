# Examen DAD2 - Sistema de Gestión Académica

## Descripción

Sistema de microservicios para la gestión académica de instructores, alumnos y talleres. Implementa arquitectura de microservicios con Spring Cloud, incluyendo Config Server, Eureka Server, API Gateway con autenticación JWT, y comunicación entre servicios mediante OpenFeign.

## Arquitectura

```
┌─────────────────────────────────────────────────────────────┐
│                        CLIENTES                              │
└──────────────────────────┬──────────────────────────────────
                           │ HTTP
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                    API GATEWAY (8080)                        │
│              + Autenticación JWT                             │
└──────────────────────────┬──────────────────────────────────┘
                           │ Enrutamiento
                           ▼
    ┌──────────────────────┼──────────────────────┐
    │                      │                      │
    ▼                      ▼                      ▼
┌───────────────┐  ┌───────────────┐  ┌───────────────┐
│  INSTRUCTOR   │  │    ALUMNO     │  │    TALLER     │
│   (8081)      │  │    (8082)     │  │   (8083)      │
│               │  │               │  │  (Compuesto)  │
───────┬───────┘  └───────┬───────┘  └──────────────┘
        │                  │                  │
        └──────────────────┼──────────────────┘
                           │ OpenFeign
                           ▼
┌─────────────────────────────────────────────────────────────┐
│              CONFIG SERVER (8888)                            │
│              DISCOVERY SERVER (8761)                         │
└─────────────────────────────────────────────────────────────┘
```

## Tecnologías

| Componente | Tecnología |
|------------|-----------|
| Java | 21 |
| Spring Boot | 3.5.13 |
| Spring Cloud | 2025.0.2 |
| Build Tool | Maven |
| Base de datos | MySQL 8.0 |
| ORM | Spring Data JPA |
| API Docs | SpringDoc OpenAPI (Swagger) |
| Containerización | Docker + Docker Compose |
| Testing | JUnit 5, Mockito, Testcontainers |

## Microservicios

### 1. config-server (Puerto 8888)
- Spring Cloud Config Server
- Configuración centralizada en modo native

### 2. discovery-server (Puerto 8761)
- Spring Cloud Netflix Eureka Server
- Registro y descubrimiento de servicios

### 3. api-gateway (Puerto 8080)
- Spring Cloud Gateway
- Enrutamiento dinámico
- Autenticación JWT
- Circuit Breaker con Resilience4j

### 4. servicio-instructor (Puerto 8081)
- CRUD de instructores
- Búsqueda por especialidad
- Comunicación con otros servicios vía OpenFeign

### 5. servicio-alumno (Puerto 8082)
- CRUD de alumnos
- Inscripción a talleres
- Historial académico
- Comunicación con otros servicios vía OpenFeign

### 6. servicio-taller (Puerto 8083) - COMPUESTO
- CRUD de talleres
- Gestión de horarios
- Verificación de disponibilidad
- Agregación de datos de instructores y alumnos
- Lógica de negocio compleja

## Requisitos Previos

- Java 21
- Maven 3.8+
- Docker y Docker Compose
- Git

## Estructura del Proyecto

```
ExamenDAD2/
├── pom.xml                          # Parent POM
├── docker-compose.yml               # Orquestación Docker
├── .env                             # Variables de entorno
├── config-server/                   # Config Server
├── discovery-server/                # Eureka Server
├── api-gateway/                     # API Gateway
├── servicio-instructor/             # Microservicio Instructores
├── servicio-alumno/                 # Microservicio Alumnos
└── servicio-taller/                 # Microservicio Talleres (Compuesto)
```

## Cómo Ejecutar

### Opción 1: Docker Compose (Recomendado)

```bash
# Construir y levantar todos los servicios
docker-compose up --build

# Levantar en segundo plano
docker-compose up -d --build

# Detener todos los servicios
docker-compose down

# Detener y eliminar volúmenes
docker-compose down -v
```

### Opción 2: Manual (para desarrollo)

```bash
# 1. Levantar bases de datos
docker-compose up -d mysql-instructor mysql-alumno mysql-taller

# 2. Ejecutar en orden:
cd config-server && mvn spring-boot:run
cd discovery-server && mvn spring-boot:run
cd api-gateway && mvn spring-boot:run
cd servicio-instructor && mvn spring-boot:run
cd servicio-alumno && mvn spring-boot:run
cd servicio-taller && mvn spring-boot:run
```

## Endpoints Principales

### Autenticación
```
POST http://localhost:8080/auth/login
Body: { "username": "admin", "password": "admin123" }
```

**Usuarios de prueba:**
| Usuario | Password | Rol |
|---------|----------|-----|
| admin | admin123 | ADMIN |
| instructor | instructor123 | INSTRUCTOR |
| alumno | alumno123 | ALUMNO |

### Instructores
```
GET    http://localhost:8080/api/instructores
GET    http://localhost:8080/api/instructores/{id}
POST   http://localhost:8080/api/instructores
PUT    http://localhost:8080/api/instructores/{id}
DELETE http://localhost:8080/api/instructores/{id}
GET    http://localhost:8080/api/instructores/search?especialidad=Java
```

### Alumnos
```
GET    http://localhost:8080/api/alumnos
GET    http://localhost:8080/api/alumnos/{id}
POST   http://localhost:8080/api/alumnos
PUT    http://localhost:8080/api/alumnos/{id}
DELETE http://localhost:8080/api/alumnos/{id}
POST   http://localhost:8080/api/alumnos/{id}/inscripciones?tallerId=1
GET    http://localhost:8080/api/alumnos/{id}/inscripciones
```

### Talleres
```
GET    http://localhost:8080/api/talleres
GET    http://localhost:8080/api/talleres/{id}
GET    http://localhost:8080/api/talleres/{id}/completo
POST   http://localhost:8080/api/talleres
PUT    http://localhost:8080/api/talleres/{id}
DELETE http://localhost:8080/api/talleres/{id}
GET    http://localhost:8080/api/talleres/{id}/horarios
GET    http://localhost:8080/api/talleres/{id}/disponibilidad
```

## Swagger UI

- Instructores: http://localhost:8081/swagger-ui.html
- Alumnos: http://localhost:8082/swagger-ui.html
- Talleres: http://localhost:8083/swagger-ui.html

## Eureka Dashboard

http://localhost:8761

## Testing

### Ejecutar todos los tests
```bash
mvn test
```

### Ejecutar tests de un microservicio
```bash
cd servicio-instructor && mvn test
cd servicio-alumno && mvn test
cd servicio-taller && mvn test
```

### Tipos de Tests

1. **Tests Unitarios**: JUnit 5 + Mockito
   - `InstructorServiceTest`
   - `AlumnoServiceTest`
   - `TallerServiceTest`
   - `InstructorControllerTest`
   - `AlumnoControllerTest`
   - `TallerControllerTest`

2. **Tests de Integración**: @DataJpaTest
   - `InstructorRepositoryIntegrationTest`
   - `AlumnoRepositoryIntegrationTest`
   - `TallerRepositoryIntegrationTest`

## Bases de Datos

| Servicio | Base de Datos | Puerto |
|----------|--------------|--------|
| servicio-instructor | db_instructor | 3306 |
| servicio-alumno | db_alumno | 3307 |
| servicio-taller | db_taller | 3308 |

## Variables de Entorno

Ver archivo `.env` para configuración completa.

## Flujo de Comunicación

1. Los servicios se registran en Eureka Server
2. Obtienen su configuración desde Config Server
3. El cliente realiza peticiones al API Gateway
4. El Gateway enruta las solicitudes al microservicio correspondiente
5. Los microservicios se comunican entre sí usando OpenFeign (HTTP)

## Autor

Proyecto desarrollado para el examen DAD2.
