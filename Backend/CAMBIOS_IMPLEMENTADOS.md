# Implementación: Categorías de Gasto Personalizadas

## Resumen de la Historia de Usuario
**Como usuario, quiero crear categorías de gasto personalizadas, para adaptar la aplicación a mis hábitos de gasto específicos.**

## Cambios Implementados

### 1. Nuevas Entidades JPA ✅

#### `Categoria.java`
- Entidad para almacenar categorías personalizadas
- Campos:
  - `id` (Long): Identificador único (generado automáticamente)
  - `nombre` (String): Nombre único de la categoría
  - `descripcion` (String): Descripción opcional
  - `tipo` (TipoCategoria): Enum que indica si es GASTO o INGRESO

#### `TipoCategoria.java`
- Enum con dos valores: `GASTO` e `INGRESO`
- Permite categorizar como gastos o ingresos

### 2. Repositorios ✅

#### `CategoriaRepository`
- Métodos principales:
  - `findByNombre(String)`: Buscar por nombre
  - `findByTipo(TipoCategoria)`: Listar por tipo
  - `findByTipoOrderByNombre(TipoCategoria)`: Listar ordenado por nombre

#### `GastoRepository` (Actualizado)
- Nuevos métodos:
  - `findByCategoriaOrderByFechaDesc(Categoria)`: Filtrar por categoría
  - `findByCategoria_IdOrderByFechaDesc(Long)`: Filtrar por ID de categoría

#### `IngresoRepository` (Actualizado)
- Nuevos métodos:
  - `findByCategoriaOrderByFechaDesc(Categoria)`: Filtrar por categoría
  - `findByCategoria_IdOrderByFechaDesc(Long)`: Filtrar por ID de categoría

### 3. Entidades Actualizadas ✅

#### `Gasto.java`
**Cambios:**
- Campo `categoria` cambiado de `String` a `Categoria` (relación ManyToOne)
- Añadida validación: `@NotNull` en la categoría
- La relación es EAGER para cargar la categoría automáticamente

#### `Ingreso.java`
**Cambios:**
- Campo `categoria` cambiado de `String` a `Categoria` (relación ManyToOne)
- Añadida validación: `@NotNull` en la categoría
- La relación es EAGER para cargar la categoría automáticamente

### 4. Nuevos Controladores REST ✅

#### `CategoriaController`
Endpoints implementados:
- `GET /api/categorias` - Listar todas las categorías
- `GET /api/categorias/tipo/{tipo}` - Listar por tipo (GASTO/INGRESO)
- `GET /api/categorias/{id}` - Obtener una categoría específica
- `POST /api/categorias` - Crear una nueva categoría
- `PUT /api/categorias/{id}` - Actualizar una categoría
- `DELETE /api/categorias/{id}` - Eliminar una categoría

**Validaciones:**
- Previene nombres duplicados (código 409 Conflict)
- Valida que el nombre no sea vacío
- Valida el tipo de categoría

### 5. Controladores Actualizados ✅

#### `GastoController`
**Nuevos métodos:**
- `GET /api/gastos?categoriaId={id}` - Filtrar gastos por categoría
- `GET /api/gastos/{id}` - Obtener un gasto específico
- `PUT /api/gastos/{id}` - Actualizar un gasto

**Validaciones:**
- Verifica que la categoría existe antes de crear/actualizar
- Valida que la categoría sea obligatoria

#### `IngresoController`
**Nuevos métodos:**
- `GET /api/ingresos?categoriaId={id}` - Filtrar ingresos por categoría
- `GET /api/ingresos/{id}` - Obtener un ingreso específico
- `PUT /api/ingresos/{id}` - Actualizar un ingreso

**Validaciones:**
- Verifica que la categoría existe antes de crear/actualizar
- Valida que la categoría sea obligatoria

## Archivos Modificados/Creados

### Creados:
1. `/src/main/java/com/finanzas/entity/Categoria.java`
2. `/src/main/java/com/finanzas/entity/TipoCategoria.java`
3. `/src/main/java/com/finanzas/repository/CategoriaRepository.java`
4. `/src/main/java/com/finanzas/controller/CategoriaController.java`
5. `/API_CATEGORIAS.md` - Documentación detallada de la API

### Modificados:
1. `/src/main/java/com/finanzas/entity/Gasto.java`
2. `/src/main/java/com/finanzas/entity/Ingreso.java`
3. `/src/main/java/com/finanzas/repository/GastoRepository.java`
4. `/src/main/java/com/finanzas/repository/IngresoRepository.java`
5. `/src/main/java/com/finanzas/controller/GastoController.java`
6. `/src/main/java/com/finanzas/controller/IngresoController.java`

## Flujo de Uso

### 1. Crear Categorías Personalizadas
```bash
POST /api/categorias
{
  "nombre": "Alimentación",
  "descripcion": "Gastos en comida",
  "tipo": "GASTO"
}
```

### 2. Registrar Gastos con Categorías
```bash
POST /api/gastos
{
  "descripcion": "Compra en supermercado",
  "monto": 45.50,
  "fecha": "2026-05-04",
  "categoria": {"id": 1}
}
```

### 3. Consultar Gastos por Categoría
```bash
GET /api/gastos?categoriaId=1
```

## Ventajas de la Implementación

✅ **Personalización:** Usuarios pueden crear sus propias categorías
✅ **Validación:** Se previene duplicados y referencias inválidas
✅ **Flexibilidad:** Filtrado fácil de gastos/ingresos por categoría
✅ **Escalabilidad:** Estructura preparada para futuras mejoras
✅ **RESTful:** API siguiendo estándares REST
✅ **Documentación:** Incluye guía completa de uso de la API

## Próximas Mejoras (Opcionales)

- [ ] Agregar estadísticas por categoría
- [ ] Permitir editar categorías predefinidas
- [ ] Agregar colores/iconos a las categorías
- [ ] Historiales de cambios de categorías
- [ ] Exportar reportes por categoría
- [ ] Establecer presupuestos por categoría

## Estado de la Compilación

✅ Proyecto compila sin errores
✅ Todas las dependencias están configuradas
✅ Listo para pruebas y despliegue
