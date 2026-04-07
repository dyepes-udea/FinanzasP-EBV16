# FinanzasP-EBV16
# Control de Finanzas Personales

Aplicación web para gestionar ingresos y gastos personales. Backend desarrollado con **Spring Boot 3** y frontend en **React 18** (via CDN, sin build step).

---

## Tecnologías utilizadas

### Backend
| Dependencia | Versión | Descripción |
|---|---|---|
| Java | 17 | Lenguaje de programación |
| Spring Boot | 3.2.4 | Framework principal |
| spring-boot-starter-web | 3.2.4 | API REST con Spring MVC + Tomcat embebido |
| spring-boot-starter-data-jpa | 3.2.4 | Acceso a datos con Hibernate/JPA |
| spring-boot-starter-validation | 3.2.4 | Validaciones con Jakarta Bean Validation |
| H2 Database | (runtime) | Base de datos en memoria (no requiere instalación) |
| spring-boot-starter-test | (test) | JUnit 5 + Mockito para pruebas |
| spring-boot-maven-plugin | 3.2.4 | Empaquetado y ejecución con Maven |

### Frontend (CDN — sin instalación)
| Librería | Versión | Descripción |
|---|---|---|
| React | 18 | Librería de UI (componentes) |
| ReactDOM | 18 | Renderizado en el navegador |
| Babel Standalone | latest | Transpila JSX directamente en el navegador |

> El frontend vive en `src/main/resources/static/index.html` y es servido automáticamente por Spring Boot como recurso estático.

---

## Requisitos previos

- **Java 17** o superior
- **Maven 3.6+** (o usar el wrapper `./mvnw`)

> **No se necesita Node.js** — el frontend usa React via CDN.

---

## Instalación y ejecución

### 1. Clonar el repositorio

```bash
git clone <url-del-repositorio>
cd control-finanzas
```

### 2. Ejecutar el backend

```bash
mvn spring-boot:run
```

O con el wrapper de Maven:

```bash
./mvnw spring-boot:run   # Linux / macOS
mvnw spring-boot:run # Windows
```

### 3. Abrir la aplicación

Una vez iniciado, abre el navegador en:

```
http://localhost:8080
```

---

## Endpoints de la API REST

### Gastos — `/api/gastos`

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/gastos` | Listar todos los gastos (ordenados por fecha desc) |
| POST | `/api/gastos` | Crear un nuevo gasto |
| DELETE | `/api/gastos/{id}` | Eliminar un gasto por ID |

### Ingresos — `/api/ingresos`

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/ingresos` | Listar todos los ingresos (ordenados por fecha desc) |
| POST | `/api/ingresos` | Crear un nuevo ingreso |
| DELETE | `/api/ingresos/{id}` | Eliminar un ingreso por ID |

### Ejemplo de cuerpo para POST

```json
{
  "descripcion": "Salario mensual",
  "monto": 3500000.00,
  "fecha": "2026-04-07",
  "categoria": "Salario"
}
```

---

### Consola H2 (inspección en tiempo real)

Con el servidor corriendo, accede a:

```
http://localhost:8080/h2-console
```

Usa los datos de conexión de la tabla anterior.

---

## Estructura del proyecto

```
control-finanzas/
├── pom.xml
└── src/
    └── main/
        ├── java/com/finanzas/
        │   ├── ControlFinanzasApplication.java   # Clase principal
        │   ├── controller/
        │   │   ├── GastoController.java           # Endpoints /api/gastos
        │   │   └── IngresoController.java         # Endpoints /api/ingresos
        │   ├── entity/
        │   │   ├── Gasto.java                     # Entidad JPA gastos
        │   │   └── Ingreso.java                   # Entidad JPA ingresos
        │   └── repository/
        │       ├── GastoRepository.java           # Repositorio JPA gastos
        │       └── IngresoRepository.java         # Repositorio JPA ingresos
        └── resources/
            ├── application.properties             # Configuración de la app
            └── static/
                └── index.html                     # Frontend React (CDN)
```

---


