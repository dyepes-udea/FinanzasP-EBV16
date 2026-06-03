# FinanzasP-EBV16 / Control de Finanzas Personales

Aplicación web académica para gestionar finanzas personales. Permite registrar usuarios, iniciar sesión, administrar perfil y gestionar ingresos, gastos, categorías y fuentes de ingreso por usuario autenticado.

El backend está desarrollado con Spring Boot y el frontend está construido con HTML, CSS y JavaScript vanilla, servido directamente desde Spring Boot.

---

## Funcionalidades principales

* Registro e inicio de sesión con correo y contraseña.
* Sesión visual mediante `localStorage`.
* Cierre de sesión.
* Perfil personal con edición de nombre.
* Cambio de foto de perfil por URL o subida de archivo.
* Creación, edición, eliminación y filtro de ingresos y gastos.
* Cada usuario ve únicamente sus propias transacciones.
* Categorías de gasto globales y categorías personalizadas por usuario.
* Fuentes de ingreso globales y fuentes personalizadas por usuario.
* Frontend estático servido desde Spring Boot, sin Node.js.

---

## Tecnologías utilizadas

| Capa          | Tecnologías                                                         |
| ------------- | ------------------------------------------------------------------- |
| Backend       | Java 17, Spring Boot 3.2.4, Spring Web, Spring Data JPA, Validation |
| Base de datos | H2 en memoria                                                       |
| Frontend      | HTML5, CSS3, JavaScript vanilla                                     |
| Build         | Maven                                                               |

---

## Requisitos

* Java 17 o superior.
* Maven 3.6 o superior.
* Navegador web moderno.

---

## Estructura general

```text
src/main/java/com/finanzas/
├── config/
├── controller/
├── dto/
├── entity/
├── repository/
└── ControlFinanzasApplication.java

src/main/resources/
├── application.properties
└── static/
    ├── index.html
    ├── login.html
    ├── registro.html
    ├── perfil.html
    ├── admin.html
    ├── admin-fuentes.html
    ├── css/
    ├── js/
    └── uploads/perfiles/
```

---

## Ejecución local

Clonar el repositorio:

```bash
git clone https://github.com/dyepes-udea/FinanzasP-EBV16.git
cd FinanzasP-EBV16
```

Ejecutar la aplicación:

```bash
mvn spring-boot:run
```

Acceder en el navegador:

```text
http://localhost:8080
```

También se puede generar y ejecutar el `.jar`:

```bash
mvn clean package
java -jar target/*.jar
```

---

## Base de datos H2

El proyecto usa H2 en memoria, por lo tanto los datos se pierden al reiniciar la aplicación.

Consola H2:

```text
http://localhost:8080/h2-console
```

Configuración:

| Campo    | Valor                    |
| -------- | ------------------------ |
| JDBC URL | `jdbc:h2:mem:finanzasdb` |
| User     | `sa`                     |
| Password | vacío                    |

---

## API REST principal

### Autenticación

| Método | Endpoint             | Descripción       |
| ------ | -------------------- | ----------------- |
| POST   | `/api/auth/registro` | Registrar usuario |
| POST   | `/api/auth/login`    | Iniciar sesión    |

Ejemplo:

```json
{
  "correo": "usuario@ejemplo.com",
  "contrasena": "1234"
}
```

---

### Usuario y perfil

| Método | Endpoint                         | Descripción              |
| ------ | -------------------------------- | ------------------------ |
| PATCH  | `/api/usuarios/{id}/nombre`      | Editar nombre            |
| PATCH  | `/api/usuarios/{id}/foto`        | Actualizar foto por URL  |
| POST   | `/api/usuarios/{id}/foto-upload` | Subir foto desde archivo |

Las imágenes subidas se guardan localmente en:

```text
src/main/resources/static/uploads/perfiles
```

---

### Gastos

| Método | Endpoint                          | Descripción               |
| ------ | --------------------------------- | ------------------------- |
| GET    | `/api/gastos?usuarioId={id}`      | Listar gastos del usuario |
| POST   | `/api/gastos`                     | Crear gasto               |
| PUT    | `/api/gastos/{id}?usuarioId={id}` | Editar gasto propio       |
| DELETE | `/api/gastos/{id}?usuarioId={id}` | Eliminar gasto propio     |

Ejemplo:

```json
{
  "descripcion": "Mercado",
  "monto": 85000,
  "fecha": "2026-05-05",
  "categoriaId": 1,
  "usuarioId": 1
}
```

---

### Ingresos

| Método | Endpoint                            | Descripción                 |
| ------ | ----------------------------------- | --------------------------- |
| GET    | `/api/ingresos?usuarioId={id}`      | Listar ingresos del usuario |
| POST   | `/api/ingresos`                     | Crear ingreso               |
| PUT    | `/api/ingresos/{id}?usuarioId={id}` | Editar ingreso propio       |
| DELETE | `/api/ingresos/{id}?usuarioId={id}` | Eliminar ingreso propio     |

Ejemplo:

```json
{
  "descripcion": "Trabajo freelance",
  "monto": 500000,
  "fecha": "2026-05-05",
  "fuenteIngresoId": 2,
  "usuarioId": 1
}
```

---

### Categorías y fuentes

Las categorías y fuentes predefinidas son globales y visibles para todos. Las creadas por un usuario solo son visibles para ese usuario.

| Recurso             | Endpoint base          |
| ------------------- | ---------------------- |
| Categorías de gasto | `/api/categorias`      |
| Fuentes de ingreso  | `/api/fuentes-ingreso` |

En las operaciones de usuario se usa `usuarioId` para listar, crear, editar o eliminar elementos propios.

---

## Despliegue en Render

Antes de desplegar, en `src/main/resources/application.properties` el puerto debe quedar así:

```properties
server.port=${PORT:8080}
```

Esto permite que localmente use `8080`, pero en Render use el puerto asignado por la plataforma.

Pasos para desplegar:

1. Subir el proyecto actualizado a GitHub.
2. Entrar a Render.
3. Crear un nuevo Web Service.
4. Conectar el repositorio de GitHub.
5. Seleccionar la rama que se va a desplegar, por ejemplo `main` o `develop`.
6. Configurar los comandos:

Build Command:

```bash
mvn clean package -DskipTests
```

Start Command:

```bash
java -jar target/*.jar
```

7. Guardar y desplegar.

Render generará una URL pública para acceder a la aplicación.

---

## Notas importantes

* No requiere Node.js.
* El frontend se sirve desde Spring Boot.
* La base de datos H2 es en memoria.
* Los datos se pierden al reiniciar o redeplegar la aplicación.
* Las imágenes subidas se guardan localmente durante la ejecución.
* No implementa Spring Security, JWT ni roles.
* La sesión se maneja en frontend con `localStorage`.
* Las contraseñas se almacenan en texto plano por alcance académico.
* Proyecto académico, no recomendado para producción sin ajustes de seguridad y persistencia.

---

## Autor

Proyecto académico — CodeFactory 2026-1
