# Guía de Edición de Categorías - Historia de Usuario Implementada

## Descripción de la Historia de Usuario
**Como usuario, quiero editar mis categorías de gasto personalizadas, para corregir o actualizar su información.**

## Nuevos Endpoints para Edición

### 1. Actualización Completa (PUT)
**Actualizar todos los campos de una categoría:**

```http
PUT /api/categorias/{id}
Content-Type: application/json

{
  "nombre": "Alimentación y Bebidas",
  "descripcion": "Gastos en comida, bebidas y supermercado",
  "tipo": "GASTO"
}
```

**Respuesta (200 OK):**
```json
{
  "id": 1,
  "nombre": "Alimentación y Bebidas",
  "descripcion": "Gastos en comida, bebidas y supermercado",
  "tipo": "GASTO"
}
```

**Errores Posibles:**
- `404 Not Found` - La categoría no existe
- `409 Conflict` - El nombre ya existe en otra categoría
- `400 Bad Request` - Datos incompletos o inválidos

---

### 2. Actualización Parcial (PATCH)
**Actualizar solo los campos que envíes:**

```http
PATCH /api/categorias/{id}
Content-Type: application/json

{
  "nombre": "Alimentación Actualizada"
}
```

Puedes enviar uno o más campos. Los campos no incluidos no se modificarán.

**Respuesta (200 OK):**
```json
{
  "id": 1,
  "nombre": "Alimentación Actualizada",
  "descripcion": "Gastos en comida y supermercado",
  "tipo": "GASTO"
}
```

---

### 3. Actualizar Solo el Nombre
**Endpoint especializado para cambiar el nombre:**

```http
PATCH /api/categorias/{id}/nombre
Content-Type: application/json

{
  "nombre": "Comida y Bebida"
}
```

**Ventajas:**
- Request más simple
- Específico para renombramiento
- Validación enfocada

**Respuesta (200 OK):**
```json
{
  "id": 1,
  "nombre": "Comida y Bebida",
  "descripcion": "Gastos en comida y supermercado",
  "tipo": "GASTO"
}
```

---

### 4. Actualizar Solo la Descripción
**Endpoint especializado para actualizar la descripción:**

```http
PATCH /api/categorias/{id}/descripcion
Content-Type: application/json

{
  "descripcion": "Todos los gastos relacionados con alimentación"
}
```

**Respuesta (200 OK):**
```json
{
  "id": 1,
  "nombre": "Alimentación",
  "descripcion": "Todos los gastos relacionados con alimentación",
  "tipo": "GASTO"
}
```

---

## Comparación de Métodos de Actualización

| Método | Endpoint | Uso | Campos Requeridos |
|--------|----------|-----|-------------------|
| PUT | `/api/categorias/{id}` | Actualizar todo | Todos (nombre, descripcion, tipo) |
| PATCH | `/api/categorias/{id}` | Actualizar parcial | Mínimo 1 de los 3 |
| PATCH | `/api/categorias/{id}/nombre` | Solo cambiar nombre | solo "nombre" |
| PATCH | `/api/categorias/{id}/descripcion` | Solo actualizar descripción | solo "descripcion" |

---

## Flujo Típico de Edición

### Escenario 1: Cambiar el nombre de una categoría

**Paso 1:** Obtener la categoría actual
```bash
curl http://localhost:8080/api/categorias/1
```

**Paso 2:** Actualizar solo el nombre
```bash
curl -X PATCH http://localhost:8080/api/categorias/1/nombre \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Alimentación y Bebidas"
  }'
```

---

### Escenario 2: Mejorar la descripción de una categoría

**Paso 1:** Actualizar la descripción
```bash
curl -X PATCH http://localhost:8080/api/categorias/2/descripcion \
  -H "Content-Type: application/json" \
  -d '{
    "descripcion": "Transporte público, taxi, Uber y combustible"
  }'
```

---

### Escenario 3: Cambiar múltiples campos a la vez

**Opción 1: Con PUT (requiere todos los campos)**
```bash
curl -X PUT http://localhost:8080/api/categorias/3 \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Entretenimiento y Ocio",
    "descripcion": "Cine, conciertos, videojuegos, libros",
    "tipo": "GASTO"
  }'
```

**Opción 2: Con PATCH (solo los que cambien)**
```bash
curl -X PATCH http://localhost:8080/api/categorias/3 \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Entretenimiento y Ocio",
    "descripcion": "Cine, conciertos, videojuegos, libros"
  }'
```

---

## Validaciones Implementadas

### 1. Validación de Existencia
- La categoría debe existir (ID válido)
- Retorna `404 Not Found` si no existe

### 2. Validación de Nombre Duplicado
- No se permite cambiar a un nombre que ya existe
- Retorna `409 Conflict`
- Se permite cambiar a otros campos mientras se mantiene el mismo nombre

### 3. Validación de Nombre Vacío
- El nombre no puede estar vacío
- Retorna `400 Bad Request`

### 4. Validación de Integridad Referencial
- Las categorías vinculadas a gastos/ingresos no se eliminan (en implementaciones futuras)

---

## Respuestas de Error

### 404 Not Found
```json
{
  "codigo": 404,
  "mensaje": "Categoría no encontrada",
  "detalles": "La categoría con ID 999 no existe"
}
```

### 409 Conflict (Nombre Duplicado)
```json
{
  "codigo": 409,
  "mensaje": "Nombre duplicado",
  "detalles": "Ya existe otra categoría con el nombre 'Alimentación'"
}
```

### 400 Bad Request (Nombre Vacío)
```json
{
  "codigo": 400,
  "mensaje": "Nombre inválido",
  "detalles": "El nombre no puede estar vacío"
}
```

---

## Ejemplos con JavaScript/Fetch API

### Actualizar nombre (PATCH especializado)
```javascript
async function actualizarNombre(id, nuevoNombre) {
  const response = await fetch(`http://localhost:8080/api/categorias/${id}/nombre`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ nombre: nuevoNombre })
  });
  return await response.json();
}

// Uso
actualizarNombre(1, "Comida");
```

### Actualización parcial (PATCH genérica)
```javascript
async function actualizarCategoriaParcial(id, datos) {
  const response = await fetch(`http://localhost:8080/api/categorias/${id}`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(datos)
  });
  return await response.json();
}

// Uso
actualizarCategoriaParcial(1, { 
  nombre: "Comida y Bebida",
  descripcion: "Actualizada"
});
```

### Actualización completa (PUT)
```javascript
async function actualizarCategoriaCompleta(id, categoria) {
  const response = await fetch(`http://localhost:8080/api/categorias/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(categoria)
  });
  return await response.json();
}

// Uso - REQUIERE TODOS LOS CAMPOS
actualizarCategoriaCompleta(1, {
  nombre: "Alimentación",
  descripcion: "Gastos en comida",
  tipo: "GASTO"
});
```

---

## Casos de Uso Comunes

### 1. Usuario escribe el nombre de forma incorrecta
```bash
# Cambiar "Alimentacion" a "Alimentación"
curl -X PATCH http://localhost:8080/api/categorias/1/nombre \
  -H "Content-Type: application/json" \
  -d '{"nombre": "Alimentación"}'
```

### 2. Usuario quiere hacer más específica una descripción
```bash
curl -X PATCH http://localhost:8080/api/categorias/2/descripcion \
  -H "Content-Type: application/json" \
  -d '{"descripcion": "Transporte público, taxi y combustible"}'
```

### 3. Usuario quiere reorganizar sus categorías
```bash
# Cambiar múltiples campos
curl -X PATCH http://localhost:8080/api/categorias/3 \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Ocio y Entretenimiento",
    "descripcion": "Cine, conciertos, videojuegos, libros, etc."
  }'
```

### 4. Usuario quiere cambiar completamente una categoría
```bash
# Cambiar nombre, descripción y tipo
curl -X PUT http://localhost:8080/api/categorias/4 \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Bonificación",
    "descripcion": "Bonos y gratificaciones",
    "tipo": "INGRESO"
  }'
```

---

## Recomendaciones de Uso

1. **Para cambios pequeños:** Usa PATCH con endpoint especializado (`/nombre` o `/descripcion`)
2. **Para cambios múltiples:** Usa PATCH genérica
3. **Para cambios completos:** Usa PUT (requiere enviar todos los campos)
4. **Validación:** Siempre maneja los códigos de error 404 y 409

---

## Integración con el Frontend

### Componente React de Edición
```javascript
import { useState } from 'react';

function EditarCategoria({ categoria, onActualizar }) {
  const [nombre, setNombre] = useState(categoria.nombre);
  const [descripcion, setDescripcion] = useState(categoria.descripcion);

  async function guardar() {
    try {
      const response = await fetch(
        `http://localhost:8080/api/categorias/${categoria.id}`,
        {
          method: 'PATCH',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ nombre, descripcion })
        }
      );
      
      if (response.ok) {
        const datos = await response.json();
        onActualizar(datos);
        alert('Categoría actualizada');
      } else if (response.status === 409) {
        alert('El nombre ya existe');
      } else {
        alert('Error al actualizar');
      }
    } catch (error) {
      console.error('Error:', error);
    }
  }

  return (
    <div>
      <input value={nombre} onChange={e => setNombre(e.target.value)} />
      <textarea value={descripcion} onChange={e => setDescripcion(e.target.value)} />
      <button onClick={guardar}>Guardar</button>
    </div>
  );
}
```

---

## Resumen de la HU Implementada

✅ **Edición completa:** PUT /api/categorias/{id}
✅ **Edición parcial:** PATCH /api/categorias/{id}
✅ **Cambiar nombre:** PATCH /api/categorias/{id}/nombre
✅ **Cambiar descripción:** PATCH /api/categorias/{id}/descripcion
✅ **Validaciones robustas:** Prevención de duplicados y datos inválidos
✅ **Mensajes de error descriptivos:** ErrorResponseDTO con detalles
✅ **Listo para usar:** Endpoints probados y documentados
