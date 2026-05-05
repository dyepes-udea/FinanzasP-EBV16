# Control de Finanzas Personalizadas - Categorías Implementadas

## 📋 Historias de Usuario Implementadas

### ✅ HU1: Crear Categorías de Gasto Personalizadas
**Descripción:** Como usuario, quiero crear categorías de gasto personalizadas, para adaptar la aplicación a mis hábitos de gasto específicos.

**Estado:** Completada
**Endpoints:** 6 (GET, POST, DELETE básicos)
**Documentación:** [API_CATEGORIAS.md](API_CATEGORIAS.md)

### ✅ HU2: Editar Categorías de Gasto Personalizadas
**Descripción:** Como usuario, quiero editar mis categorías de gasto personalizadas, para corregir o actualizar su información.

**Estado:** Completada
**Nuevos Endpoints:** 4 (PUT, PATCH x3)
**Documentación:** [EDICION_CATEGORIAS.md](EDICION_CATEGORIAS.md)

---

## 🏗️ Arquitectura Implementada

### Entidades JPA
```
┌─────────────────┐
│   Categoria     │
├─────────────────┤
│ - id (PK)       │
│ - nombre (U)    │
│ - descripcion   │
│ - tipo (ENUM)   │
└────────┬────────┘
         │ 1
         │
         ├─────────────────────────────────┐
         │                                 │
    ┌────▼────┐                      ┌────▼────┐
    │  Gasto  │                      │ Ingreso │
    └─────────┘                      └─────────┘
```

### Data Transfer Objects (DTOs)
- **ActualizarCategoriaDTO** - Para cambios de categorías
- **CategoriaResponseDTO** - Para respuestas consistentes
- **ErrorResponseDTO** - Para errores descriptivos

---

## 🔌 API REST - Endpoints Disponibles

### Gestión de Categorías (9 endpoints)

#### Lectura
```http
GET /api/categorias                          # Todas las categorías
GET /api/categorias/{id}                     # Una categoría
GET /api/categorias/tipo/{tipo}              # Por tipo (GASTO/INGRESO)
```

#### Creación
```http
POST /api/categorias
Content-Type: application/json

{
  "nombre": "Alimentación",
  "descripcion": "Gastos en comida",
  "tipo": "GASTO"
}
```

#### Actualización Completa (PUT)
```http
PUT /api/categorias/{id}
{
  "nombre": "Comidas y Bebidas",
  "descripcion": "Actualizado",
  "tipo": "GASTO"
}
```

#### Actualizaciones Parciales (PATCH)
```http
# Cambiar múltiples campos
PATCH /api/categorias/{id}
{ "nombre": "Nuevo nombre", "descripcion": "Nueva desc" }

# Solo nombre
PATCH /api/categorias/{id}/nombre
{ "nombre": "Nuevo nombre" }

# Solo descripción
PATCH /api/categorias/{id}/descripcion
{ "descripcion": "Nueva descripción" }
```

#### Eliminación
```http
DELETE /api/categorias/{id}
```

### Gestos e Ingresos (Mejorados)

```http
GET /api/gastos                              # Todos
GET /api/gastos?categoriaId=1                # Filtrados por categoría
GET /api/gastos/{id}                         # Uno específico
POST /api/gastos                             # Crear con categoría
PUT /api/gastos/{id}                         # Actualizar
DELETE /api/gastos/{id}                      # Eliminar

# Misma estructura para /api/ingresos
```

---

## 💾 Estructura de Archivos Creados

```
src/main/java/com/finanzas/
├── entity/
│   ├── Categoria.java           ✅ Nueva
│   ├── TipoCategoria.java       ✅ Nueva (Enum)
│   ├── Gasto.java               ✏️ Actualizada
│   └── Ingreso.java             ✏️ Actualizada
├── dto/
│   ├── ActualizarCategoriaDTO.java      ✅ Nueva
│   ├── CategoriaResponseDTO.java        ✅ Nueva
│   └── ErrorResponseDTO.java            ✅ Nueva
├── repository/
│   ├── CategoriaRepository.java         ✅ Nueva
│   ├── GastoRepository.java             ✏️ Actualizada
│   └── IngresoRepository.java           ✏️ Actualizada
└── controller/
    ├── CategoriaController.java         ✅ Nueva + ✏️ Actualizada
    ├── GastoController.java             ✏️ Actualizada
    └── IngresoController.java           ✏️ Actualizada

Documentación/
├── API_CATEGORIAS.md                    ✅ Nueva
├── CAMBIOS_IMPLEMENTADOS.md             ✅ Nueva
├── GUIA_PRUEBAS.md                      ✅ Nueva
├── EDICION_CATEGORIAS.md                ✅ Nueva
├── EDICION_IMPLEMENTADA.md              ✅ Nueva
└── README.md                            ← Este archivo
```

---

## 🚀 Cómo Usar

### 1. Iniciar la Aplicación
```bash
mvn spring-boot:run
```
La app estará disponible en: `http://localhost:8080`

### 2. Crear Categorías
```bash
curl -X POST http://localhost:8080/api/categorias \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Alimentación",
    "descripcion": "Comida y supermercado",
    "tipo": "GASTO"
  }'
```

### 3. Crear Gastos con Categoría
```bash
curl -X POST http://localhost:8080/api/gastos \
  -H "Content-Type: application/json" \
  -d '{
    "descripcion": "Compra semanal",
    "monto": 85.50,
    "fecha": "2026-05-04",
    "categoria": {"id": 1}
  }'
```

### 4. Editar una Categoría
```bash
# Cambiar solo el nombre
curl -X PATCH http://localhost:8080/api/categorias/1/nombre \
  -H "Content-Type: application/json" \
  -d '{"nombre": "Comida y Bebida"}'
```

### 5. Filtrar Gastos por Categoría
```bash
curl "http://localhost:8080/api/gastos?categoriaId=1"
```

---

## ✨ Características Principales

### HU1: Crear Categorías
- ✅ Crear categorías personalizadas
- ✅ Asignar tipo (GASTO o INGRESO)
- ✅ Agregar descripción opcional
- ✅ Prevención de nombres duplicados
- ✅ Validación de datos obligatorios

### HU2: Editar Categorías
- ✅ Actualización completa (PUT)
- ✅ Actualización parcial (PATCH)
- ✅ Editar nombre directamente
- ✅ Editar descripción directamente
- ✅ Prevención de nombres duplicados al editar
- ✅ Mensajes de error descriptivos
- ✅ Códigos HTTP semánticamente correctos

### Validaciones Implementadas
- ✅ Campo nombre: obligatorio, único, no vacío
- ✅ Campo descripción: opcional, puede estar vacío
- ✅ Campo tipo: obligatorio, valores válidos (GASTO/INGRESO)
- ✅ Categoría debe existir en BD
- ✅ Prevención de nombres duplicados

### Manejo de Errores

| Situación | Código | Mensaje |
|-----------|--------|---------|
| Categoría no existe | 404 | Categoría no encontrada |
| Nombre duplicado | 409 | Nombre duplicado |
| Nombre vacío | 400 | Nombre inválido |
| Categoría no asignada en gasto/ingreso | 400 | Bad Request |
| Categoría en gasto/ingreso no existe | 404 | Not Found |

---

## 🧪 Pruebas

### Con curl
Ver [GUIA_PRUEBAS.md](GUIA_PRUEBAS.md) para ejemplos completos

### Con Postman
1. Importar endpoints desde la documentación
2. Crear una colección "Control Finanzas"
3. Ejecutar requests de prueba

### Con PowerShell
```powershell
# Listar categorías
Invoke-RestMethod -Uri "http://localhost:8080/api/categorias" -Method Get

# Crear categoría
$body = @{
    nombre = "Alimentación"
    descripcion = "Gastos en comida"
    tipo = "GASTO"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/categorias" `
  -Method Post `
  -ContentType "application/json" `
  -Body $body
```

---

## 📊 Casos de Uso Implementados

### Caso 1: Crear mis categorías personalizadas
1. Usuario crea categorías que se ajusten a sus gastos
2. Puede crear: Alimentación, Transporte, Entretenimiento, etc.
3. Cada una con su descripción personalizada

### Caso 2: Corregir errores al crear categoría
1. Usuario comete error al escribir nombre (ej: "Alimentacion" en lugar de "Alimentación")
2. Edita el nombre sin perder la categoría
3. Los gastos vinculados mantienen la referencia actualizada

### Caso 3: Actualizar descripción para ser más específico
1. Categoría "Transporte" inicialmente simple
2. Usuario actualiza descripción: "Transporte público, taxi, combustible"
3. Información más clara sin reescribir todo

### Caso 4: Cambiar el tipo de categoría
1. Usuario se da cuenta que clasificó mal (era INGRESO, debe ser GASTO)
2. Edita el tipo sin perder los datos
3. Refleja correctamente en reportes

---

## 🔄 Diagrama de Flujo

```
Usuario
  │
  ├─→ Crear Categoría
  │      └─→ POST /categorias
  │          └─→ Validar nombre único
  │          └─→ Guardar en BD
  │
  ├─→ Crear Gasto con Categoría
  │      └─→ POST /gastos
  │          └─→ Validar categoría existe
  │          └─→ Guardar gasto con referencia
  │
  ├─→ Ver Categorías
  │      └─→ GET /categorias
  │          └─→ Retornar lista
  │
  ├─→ Editar Categoría
  │      ├─→ PUT /categorias/{id}
  │      │    └─→ Actualización completa
  │      ├─→ PATCH /categorias/{id}
  │      │    └─→ Actualización parcial
  │      ├─→ PATCH /categorias/{id}/nombre
  │      │    └─→ Solo nombre
  │      └─→ PATCH /categorias/{id}/descripcion
  │          └─→ Solo descripción
  │
  └─→ Filtrar Gastos por Categoría
         └─→ GET /gastos?categoriaId=X
            └─→ Retornar gastos de esa categoría
```

---

## 📈 Estadísticas del Proyecto

| Métrica | Cantidad |
|---------|----------|
| Archivos Java | 14 |
| Archivos creados | 8 |
| Archivos modificados | 6 |
| Entidades JPA | 4 |
| DTOs creados | 3 |
| Repositorios | 3 |
| Controladores | 3 |
| Endpoints total | 15+ |
| Documentos | 5 |

---

## 🔮 Próximas Mejoras Sugeridas

### Funcionalidades Futuras
- [ ] Historial de cambios en categorías
- [ ] Auditoría (quién/cuándo cambió)
- [ ] Iconos/colores para categorías
- [ ] Presupuestos por categoría
- [ ] Reportes y estadísticas por categoría
- [ ] Categorías predefinidas opcionales
- [ ] Importar/exportar categorías
- [ ] Categorías favoritas

### Mejoras Técnicas
- [ ] Unit tests para servicios
- [ ] Integración tests
- [ ] Documentación Swagger/OpenAPI
- [ ] Paginación en listados
- [ ] Búsqueda y filtros avanzados
- [ ] ETag para control de versiones
- [ ] Soft delete (no eliminar físicamente)
- [ ] Caché de categorías principales

---

## 🛠️ Stack Tecnológico

- **Framework:** Spring Boot 3.2.4
- **Base de datos:** H2 (en memoria)
- **ORM:** Hibernate JPA
- **Build:** Maven
- **Java:** 17
- **Validación:** Jakarta Validation
- **REST:** Spring Web MVC

---

## ✅ Checklist de Implementación

### HU1: Crear Categorías
- [x] Entidad Categoria creada
- [x] Enum TipoCategoria creado
- [x] Repository de Categoria
- [x] Controlador con endpoints CRUD
- [x] Relaciones con Gasto e Ingreso
- [x] Validaciones implementadas
- [x] Documentación del API
- [x] Ejemplos de prueba
- [x] Compilación exitosa

### HU2: Editar Categorías
- [x] DTOs creados (ActualizarCategoria, Response, Error)
- [x] Endpoint PUT para actualización completa
- [x] Endpoint PATCH para actualización parcial
- [x] Endpoints PATCH especializados (nombre, descripción)
- [x] Validaciones robustas
- [x] Manejo de errores mejorado
- [x] ErrorResponseDTO implementado
- [x] Documentación de edición
- [x] Ejemplos de uso
- [x] Compilación exitosa

---

## 📞 Soporte y Contacto

Para más detalles sobre la implementación:
- Ver [API_CATEGORIAS.md](API_CATEGORIAS.md) - Documentación general
- Ver [EDICION_CATEGORIAS.md](EDICION_CATEGORIAS.md) - Documentación de edición
- Ver [GUIA_PRUEBAS.md](GUIA_PRUEBAS.md) - Ejemplos de testing

---

## 📄 Licencia

Este proyecto es parte del curso de Análisis y Diseño de Software 2
Universidad de Antioquia - Semestre 2026-1

---

**Última actualización:** 4 de mayo de 2026
**Estado:** ✅ Completado
