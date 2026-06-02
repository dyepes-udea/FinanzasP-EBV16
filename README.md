# FinanzasP-EBV16 / Control de Finanzas Personales

Aplicación web académica para gestionar finanzas personales. Permite registrar usuarios, iniciar sesión, administrar perfil, crear categorías y fuentes personalizadas, y gestionar ingresos y gastos asociados a cada usuario autenticado.

El backend está desarrollado con Spring Boot y el frontend es estático, construido con HTML, CSS y JavaScript vanilla, servido directamente desde Spring Boot.

---

## Funcionalidades principales

- Registro de usuario con correo y contraseña.
- Inicio de sesión con correo y contraseña.
- Persistencia visual de sesión en navegador mediante `localStorage`.
- Cierre de sesión eliminando la clave `usuarioAutenticado`.
- Visualización de perfil personal.
- Edición del nombre del perfil.
- Actualización de foto de perfil mediante URL.
- Subida de foto de perfil desde archivo local.
- Gestión de ingresos y gastos por usuario autenticado.
- Cada usuario ve, crea, edita y elimina únicamente sus propias transacciones.
- Creación, edición y eliminación de transacciones.
- Filtro de transacciones por tipo: todas, ingresos o gastos.
- Administración de categorías de gasto.
- Categorías de gasto globales/predefinidas visibles para todos.
- Categorías de gasto personalizadas visibles solo para el usuario que las creó.
- Administración de fuentes de ingreso.
- Fuentes de ingreso globales/predefinidas visibles para todos.
- Fuentes de ingreso personalizadas visibles solo para el usuario que las creó.
- Frontend servido por Spring Boot, sin necesidad de Node.js.

---

## Tecnologías utilizadas

### Backend

| Tecnología | Versión | Descripción |
|---|---:|---|
| Java | 17 | Lenguaje principal |
| Spring Boot | 3.2.4 | Framework backend |
| Spring Web | 3.2.4 | API REST |
| Spring Data JPA | 3.2.4 | Persistencia con Hibernate |
| Spring Validation | 3.2.4 | Validaciones |
| H2 Database | Runtime | Base de datos en memoria |
| Maven | 3.6+ | Gestión de dependencias y build |

### Frontend

| Tecnología | Descripción |
|---|---|
| HTML5 | Estructura de páginas |
| CSS3 | Estilos |
| JavaScript vanilla | Lógica del cliente |

Ubicación del frontend:

```text
src/main/resources/static/
```

Archivos principales:

- `index.html`: panel principal de finanzas personales.
- `login.html`: inicio de sesión.
- `registro.html`: registro de usuario.
- `perfil.html`: perfil personal.
- `admin.html`: administración de categorías de gasto.
- `admin-fuentes.html`: administración de fuentes de ingreso.
- `js/admin.js`: lógica de categorías.
- `js/admin-fuentes.js`: lógica de fuentes de ingreso.

---

## Requisitos

- Java 17 o superior.
- Maven 3.6 o superior.
- Navegador web moderno.

No requiere Node.js, porque el frontend estático se sirve directamente desde Spring Boot.

---

## Estructura del proyecto

```text
src/
`-- main/
    |-- java/com/finanzas/
    |   |-- config/
    |   |   |-- DataInitializer.java
    |   |   `-- StaticUploadsConfig.java
    |   |-- controller/
    |   |   |-- AuthController.java
    |   |   |-- UsuarioController.java
    |   |   |-- GastoController.java
    |   |   |-- IngresoController.java
    |   |   |-- CategoriaController.java
    |   |   `-- FuenteIngresoController.java
    |   |-- dto/
    |   |-- entity/
    |   |-- repository/
    |   `-- ControlFinanzasApplication.java
    `-- resources/
        |-- application.properties
        `-- static/
            |-- index.html
            |-- login.html
            |-- registro.html
            |-- perfil.html
            |-- admin.html
            |-- admin-fuentes.html
            |-- css/
            |-- js/
            `-- uploads/perfiles/
```

---

## Ejecución local

### 1. Clonar repositorio

```bash
git clone https://github.com/dyepes-udea/FinanzasP-EBV16.git
cd FinanzasP-EBV16
```

### 2. Ejecutar aplicación

```bash
mvn spring-boot:run
```

### 3. Empaquetar aplicación

```bash
mvn clean package
```

### 4. Ejecutar el JAR generado

```bash
java -jar target/*.jar
```

---

## Acceso local

Aplicación:

```text
http://localhost:8080
```

Pantallas principales:

- `http://localhost:8080/login.html`
- `http://localhost:8080/registro.html`
- `http://localhost:8080/index.html`
- `http://localhost:8080/perfil.html`
- `http://localhost:8080/admin.html`
- `http://localhost:8080/admin-fuentes.html`

---

## Base de datos H2

El proyecto usa H2 en memoria para alcance académico. Los datos se pierden al reiniciar la aplicación.

Consola H2:

```text
http://localhost:8080/h2-console
```

Configuración real:

| Campo | Valor |
|---|---|
| JDBC URL | `jdbc:h2:mem:finanzasdb` |
| User | `sa` |
| Password | vacío |

La propiedad `spring.jpa.hibernate.ddl-auto=create-drop` crea las tablas al iniciar y las elimina al cerrar la aplicación.

---

## API REST principal

> Nota: el sistema usa un alcance académico simple. No implementa Spring Security ni JWT. La relación con el usuario autenticado se controla enviando `usuarioId` desde el frontend.

### Autenticación

| Método | Endpoint | Descripción |
|---|---|---|
| POST | `/api/auth/registro` | Registra un usuario con correo y contraseña |
| POST | `/api/auth/login` | Valida credenciales y devuelve datos mínimos del usuario |

Ejemplo de registro:

```json
{
  "correo": "usuario@ejemplo.com",
  "contrasena": "1234"
}
```

Ejemplo de login:

```json
{
  "correo": "usuario@ejemplo.com",
  "contrasena": "1234"
}
```

Respuesta exitosa de login:

```json
{
  "id": 1,
  "correo": "usuario@ejemplo.com",
  "nombre": "",
  "fotoUrl": ""
}
```

### Usuario y perfil

| Método | Endpoint | Descripción |
|---|---|---|
| PATCH | `/api/usuarios/{id}/nombre` | Actualiza el nombre del usuario |
| PATCH | `/api/usuarios/{id}/foto` | Actualiza la foto mediante una URL |
| POST | `/api/usuarios/{id}/foto-upload` | Sube una imagen usando `multipart/form-data` |

Ejemplo para actualizar nombre:

```json
{
  "nombre": "Juan Pérez"
}
```

Ejemplo para actualizar foto por URL:

```json
{
  "fotoUrl": "https://ejemplo.com/foto.jpg"
}
```

Para subir foto desde archivo:

- Método: `POST`
- Endpoint: `/api/usuarios/{id}/foto-upload`
- Tipo: `multipart/form-data`
- Campo de archivo: `foto`
- Tipos permitidos: `image/jpeg`, `image/png`, `image/webp`, `image/gif`

Las imágenes subidas se guardan localmente en:

```text
src/main/resources/static/uploads/perfiles
```

La URL guardada en el usuario queda con formato:

```text
/uploads/perfiles/nombre-del-archivo.jpg
```

### Gastos

| Método | Endpoint | Parámetros | Descripción |
|---|---|---|---|
| GET | `/api/gastos` | `usuarioId` obligatorio, `categoriaId` opcional | Lista gastos del usuario |
| GET | `/api/gastos/{id}` | `usuarioId` obligatorio | Obtiene un gasto solo si pertenece al usuario |
| POST | `/api/gastos` | Body JSON | Crea un gasto asociado al usuario |
| PUT | `/api/gastos/{id}` | `usuarioId` obligatorio | Edita un gasto solo si pertenece al usuario |
| DELETE | `/api/gastos/{id}` | `usuarioId` obligatorio | Elimina un gasto solo si pertenece al usuario |

Ejemplo para crear gasto:

```json
{
  "descripcion": "Mercado",
  "monto": 85000,
  "fecha": "2026-05-05",
  "categoriaId": 1,
  "usuarioId": 1
}
```

Ejemplo para editar gasto:

```json
{
  "descripcion": "Mercado actualizado",
  "monto": 90000,
  "fecha": "2026-05-06",
  "categoria": {
    "id": 1
  }
}
```

Ruta de edición:

```text
PUT /api/gastos/{id}?usuarioId=1
```

### Ingresos

| Método | Endpoint | Parámetros | Descripción |
|---|---|---|---|
| GET | `/api/ingresos` | `usuarioId` obligatorio, `categoriaId` opcional | Lista ingresos del usuario |
| GET | `/api/ingresos/{id}` | `usuarioId` obligatorio | Obtiene un ingreso solo si pertenece al usuario |
| POST | `/api/ingresos` | Body JSON | Crea un ingreso asociado al usuario |
| PUT | `/api/ingresos/{id}` | `usuarioId` obligatorio | Edita un ingreso solo si pertenece al usuario |
| DELETE | `/api/ingresos/{id}` | `usuarioId` obligatorio | Elimina un ingreso solo si pertenece al usuario |

Ejemplo para crear ingreso:

```json
{
  "descripcion": "Trabajo freelance",
  "monto": 500000,
  "fecha": "2026-05-05",
  "fuenteIngresoId": 2,
  "usuarioId": 1
}
```

Ejemplo para editar ingreso:

```json
{
  "descripcion": "Trabajo freelance actualizado",
  "monto": 600000,
  "fecha": "2026-05-06",
  "fuenteIngreso": {
    "id": 2
  }
}
```

Ruta de edición:

```text
PUT /api/ingresos/{id}?usuarioId=1
```

### Categorías de gasto

Las categorías predefinidas tienen `usuario == null` y son visibles para todos. Las categorías personalizadas tienen usuario asociado y solo son visibles para su propietario.

| Método | Endpoint | Parámetros | Descripción |
|---|---|---|---|
| GET | `/api/categorias` | `usuarioId` opcional | Lista categorías globales y, si se envía `usuarioId`, también las propias |
| GET | `/api/categorias/tipo/{tipo}` | `usuarioId` opcional | Lista por tipo, principalmente `GASTO` |
| GET | `/api/categorias/{id}` | `usuarioId` opcional | Obtiene una categoría si es visible para el usuario |
| POST | `/api/categorias` | Body JSON con `usuarioId` | Crea categoría personalizada |
| PUT | `/api/categorias/{id}` | `usuarioId` obligatorio | Actualiza categoría personalizada propia |
| PATCH | `/api/categorias/{id}` | `usuarioId` obligatorio | Actualiza parcialmente categoría propia |
| PATCH | `/api/categorias/{id}/nombre` | `usuarioId` obligatorio | Actualiza nombre de categoría propia |
| PATCH | `/api/categorias/{id}/descripcion` | `usuarioId` obligatorio | Actualiza descripción de categoría propia |
| GET | `/api/categorias/{id}/referencias` | `usuarioId` obligatorio | Consulta referencias antes de eliminar |
| POST | `/api/categorias/{id}/eliminar` | `usuarioId` obligatorio | Elimina con opciones si hay referencias |
| DELETE | `/api/categorias/{id}` | `usuarioId` obligatorio | Elimina categoría propia sin referencias |

Ejemplo para crear categoría personalizada:

```json
{
  "nombre": "Mascotas",
  "descripcion": "Gastos de mascotas",
  "tipo": "GASTO",
  "usuarioId": 1
}
```

Las categorías globales/predefinidas no pueden ser editadas ni eliminadas por usuarios comunes.

### Fuentes de ingreso

Las fuentes predefinidas tienen `usuario == null` y son visibles para todos. Las fuentes personalizadas tienen usuario asociado y solo son visibles para su propietario.

| Método | Endpoint | Parámetros | Descripción |
|---|---|---|---|
| GET | `/api/fuentes-ingreso` | `usuarioId` opcional | Lista fuentes globales y, si se envía `usuarioId`, también las propias |
| GET | `/api/fuentes-ingreso/{id}` | `usuarioId` opcional | Obtiene una fuente si es visible para el usuario |
| POST | `/api/fuentes-ingreso` | Body JSON con `usuarioId` | Crea fuente personalizada |
| PUT | `/api/fuentes-ingreso/{id}` | `usuarioId` obligatorio | Actualiza fuente personalizada propia |
| PATCH | `/api/fuentes-ingreso/{id}` | `usuarioId` obligatorio | Actualiza parcialmente fuente propia |
| PATCH | `/api/fuentes-ingreso/{id}/nombre` | `usuarioId` obligatorio | Actualiza nombre de fuente propia |
| PATCH | `/api/fuentes-ingreso/{id}/descripcion` | `usuarioId` obligatorio | Actualiza descripción de fuente propia |
| GET | `/api/fuentes-ingreso/{id}/referencias` | `usuarioId` obligatorio | Consulta referencias antes de eliminar |
| POST | `/api/fuentes-ingreso/{id}/eliminar` | `usuarioId` obligatorio | Elimina con opciones si hay referencias |
| DELETE | `/api/fuentes-ingreso/{id}` | `usuarioId` obligatorio | Elimina fuente propia sin referencias |

Ejemplo para crear fuente personalizada:

```json
{
  "nombre": "Freelance",
  "descripcion": "Ingresos por trabajos independientes",
  "usuarioId": 1
}
```

Las fuentes globales/predefinidas no pueden ser editadas ni eliminadas por usuarios comunes.

---

## Despliegue en Render

### 1. Subir el proyecto a GitHub

Confirmar que los cambios estén guardados y subir el proyecto actualizado al repositorio:

```bash
git add .
git commit -m "Actualiza proyecto de finanzas personales"
git push origin main
```

Usar `main`, `develop` u otra rama según la rama real del despliegue.

### 2. Crear servicio en Render

1. Entrar a [Render](https://render.com/).
2. Crear un nuevo **Web Service**.
3. Conectar el repositorio de GitHub.
4. Seleccionar la rama a desplegar, por ejemplo `main` o `develop`.
5. Configurar el runtime como Java o Native Runtime compatible.

### 3. Configurar comandos

Build Command:

```bash
mvn clean package -DskipTests
```

Start Command:

```bash
java -jar target/*.jar
```

### 4. Configurar puerto

Render asigna el puerto mediante la variable de entorno `PORT`. Para que el proyecto sea compatible con Render, `application.properties` debe usar:

```properties
server.port=${PORT:8080}
```

Actualmente el proyecto tiene:

```properties
server.port=8080
```

Por lo tanto, antes de desplegar en Render se debe ajustar esa propiedad para respetar el puerto asignado por la plataforma.

### 5. Acceso público

Después del despliegue, Render genera una URL pública del servicio. Desde esa URL se puede acceder al frontend servido por Spring Boot.

### 6. Consideraciones en Render

- No requiere Node.js.
- El frontend se sirve desde `src/main/resources/static`.
- La base de datos H2 es en memoria.
- Los usuarios, transacciones, categorías personalizadas, fuentes personalizadas y referencias de foto pueden perderse al reiniciar o redeplegar el servicio.
- Las imágenes subidas se guardan localmente en el proyecto durante la ejecución, pero la referencia queda en H2; al reiniciar, esa relación puede perderse.

---

## Notas importantes

- Proyecto académico con alcance simple.
- No implementa Spring Security, JWT ni roles.
- Las contraseñas se almacenan en texto plano por alcance académico; no se recomienda para producción.
- La sesión visual se maneja en frontend con `localStorage`.
- La separación de datos por usuario se realiza enviando `usuarioId` a los endpoints correspondientes.
- H2 se usa en memoria; no hay persistencia real entre reinicios.
- Las categorías y fuentes globales son creadas por inicialización del sistema y se comparten entre usuarios.
- Las categorías y fuentes personalizadas pertenecen al usuario que las creó.

---

## Autor

Proyecto académico — CodeFactory 2026-1
