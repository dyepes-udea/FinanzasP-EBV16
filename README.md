# FinanzasP-EBV16
# Control de Finanzas Personales

Aplicación web para gestionar ingresos y gastos personales.

- Backend desarrollado con Spring Boot 3
- Frontend en HTML, CSS y JavaScript (vanilla)

---

## Tecnologías utilizadas

### Backend
| Tecnología | Versión | Descripción |
|---|---|---|
| Java | 17 | Lenguaje principal |
| Spring Boot | 3.2.4 | Framework backend |
| Spring Web | 3.2.4 | API REST |
| Spring Data JPA | 3.2.4 | Persistencia con Hibernate |
| Validation | 3.2.4 | Validaciones |
| H2 Database | Runtime | Base de datos en memoria |
| Maven | 3.6+ | Gestión de dependencias |

---

### Frontend
| Tecnología | Descripción |
|---|---|
| HTML5 | Estructura |
| CSS3 | Estilos |
| JavaScript | Lógica del cliente |

Ubicación:
src/main/resources/static/

Incluye:
- index.html → App principal
- admin.html → Categorías de gasto
- admin-fuentes.html → Fuentes de ingreso
- /js/*.js → Lógica del frontend

---

## Requisitos

- Java 17 o superior
- Maven 3.6 o superior

---

## Ejecución local

### 1. Clonar repositorio

git clone https://github.com/dyepes-udea/FinanzasP-EBV16.git  
cd FinanzasP-EBV16

### 2. Ejecutar aplicación

mvn spring-boot:run

### 3. Acceso en navegador

http://localhost:8080

---

## API REST

### Gastos (/api/gastos)

| Método | Endpoint |
|--------|--------|
| GET | /api/gastos |
| POST | /api/gastos |
| DELETE | /api/gastos/{id} |

Ejemplo:

{
  "descripcion": "Cerveza",
  "monto": 20000,
  "fecha": "2026-05-05",
  "categoriaId": 1
}

---

### Ingresos (/api/ingresos)

| Método | Endpoint |
|--------|--------|
| GET | /api/ingresos |
| POST | /api/ingresos |
| DELETE | /api/ingresos/{id} |

Ejemplo:

{
  "descripcion": "Trabajo freelance",
  "monto": 500000,
  "fecha": "2026-05-05",
  "fuenteIngresoId": 2
}

---

### Categorías (/api/categorias)

| Método | Endpoint |
|--------|--------|
| GET | /api/categorias |
| POST | /api/categorias |
| PATCH | /api/categorias/{id} |
| DELETE | /api/categorias/{id} |

Notas:
- Solo se permiten categorías de tipo GASTO
- El tipo se valida en backend

---

### Fuentes de ingreso (/api/fuentes-ingreso)

| Método | Endpoint |
|--------|--------|
| GET | /api/fuentes-ingreso |
| POST | /api/fuentes-ingreso |
| PATCH | /api/fuentes-ingreso/{id} |
| DELETE | /api/fuentes-ingreso/{id} |

---

## Lógica del sistema

- Los gastos requieren categoría
- Los ingresos no usan categoría
- Los ingresos requieren fuenteIngresoId
- Las categorías son únicamente de tipo GASTO
- Las validaciones se realizan en backend

---

## Base de datos (H2)

Acceso:

http://localhost:8080/h2-console

Configuración:

| Campo | Valor |
|------|------|
| JDBC URL | jdbc:h2:mem:testdb |
| User | sa |
| Password | (vacío) |

---

## Estructura del proyecto

```text
src/
└── main/
    ├── java/com/finanzas/
    │   ├── controller/
    │   │   ├── GastoController.java
    │   │   ├── IngresoController.java
    │   │   ├── CategoriaController.java
    │   │   └── FuenteIngresoController.java
    │   ├── entity/
    │   ├── repository/
    │   └── dto/
    └── resources/
        ├── application.properties
        └── static/
            ├── index.html
            ├── admin.html
            ├── admin-fuentes.html
            └── js/
```

## Despliegue en Render

Build:

mvn clean install

Start:

java -jar target/*.jar

Nota: el archivo .jar se genera automáticamente dentro de la carpeta target al ejecutar mvn clean install.

---

## Notas

- No requiere Node.js
- El frontend se sirve desde Spring Boot
- La base de datos es en memoria
- Para producción se recomienda usar una base de datos persistente

---

## Autor

Proyecto académico — CodeFactory 2026-1
