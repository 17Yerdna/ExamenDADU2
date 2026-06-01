# Sistema de Gestión Académica - Guía de Instalación y Ejecución

## Proyecto: Examen DAD2

**Autor:** Andrey Mestanza 
**Fecha:** Junio 2026  
**Tecnologías:** Spring Boot 3.x, Spring Cloud, Docker, MySQL 8.0

---

## 1. Introducción

Este documento describe la arquitectura, instalación y ejecución del **Sistema de Gestión Académica**, un proyecto basado en microservicios desarrollado con Spring Cloud para el examen de DAD2.

El sistema implementa los siguientes patrones de arquitectura de microservicios:

- **API Gateway:** Punto de entrada único para todas las solicitudes de clientes.
- **Service Discovery (Eureka):** Registro y descubrimiento dinámico de servicios.
- **Configuration Server:** Centralización de la configuración externa.
- **Comunicación entre servicios:** OpenFeign para comunicación HTTP/gRPC.

---

## 2. Arquitectura del Sistema

### 2.1 Diagrama de Arquitectura

```
─────────────────────────────────────────────────────────────────────
│                         CLIENTES (HTTP)                             │
└──────────────────────────────┬──────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    3. ms-admin-api-gateway                          │
│                    Spring Cloud Gateway (Puerto: 8080)              │
└──────────┬──────────────────────┬──────────────────────┬────────────┘
           │                      │                      │
           ▼                      ▼                      ▼
┌─────────────────────┐ ┌─────────────────────┐ ─────────────────────┐
│ 4. ms-gestion-      │ │ 5. ms-gestion-      │ │ 6. ms-gestion-      │
│    instructor       │ │    alumno           │ │    taller           │
│ (Puerto: 8081)      │ │ (Puerto: 8082)      │ │ (Puerto: 8083)      │
│ MySQL: 3306         │ │ MySQL: 3307         │ │ MySQL: 3308         │
└─────────────────────┘ └─────────────────────┘ ─────────────────────┘
           ▲                      ▲                      ▲
           │                      │                      │
           └──────────────────────┼──────────────────────┘
                                  │
                    ┌─────────────┴─────────────┐
                    │  OpenFeign (HTTP/gRPC)    │
                    └───────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│  1. ms-admin-config-server (Puerto: 8888)                           │
│  Spring Cloud Config Server                                         │
└─────────────────────────────────────────────────────────────────────┘
                                  │
                                  ▼
┌─────────────────────────────────────────────────────────────────────┐
│  2. ms-admin-registry-server (Puerto: 8761)                         │
│  Spring Cloud Netflix Eureka Server                                 │
└─────────────────────────────────────────────────────────────────────┘
```

### 2.2 Componentes del Sistema

| # | Microservicio | Descripción | Puerto | Tecnología |
|---|--------------|-------------|--------|------------|
| 1 | config-server | Servidor de configuración centralizada | 8888 | Spring Cloud Config |
| 2 | discovery-server | Servidor de registro y descubrimiento | 8761 | Spring Cloud Netflix Eureka |
| 3 | api-gateway | Puerta de enlace y enrutamiento | 8080 | Spring Cloud Gateway |
| 4 | servicio-instructor | Gestión de instructores | 8081 | Spring Boot + MySQL |
| 5 | servicio-alumno | Gestión de alumnos | 8082 | Spring Boot + MySQL |
| 6 | servicio-taller | Gestión de talleres | 8083 | Spring Boot + MySQL |

### 2.3 Flujo de Comunicación

1. **Registro de servicios:** Todos los microservicios se registran en Eureka Server al iniciar.
2. **Obtención de configuración:** Los servicios obtienen su configuración desde Config Server.
3. **Solicitud del cliente:** El cliente realiza peticiones HTTP al API Gateway.
4. **Enrutamiento:** El Gateway enruta las solicitudes al microservicio correspondiente.
5. **Comunicación entre servicios:** Los microservicios se comunican entre sí usando OpenFeign (HTTP/gRPC).

---

## 3. Requisitos Previos

Antes de comenzar la instalación, asegúrese de tener instalado lo siguiente:

### 3.1 Software Requerido

| Herramienta | Versión Mínima | Descripción |
|------------|----------------|-------------|
| Docker | 20.10+ | Plataforma de contenedores |
| Docker Compose | 2.0+ | Orquestación de contenedores |
| Java JDK | 21+ | Solo necesario si se compila manualmente |
| Maven | 3.9+ | Solo necesario si se compila manualmente |
| Git | 2.0+ | Control de versiones |

### 3.2 Verificación de Requisitos

Ejecute los siguientes comandos para verificar las instalaciones:

```bash
# Verificar Docker
docker --version

# Verificar Docker Compose
docker compose version

# Verificar Java (opcional, para compilación manual)
java -version

# Verificar Maven (opcional, para compilación manual)
mvn --version
```

---

## 4. Estructura del Proyecto

```
ExamenDAD2/
├── .env                          # Variables de entorno
├── docker-compose.yml            # Orquestación de contenedores
├── pom.xml                       # POM padre del proyecto
├── config-server/                # Servidor de configuración
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
├── discovery-server/             # Servidor Eureka
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
├── api-gateway/                  # API Gateway
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
├── servicio-instructor/          # Microservicio de instructores
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
├── servicio-alumno/              # Microservicio de alumnos
│   ├── Dockerfile
│   ├── pom.xml
│   ── src/
└── servicio-taller/              # Microservicio de talleres
    ├── Dockerfile
    ├── pom.xml
    └── src/
```

---

## 5. Configuración de Variables de Entorno

El archivo `.env` contiene todas las variables de configuración necesarias:

### 5.1 Configuración de Bases de Datos

```env
MYSQL_ROOT_PASSWORD=mysql123
MYSQL_INSTRUCTOR_DB=db_instructor
MYSQL_ALUMNO_DB=db_alumno
MYSQL_TALLER_DB=db_taller
```

### 5.2 Puertos de Bases de Datos

```env
MYSQL_INSTRUCTOR_PORT=3306
MYSQL_ALUMNO_PORT=3307
MYSQL_TALLER_PORT=3308
```

### 5.3 Puertos de Servicios

```env
CONFIG_SERVER_PORT=8888
DISCOVERY_SERVER_PORT=8761
API_GATEWAY_PORT=8080
SERVICIO_INSTRUCTOR_PORT=8081
SERVICIO_ALUMNO_PORT=8082
SERVICIO_TALLER_PORT=8083
```

### 5.4 Configuración JWT

```env
JWT_SECRET=clave-secreta-muy-segura-para-examen-dad2-2024
JWT_EXPIRATION=900000
```

---

## 6. Dockerfiles

Cada microservicio cuenta con un Dockerfile que utiliza **multi-stage build** para optimizar el tamaño de la imagen final.

### 6.1 Estructura del Dockerfile

```dockerfile
# Etapa 1: Construcción con Maven
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -Dmaven.test.skip=true

# Etapa 2: Imagen final con JRE
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE <PUERTO>

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 6.2 Puertos Expuestos por Servicio

| Servicio | Puerto |
|----------|--------|
| config-server | 8888 |
| discovery-server | 8761 |
| api-gateway | 8080 |
| servicio-instructor | 8081 |
| servicio-alumno | 8082 |
| servicio-taller | 8083 |

---

## 7. Docker Compose

El archivo `docker-compose.yml` orquesta todos los servicios del sistema.

### 7.1 Servicios de Base de Datos

- **mysql-instructor:** Base de datos para el microservicio de instructores
- **mysql-alumno:** Base de datos para el microservicio de alumnos
- **mysql-taller:** Base de datos para el microservicio de talleres

### 7.2 Servicios de Infraestructura

- **config-server:** Servidor de configuración centralizada
- **discovery-server:** Servidor de registro y descubrimiento (Eureka)
- **api-gateway:** Puerta de enlace para enrutamiento

### 7.3 Servicios de Negocio

- **servicio-instructor:** Gestión de instructores
- **servicio-alumno:** Gestión de alumnos
- **servicio-taller:** Gestión de talleres

### 7.4 Red y Volúmenes

```yaml
networks:
  gestion-academica-network:
    driver: bridge

volumes:
  mysql-instructor-data:
  mysql-alumno-data:
  mysql-taller-data:
```

---

## 8. Guía de Instalación y Ejecución

### 8.1 Método 1: Ejecución con Docker Compose (Recomendado)

#### Paso 1: Clonar o acceder al proyecto

```bash
cd ExamenDAD2
```

#### Paso 2: Verificar el archivo .env

Asegúrese de que el archivo `.env` existe en la raíz del proyecto y contiene las variables de configuración correctas.

#### Paso 3: Construir y levantar todos los servicios

```bash
docker compose up --build -d
```

**Descripción del comando:**
- `up`: Inicia los contenedores
- `--build`: Fuerza la reconstrucción de las imágenes
- `-d`: Ejecuta en modo detached (segundo plano)

#### Paso 4: Verificar el estado de los servicios

```bash
docker compose ps
```

#### Paso 5: Verificar los logs de los servicios

```bash
# Ver logs de todos los servicios
docker compose logs -f

# Ver logs de un servicio específico
docker compose logs -f config-server
docker compose logs -f discovery-server
docker compose logs -f api-gateway
```

#### Paso 6: Acceder a los servicios

| Servicio | URL | Descripción |
|----------|-----|-------------|
| Eureka Dashboard | http://localhost:8761 | Panel de registro de servicios |
| API Gateway | http://localhost:8080 | Punto de entrada principal |
| Config Server | http://localhost:8888 | Servidor de configuración |

### 8.2 Método 2: Compilación Manual con Maven

Si prefiere compilar manualmente antes de usar Docker:

#### Paso 1: Compilar el proyecto completo

```bash
mvn clean package -Dmaven.test.skip=true
```

#### Paso 2: Construir las imágenes Docker

```bash
docker compose build
```

#### Paso 3: Iniciar los servicios

```bash
docker compose up -d
```

### 8.3 Método 3: Ejecución Individual de Servicios

Para desarrollo y depuración, puede ejecutar servicios individualmente:

```bash
# Construir imagen de un servicio específico
docker compose build config-server

# Iniciar un servicio específico
docker compose up -d config-server

# Ver logs de un servicio
docker compose logs -f config-server
```

---

## 9. Verificación del Sistema

### 9.1 Verificar Eureka Server

Acceda al panel de Eureka para confirmar que todos los servicios están registrados:

```
http://localhost:8761
```

Debería ver los siguientes servicios registrados:
- API-GATEWAY
- SERVICIO-INSTRUCTOR
- SERVICIO-ALUMNO
- SERVICIO-TALLER

### 9.2 Verificar Config Server

Acceda al servidor de configuración:

```
http://localhost:8888/actuator/health
```

### 9.3 Verificar API Gateway

Pruebe el enrutamiento del gateway:

```bash
# Verificar health del gateway
curl http://localhost:8080/actuator/health

# Acceder a un microservicio a través del gateway
curl http://localhost:8080/instructores
curl http://localhost:8080/alumnos
curl http://localhost:8080/talleres
```

### 9.4 Verificar Bases de Datos

```bash
# Conectar a MySQL de instructores
docker exec -it mysql-instructor mysql -u root -pmysql123 db_instructor

# Conectar a MySQL de alumnos
docker exec -it mysql-alumno mysql -u root -pmysql123 db_alumno

# Conectar a MySQL de talleres
docker exec -it mysql-taller mysql -u root -pmysql123 db_taller
```

---

## 10. Comandos Útiles de Docker

### 10.1 Gestión de Contenedores

```bash
# Ver contenedores en ejecución
docker ps

# Ver todos los contenedores (incluyendo detenidos)
docker ps -a

# Detener todos los servicios
docker compose down

# Detener y eliminar volúmenes
docker compose down -v

# Reiniciar un servicio
docker compose restart servicio-instructor
```

### 10.2 Logs y Depuración

```bash
# Ver logs en tiempo real
docker compose logs -f

# Ver logs de un servicio específico
docker compose logs -f api-gateway

# Ver últimas 100 líneas de logs
docker compose logs --tail=100 servicio-alumno
```

### 10.3 Acceso a Contenedores

```bash
# Acceder a la shell de un contenedor
docker exec -it api-gateway sh

# Acceder a MySQL
docker exec -it mysql-instructor mysql -u root -pmysql123
```

---

## 11. Solución de Problemas

### 11.1 Los servicios no se registran en Eureka

**Problema:** Los microservicios no aparecen en el dashboard de Eureka.

**Solución:**
1. Verificar que Config Server esté saludable primero
2. Verificar que Discovery Server esté saludable
3. Revisar los logs del servicio problemático:
   ```bash
   docker compose logs -f servicio-instructor
   ```
4. Verificar la configuración de `eureka.client.service-url.defaultZone`

### 11.2 Error de conexión a la base de datos

**Problema:** Los servicios no pueden conectar a MySQL.

**Solución:**
1. Verificar que MySQL esté saludable:
   ```bash
   docker compose ps mysql-instructor
   ```
2. Verificar las variables de entorno de conexión
3. Esperar a que MySQL complete su inicialización (puede tardar 30-60 segundos)

### 11.3 Puerto ya en uso

**Problema:** Error de puerto ya en uso.

**Solución:**
1. Verificar puertos en uso:
   ```bash
   # Windows PowerShell
   netstat -ano | findstr :8080
   
   # Linux/Mac
   lsof -i :8080
   ```
2. Detener el proceso que usa el puerto o cambiar el puerto en `.env`

### 11.4 Reconstrucción completa

Si necesita una limpieza completa:

```bash
# Detener y eliminar todo
docker compose down -v --remove-orphans

# Eliminar imágenes
docker rmi $(docker images -q examen-dad2*)

# Reconstruir desde cero
docker compose up --build -d
```

---

## 12. Endpoints del API Gateway

### 12.1 Endpoints Disponibles

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/instructores` | Listar todos los instructores |
| GET | `/instructores/{id}` | Obtener instructor por ID |
| POST | `/instructores` | Crear nuevo instructor |
| PUT | `/instructores/{id}` | Actualizar instructor |
| DELETE | `/instructores/{id}` | Eliminar instructor |
| GET | `/alumnos` | Listar todos los alumnos |
| GET | `/alumnos/{id}` | Obtener alumno por ID |
| POST | `/alumnos` | Crear nuevo alumno |
| PUT | `/alumnos/{id}` | Actualizar alumno |
| DELETE | `/alumnos/{id}` | Eliminar alumno |
| GET | `/talleres` | Listar todos los talleres |
| GET | `/talleres/{id}` | Obtener taller por ID |
| POST | `/talleres` | Crear nuevo taller |
| PUT | `/talleres/{id}` | Actualizar taller |
| DELETE | `/talleres/{id}` | Eliminar taller |

### 12.2 Ejemplos de Uso con cURL

```bash
# Crear instructor
curl -X POST http://localhost:8080/instructores \
  -H "Content-Type: application/json" \
  -d '{"nombre": "Juan", "apellido": "Pérez", "email": "juan@email.com"}'

# Listar instructores
curl http://localhost:8080/instructores

# Crear alumno
curl -X POST http://localhost:8080/alumnos \
  -H "Content-Type: application/json" \
  -d '{"nombre": "María", "apellido": "García", "email": "maria@email.com"}'

# Crear taller
curl -X POST http://localhost:8080/talleres \
  -H "Content-Type: application/json" \
  -d '{"nombre": "Spring Boot Avanzado", "descripcion": "Curso avanzado"}'
```

---

## 13. Patrones de Microservicios Implementados

### 13.1 API Gateway Pattern

El API Gateway actúa como punto de entrada único para todas las solicitudes de clientes, proporcionando:
- Enrutamiento de solicitudes a los microservicios correspondientes
- Balanceo de carga
- Posibilidad de implementar filtros de seguridad, logging, etc.

### 13.2 Service Discovery Pattern

Eureka Server proporciona:
- Registro automático de servicios al iniciar
- Descubrimiento dinámico de servicios
- Heartbeat para monitoreo de salud de servicios

### 13.3 Externalized Configuration Pattern

Config Server proporciona:
- Configuración centralizada y externa
- Posibilidad de actualizar configuración sin reiniciar servicios
- Soporte para diferentes perfiles (dev, prod, etc.)

### 13.4 Circuit Breaker Pattern

OpenFeign permite:
- Comunicación tipo cliente entre microservicios
- Posibilidad de implementar circuit breakers con Resilience4j
- Load balancing integrado con Ribbon

---

## 14. Consideraciones de Producción

### 14.1 Seguridad

- Cambiar la contraseña de MySQL por defecto
- Actualizar el JWT_SECRET en producción
- Implementar HTTPS en el API Gateway
- Configurar autenticación y autorización

### 14.2 Monitoreo

- Implementar Spring Boot Actuator en todos los servicios
- Configurar Prometheus y Grafana para métricas
- Implementar logging centralizado con ELK Stack

### 14.3 Escalabilidad

- Configurar réplicas de servicios con Docker Swarm o Kubernetes
- Implementar balanceo de carga
- Configurar auto-scaling basado en métricas

---

## 15. Referencias

- Spring Cloud: https://spring.io/projects/spring-cloud
- Spring Cloud Gateway: https://spring.io/projects/spring-cloud-gateway
- Netflix Eureka: https://github.com/Netflix/eureka
- Docker: https://docs.docker.com
- Docker Compose: https://docs.docker.com/compose/

---

## 16. Contacto

Para consultas o soporte, contactar al autor del proyecto.

---

*Documento generado para el Examen DAD2 - Sistema de Gestión Académica*
