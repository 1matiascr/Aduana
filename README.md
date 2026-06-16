# 🛂 Frontera Inteligente Los Libertadores
## Sistema de Gestión y Control Fronterizo

Sistema de microservicios Spring Boot para la gestión y control del paso fronterizo Los Libertadores (Chile-Argentina).

---

## 🏗️ Arquitectura de Microservicios

```
                    ┌─────────────────┐
                    │   API GATEWAY   │  :8080
                    │  (Punto único   │
                    │   de entrada)   │
                    └────────┬────────┘
                             │
          ┌──────────────────┼──────────────────┐
          │                  │                  │
    ┌─────▼──────┐   ┌───────▼──────┐  ┌───────▼──────┐
    │  ms-auth   │   │ms-preregistro│  │ms-validacion │
    │   :8081    │   │    :8084     │  │    :8085     │
    └────────────┘   └──────┬───────┘  └──────────────┘
                            │ Kafka
                     ┌──────▼───────┐
                     │ms-notificac  │   ┌──────────────┐
                     │    :8083     │   │ms-fila-virt  │
                     └──────────────┘   │    :8082     │
                                        └──────┬───────┘
                                               │ RestTemplate
                                        ┌──────▼───────┐
                                        │ms-operaciones│
                                        │    :8087     │
                                        └──────────────┘
                     ┌────────────────┐
                     │  ms-reportes  │
                     │    :8086      │
                     └───────────────┘
                     ┌────────────────┐
                     │ eureka-server  │
                     │    :8761      │
                     └───────────────┘
```

---

## 📦 Microservicios

| Microservicio       | Puerto | Descripción                                              |
|---------------------|--------|----------------------------------------------------------|
| `eureka-server`     | 8761   | Servidor de descubrimiento de servicios (Eureka)         |
| `api-gateway`       | 8080   | Punto de entrada único, enruta peticiones                |
| `ms-autenticacion`  | 8081   | Login y registro de usuarios (JWT + BCrypt)              |
| `ms-fila-virtual`   | 8082   | Cola virtual de vehículos en espera                      |
| `ms-notificaciones` | 8083   | Consumidor Kafka, asigna turnos en Redis                 |
| `ms-preregistro`    | 8084   | Prerregistro de viajeros y generación de tickets QR      |
| `ms-validacion`     | 8085   | Validación de identidad y documentos en el control       |
| `ms-reportes`       | 8086   | Dashboard con métricas en tiempo real                    |
| `ms-operaciones`    | 8087   | Revisión aduanera de vehículos y carga                   |

---

## 🔄 Flujo del Proceso

```
1. PRERREGISTRO (ms-preregistro :8084)
   └── El viajero registra sus datos antes de llegar → genera código QR

2. GENERACIÓN DE TICKET (ms-notificaciones :8083)
   └── Kafka publica evento → ms-notificaciones asigna turno en fila virtual (Redis)

3. CONTROL DE IDENTIDAD (ms-validacion :8085)
   └── Funcionario escanea cédula/pasaporte → verifica contra el preregistro

4. REVISIÓN ADUANERA (ms-operaciones :8087)
   └── Funcionario revisa vehículo, equipaje y documentación

5. PASO AUTORIZADO (ms-fila-virtual :8082)
   └── Si todo aprobado → el sistema registra y autoriza el ingreso
```

---

## 🚀 Cómo levantar el sistema

### Prerrequisitos
- Java 17+
- Maven 3.8+
- PostgreSQL 15+
- Apache Kafka 3.x
- Redis 7+

### Bases de datos requeridas (PostgreSQL)
```sql
CREATE DATABASE db_usuarios;      -- ms-autenticacion
CREATE DATABASE db_tramites;      -- ms-preregistro
CREATE DATABASE db_validaciones;  -- ms-validacion
CREATE DATABASE db_operaciones;   -- ms-operaciones
```

### Orden de inicio (¡importante!)
```bash
# 1. Primero el servidor Eureka
cd eureka-server && ./mvnw spring-boot:run

# 2. Luego los microservicios (en cualquier orden)
cd ms-autenticacion  && ./mvnw spring-boot:run
cd ms-preregistro    && ./mvnw spring-boot:run
cd ms-validacion     && ./mvnw spring-boot:run
cd ms-operaciones    && ./mvnw spring-boot:run
cd MS-Fila-Virtual   && ./mvnw spring-boot:run
cd ms-notificaciones && ./mvnw spring-boot:run
cd ms-reportes       && ./mvnw spring-boot:run

# 3. Por último el API Gateway
cd api-gateway && ./mvnw spring-boot:run
```

---

## 📡 Endpoints principales (via API Gateway :8080)

### Autenticación
```
POST /api/v1/auth/registro   → Registrar nuevo funcionario o viajero
POST /api/v1/auth/login      → Obtener token JWT
```

### Prerregistro
```
POST  /api/preregistro                    → Crear prerregistro y obtener QR
GET   /api/preregistro/{idTramite}        → Consultar estado del trámite
GET   /api/preregistro/viajero/{rut}      → Ver todos los trámites de un viajero
PATCH /api/preregistro/{id}/estado        → Actualizar estado (funcionario)
```

### Validación de Identidad
```
POST /api/validacion                      → Validar documento vs preregistro
GET  /api/validacion/tramite/{idTramite}  → Ver resultado de validación
GET  /api/validacion/estadisticas         → Estadísticas del día
```

### Fila Virtual
```
GET    /api/fila              → Ver todos los vehículos en espera
POST   /api/fila              → Agregar vehículo a la fila
DELETE /api/fila/atender      → Atender el primer vehículo (llama a ms-operaciones)
GET    /api/fila/tipo/{tipo}  → Filtrar por tipo (Camión/Auto)
```

### Operaciones Aduaneras
```
POST /api/operaciones/revisar             → Revisión completa de un vehículo
GET  /api/operaciones/validar/{patente}   → Validación rápida por patente
GET  /api/operaciones/historial/{patente} → Historial de revisiones
```

### Dashboard y Reportes
```
GET /api/reportes/dashboard              → Métricas consolidadas del día
GET /api/reportes/estado-sistema         → Estado de todos los microservicios
```

---

## 🔗 Integración con sistemas externos

| Sistema | Descripción                             |
|---------|-----------------------------------------|
| **PDI** | Policía de Investigaciones (identidad)  |
| **SII** | Servicio de Impuestos Internos          |
| **SAG** | Servicio Agrícola y Ganadero            |
| **TGR** | Tesorería General de la República       |

---

## 🛠️ Tecnologías utilizadas

- **Spring Boot 3.5.14** — Framework principal
- **Spring Cloud Gateway** — API Gateway con load balancing
- **Netflix Eureka** — Service Discovery
- **Apache Kafka** — Bus de eventos asíncrono
- **Redis** — Almacenamiento en memoria para fila virtual
- **PostgreSQL** — Base de datos relacional
- **JWT (JJWT)** — Autenticación y autorización
- **Lombok** — Reducción de código boilerplate
