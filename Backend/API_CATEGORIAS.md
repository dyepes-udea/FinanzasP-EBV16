# API de Categorías Personalizadas - Documentación

## Descripción
Esta funcionalidad permite a los usuarios crear, gestionar y personalizar categorías de gasto e ingreso según sus necesidades específicas.

## Nuevos Endpoints

### 1. Gestión de Categorías

#### Listar todas las categorías
```
GET /api/categorias
```
**Respuesta (200 OK):**
```json
[
  {
    "id": 1,
    "nombre": "Alimentación",
    "descripcion": "Gastos en comida y supermercado",
    "tipo": "GASTO"
  },
  {
    "id": 2,
    "nombre": "Salario",
    "descripcion": "Ingreso principal",
    "tipo": "INGRESO"
  }
]
```

#### Listar categorías por tipo
```
GET /api/categorias/tipo/{tipo}
```
Donde `{tipo}` puede ser: `GASTO` o `INGRESO`

**Ejemplo:**
```
GET /api/categorias/tipo/GASTO
```

#### Obtener una categoría específica
```
GET /api/categorias/{id}
```

#### Crear una nueva categoría
```
POST /api/categorias
Content-Type: application/json

{
  "nombre": "Transporte",
  "descripcion": "Gastos en transporte público y combustible",
  "tipo": "GASTO"
}
```

**Respuesta (201 Created):**
```json
{
  "id": 3,
  "nombre": "Transporte",
  "descripcion": "Gastos en transporte público y combustible",
  "tipo": "GASTO"
}
```

#### Actualizar una categoría
```
PUT /api/categorias/{id}
Content-Type: application/json

{
  "nombre": "Transporte Público",
  "descripcion": "Gastos actualizados",
  "tipo": "GASTO"
}
```

#### Eliminar una categoría
```
DELETE /api/categorias/{id}
```

---

### 2. Crear Gastos con Categoría

#### Crear un gasto (versión actualizada)
```
POST /api/gastos
Content-Type: application/json

{
  "descripcion": "Compra en supermercado",
  "monto": 45.50,
  "fecha": "2026-05-04",
  "categoria": {
    "id": 1
  }
}
```

**Respuesta (201 Created):**
```json
{
  "id": 1,
  "descripcion": "Compra en supermercado",
  "monto": 45.50,
  "fecha": "2026-05-04",
  "categoria": {
    "id": 1,
    "nombre": "Alimentación",
    "descripcion": "Gastos en comida y supermercado",
    "tipo": "GASTO"
  }
}
```

#### Listar gastos (con filtro opcional)
```
GET /api/gastos                           # Todos los gastos
GET /api/gastos?categoriaId=1             # Gastos de la categoría 1
```

#### Actualizar un gasto
```
PUT /api/gastos/{id}
Content-Type: application/json

{
  "descripcion": "Compra actualizada",
  "monto": 50.00,
  "fecha": "2026-05-04",
  "categoria": {
    "id": 1
  }
}
```

#### Obtener un gasto específico
```
GET /api/gastos/{id}
```

#### Eliminar un gasto
```
DELETE /api/gastos/{id}
```

---

### 3. Crear Ingresos con Categoría

Los endpoints de ingresos siguen la misma estructura que los de gastos:

```
POST /api/ingresos              # Crear ingreso
GET /api/ingresos               # Listar ingresos (con opción ?categoriaId=X)
GET /api/ingresos/{id}          # Obtener un ingreso
PUT /api/ingresos/{id}          # Actualizar ingreso
DELETE /api/ingresos/{id}       # Eliminar ingreso
```

---

## Flujo de Uso Típico

### 1. Crear Categorías Personalizadas
```bash
# Crear categoría de "Alimentación"
POST /api/categorias
{
  "nombre": "Alimentación",
  "descripcion": "Gastos en comida y supermercado",
  "tipo": "GASTO"
}

# Crear categoría de "Entretenimiento"
POST /api/categorias
{
  "nombre": "Entretenimiento",
  "descripcion": "Cine, conciertos, videojuegos",
  "tipo": "GASTO"
}

# Crear categoría de "Salario"
POST /api/categorias
{
  "nombre": "Salario",
  "descripcion": "Ingresos principales",
  "tipo": "INGRESO"
}
```

### 2. Registrar Gastos con las Categorías Creadas
```bash
# Registrar gasto de alimentación
POST /api/gastos
{
  "descripcion": "Compra en supermercado",
  "monto": 45.50,
  "fecha": "2026-05-04",
  "categoria": { "id": 1 }
}

# Registrar gasto de entretenimiento
POST /api/gastos
{
  "descripcion": "Entrada de cine",
  "monto": 12.00,
  "fecha": "2026-05-03",
  "categoria": { "id": 2 }
}
```

### 3. Consultar Gastos por Categoría
```bash
# Ver todos los gastos de alimentación
GET /api/gastos?categoriaId=1

# Ver todos los gastos de entretenimiento
GET /api/gastos?categoriaId=2
```

---

## Campos Requeridos

### Categoría
- `nombre` (String, requerido) - Máximo: único en la base de datos
- `descripcion` (String, opcional)
- `tipo` (Enum: GASTO | INGRESO, requerido)

### Gasto / Ingreso
- `descripcion` (String, requerido)
- `monto` (BigDecimal, requerido, debe ser positivo)
- `fecha` (LocalDate, requerido) - Formato: YYYY-MM-DD
- `categoria` (Object con `id`, requerido)

---

## Códigos de Respuesta HTTP

| Código | Significado |
|--------|-------------|
| 200    | OK - Operación exitosa |
| 201    | Created - Recurso creado exitosamente |
| 204    | No Content - Recurso eliminado exitosamente |
| 400    | Bad Request - Datos inválidos o incompletos |
| 404    | Not Found - Recurso no encontrado |
| 409    | Conflict - Nombre de categoría duplicado |

---

## Ejemplos con cURL

### Crear una categoría
```bash
curl -X POST http://localhost:8080/api/categorias \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Utilidades",
    "descripcion": "Servicios básicos",
    "tipo": "GASTO"
  }'
```

### Listar categorías de gastos
```bash
curl http://localhost:8080/api/categorias/tipo/GASTO
```

### Crear un gasto
```bash
curl -X POST http://localhost:8080/api/gastos \
  -H "Content-Type: application/json" \
  -d '{
    "descripcion": "Pago de servicios",
    "monto": 120.00,
    "fecha": "2026-05-04",
    "categoria": {"id": 3}
  }'
```

### Filtrar gastos por categoría
```bash
curl "http://localhost:8080/api/gastos?categoriaId=3"
```

---

## Validaciones

1. **Nombre de categoría único:** No se pueden crear dos categorías con el mismo nombre
2. **Categoría obligatoria:** Todo gasto/ingreso debe tener una categoría asignada
3. **Categoría válida:** La categoría debe existir en la base de datos
4. **Monto positivo:** El monto debe ser mayor a 0
5. **Fecha requerida:** Es obligatorio especificar la fecha del gasto/ingreso
