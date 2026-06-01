# Diagramas del Proyecto - Microservicios Spring Cloud

## 1. Diagrama de Arquitectura de Componentes

```mermaid
graph TB
    subgraph Clientes["👥 Capa de Clientes"]
        Web["🖥️ Aplicación Web"]
        Mobile[" Aplicación Móvil"]
    end

    subgraph API["🚪 Capa de Acceso - API Gateway"]
        Gateway["ms-admin-api-gateway<br/>Spring Cloud Gateway<br/>Puerto: 8080"]
    end

    subgraph Infraestructura["⚙️ Servicios de Infraestructura"]
        Config["ms-admin-config-server<br/>Spring Cloud Config<br/>Puerto: 8888"]
        Eureka["ms-admin-registry-server<br/>Eureka Server<br/>Puerto: 8761"]
    end

    subgraph Microservicios["🧩 Capa de Microservicios de Negocio"]
        Instructor["ms-gestion-instructor<br/>Gestión de Instructores<br/>Puerto: 8081"]
        Alumno["ms-gestion-alumno<br/>Gestión de Alumnos<br/>Puerto: 8082"]
        Taller["ms-gestion-taller<br/>Gestión de Talleres<br/>Puerto: 8083"]
    end

    subgraph Datos["💾 Capa de Datos"]
        DB_Instructor["🗄️ BD Instructores<br/>PostgreSQL/MySQL"]
        DB_Alumno["🗄️ BD Alumnos<br/>PostgreSQL/MySQL"]
        DB_Taller["🗄️ BD Talleres<br/>PostgreSQL/MySQL"]
    end

    %% Conexiones de clientes
    Web -->|HTTP/HTTPS| Gateway
    Mobile -->|HTTP/HTTPS| Gateway

    %% Conexiones de infraestructura
    Gateway -.->|Registro/Descubrimiento| Eureka
    Config -.->|Registro/Descubrimiento| Eureka
    Instructor -.->|Registro/Descubrimiento| Eureka
    Alumno -.->|Registro/Descubrimiento| Eureka
    Taller -.->|Registro/Descubrimiento| Eureka

    Gateway -.->|Obtiene configuración| Config
    Instructor -.->|Obtiene configuración| Config
    Alumno -.->|Obtiene configuración| Config
    Taller -.->|Obtiene configuración| Config

    %% Enrutamiento del Gateway
    Gateway -->|Rutea /instructor/**| Instructor
    Gateway -->|Rutea /alumno/**| Alumno
    Gateway -->|Rutea /taller/**| Taller

    %% Comunicación entre microservicios
    Instructor <-->|OpenFeign HTTP/gRPC| Alumno
    Alumno <-->|OpenFeign HTTP/gRPC| Taller
    Instructor <-->|OpenFeign HTTP/gRPC| Taller

    %% Conexiones a bases de datos
    Instructor -->|JDBC/JPA| DB_Instructor
    Alumno -->|JDBC/JPA| DB_Alumno
    Taller -->|JDBC/JPA| DB_Taller

    %% Estilos
    classDef cliente fill:#e3f2fd,stroke:#1565c0,stroke-width:2px
    classDef gateway fill:#fff3e0,stroke:#e65100,stroke-width:2px
    classDef infra fill:#f3e5f5,stroke:#6a1b9a,stroke-width:2px
    classDef microservicio fill:#e8f5e9,stroke:#2e7d32,stroke-width:2px
    classDef datos fill:#fce4ec,stroke:#c62828,stroke-width:2px

    class Web,Mobile cliente
    class Gateway gateway
    class Config,Eureka infra
    class Instructor,Alumno,Taller microservicio
    class DB_Instructor,DB_Alumno,DB_Taller datos
```

## 2. Diagrama de Despliegue

```mermaid
graph TB
    subgraph Internet["🌐 Internet"]
        User["👤 Usuarios Finales"]
    end

    subgraph LoadBalancer["️ Balanceador de Carga<br/>(Nginx / AWS ALB)"]
        LB["Load Balancer<br/>Puerto: 443/80"]
    end

    subgraph DockerHost[" Docker Host / Servidor"]
        subgraph Contenedores["📦 Contenedores Docker"]
            subgraph InfraContainer["Infraestructura"]
                ConfigContainer["📦 ms-admin-config-server<br/>Config Server<br/>Port: 8888"]
                EurekaContainer["📦 ms-admin-registry-server<br/>Eureka Server<br/>Port: 8761"]
            end

            subgraph GatewayContainer["Gateway"]
                GatewayDocker["📦 ms-admin-api-gateway<br/>API Gateway<br/>Port: 8080"]
            end

            subgraph MicroserviciosContainer["Microservicios"]
                InstructorContainer["📦 ms-gestion-instructor<br/>Port: 8081"]
                AlumnoContainer["📦 ms-gestion-alumno<br/>Port: 8082"]
                TallerContainer[" ms-gestion-taller<br/>Port: 8083"]
            end
        end

        subgraph Databases["💾 Bases de Datos"]
            DB1[" PostgreSQL<br/>bd_instructores<br/>Port: 5432"]
            DB2["🐘 PostgreSQL<br/>bd_alumnos<br/>Port: 5433"]
            DB3["🐘 PostgreSQL<br/>bd_talleres<br/>Port: 5434"]
        end
    end

    %% Flujo de despliegue
    User -->|HTTPS| LB
    LB -->|HTTP| GatewayDocker

    %% Registro y configuración
    ConfigContainer -.->|Se registra en| EurekaContainer
    GatewayDocker -.->|Se registra en| EurekaContainer
    InstructorContainer -.->|Se registra en| EurekaContainer
    AlumnoContainer -.->|Se registra en| EurekaContainer
    TallerContainer -.->|Se registra en| EurekaContainer

    GatewayDocker -.->|Lee configuración de| ConfigContainer
    InstructorContainer -.->|Lee configuración de| ConfigContainer
    AlumnoContainer -.->|Lee configuración de| ConfigContainer
    TallerContainer -.->|Lee configuración de| ConfigContainer

    %% Ruteo
    GatewayDocker -->|/api/instructor/**| InstructorContainer
    GatewayDocker -->|/api/alumno/**| AlumnoContainer
    GatewayDocker -->|/api/taller/**| TallerContainer

    %% Comunicación entre servicios
    InstructorContainer <-->|Feign Client| AlumnoContainer
    AlumnoContainer <-->|Feign Client| TallerContainer
    InstructorContainer <-->|Feign Client| TallerContainer

    %% Conexiones a BD
    InstructorContainer -->|JDBC| DB1
    AlumnoContainer -->|JDBC| DB2
    TallerContainer -->|JDBC| DB3

    %% Estilos
    classDef internet fill:#e3f2fd,stroke:#1565c0,stroke-width:2px
    classDef lb fill:#fff3e0,stroke:#e65100,stroke-width:2px
    classDef container fill:#e8f5e9,stroke:#2e7d32,stroke-width:2px
    classDef db fill:#fce4ec,stroke:#c62828,stroke-width:2px

    class User internet
    class LB lb
    class ConfigContainer,EurekaContainer,GatewayDocker,InstructorContainer,AlumnoContainer,TallerContainer container
    class DB1,DB2,DB3 db
```

## 3. Diagrama de Despliegue en Kubernetes (Opcional - Producción)

```mermaid
graph TB
    subgraph K8sCluster["☸️ Cluster Kubernetes"]
        subgraph NamespaceInfra["Namespace: infraestructura"]
            ConfigPod["📦 Config Server Pod<br/>Replicas: 1"]
            EurekaPod[" Eureka Server Pod<br/>Replicas: 2"]
        end

        subgraph NamespaceApp["Namespace: aplicacion"]
            GatewayPod[" API Gateway Pod<br/>Replicas: 2<br/>Service: ClusterIP"]
            InstructorPod["📦 Instructor Pod<br/>Replicas: 2<br/>Service: ClusterIP"]
            AlumnoPod["📦 Alumno Pod<br/>Replicas: 2<br/>Service: ClusterIP"]
            TallerPod["📦 Taller Pod<br/>Replicas: 2<br/>Service: ClusterIP"]
        end

        Ingress[" Ingress Controller<br/>Nginx/Traefik"]
    end

    subgraph ExternalDB["💾 Bases de Datos Externas"]
        RDS1["🐘 RDS PostgreSQL<br/>bd_instructores"]
        RDS2["🐘 RDS PostgreSQL<br/>bd_alumnos"]
        RDS3["🐘 RDS PostgreSQL<br/>bd_talleres"]
    end

    User -->|HTTPS| Ingress
    Ingress --> GatewayPod

    %% Registro
    ConfigPod -.-> EurekaPod
    GatewayPod -.-> EurekaPod
    InstructorPod -.-> EurekaPod
    AlumnoPod -.-> EurekaPod
    TallerPod -.-> EurekaPod

    %% Configuración
    GatewayPod -.-> ConfigPod
    InstructorPod -.-> ConfigPod
    AlumnoPod -.-> ConfigPod
    TallerPod -.-> ConfigPod

    %% Ruteo interno
    GatewayPod --> InstructorPod
    GatewayPod --> AlumnoPod
    GatewayPod --> TallerPod

    %% Comunicación entre pods
    InstructorPod <--> AlumnoPod
    AlumnoPod <--> TallerPod
    InstructorPod <--> TallerPod

    %% Conexiones a BD externas
    InstructorPod --> RDS1
    AlumnoPod --> RDS2
    TallerPod --> RDS3

    classDef k8s fill:#e3f2fd,stroke:#1565c0,stroke-width:2px
    classDef pod fill:#e8f5e9,stroke:#2e7d32,stroke-width:2px
    classDef external fill:#fce4ec,stroke:#c62828,stroke-width:2px

    class Ingress k8s
    class ConfigPod,EurekaPod,GatewayPod,InstructorPod,AlumnoPod,TallerPod pod
    class RDS1,RDS2,RDS3 external
```

## 4. Tabla de Puertos y Servicios

| Servicio | Puerto | Descripción |
|----------|--------|-------------|
| ms-admin-config-server | 8888 | Servidor de configuración centralizada |
| ms-admin-registry-server | 8761 | Servidor de registro Eureka |
| ms-admin-api-gateway | 8080 | Puerta de enlace API |
| ms-gestion-instructor | 8081 | Microservicio de gestión de instructores |
| ms-gestion-alumno | 8082 | Microservicio de gestión de alumnos |
| ms-gestion-taller | 8083 | Microservicio de gestión de talleres |
| PostgreSQL (Instructores) | 5432 | Base de datos de instructores |
| PostgreSQL (Alumnos) | 5433 | Base de datos de alumnos |
| PostgreSQL (Talleres) | 5434 | Base de datos de talleres |

## 5. Tecnologías Utilizadas

| Capa | Tecnología |
|------|------------|
| Framework | Spring Boot 3.x |
| Cloud | Spring Cloud 2023.x |
| Gateway | Spring Cloud Gateway |
| Registry | Netflix Eureka Server |
| Config | Spring Cloud Config Server |
| Comunicación | OpenFeign, HTTP/gRPC |
| Base de Datos | PostgreSQL / MySQL |
| ORM | Spring Data JPA / Hibernate |
| Contenedores | Docker |
| Orquestación | Kubernetes (opcional) |
| CI/CD | GitHub Actions / Jenkins |
