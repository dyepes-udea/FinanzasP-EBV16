# Guía de Pruebas - Categorías Personalizadas

## Cómo Probar la Funcionalidad

### Opción 1: Usando curl (Línea de Comandos)

#### 1. Iniciar la aplicación
```bash
# Desde el directorio del proyecto
mvn spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8080`

#### 2. Crear categorías de prueba

**Crear categoría "Alimentación":**
```bash
curl -X POST http://localhost:8080/api/categorias \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Alimentación",
    "descripcion": "Gastos en comida y supermercado",
    "tipo": "GASTO"
  }'
```
Respuesta esperada:
```json
{
  "id": 1,
  "nombre": "Alimentación",
  "descripcion": "Gastos en comida y supermercado",
  "tipo": "GASTO"
}
```

**Crear categoría "Transporte":**
```bash
curl -X POST http://localhost:8080/api/categorias \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Transporte",
    "descripcion": "Gastos en transporte",
    "tipo": "GASTO"
  }'
```

**Crear categoría "Entretenimiento":**
```bash
curl -X POST http://localhost:8080/api/categorias \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Entretenimiento",
    "descripcion": "Cine, conciertos, etc.",
    "tipo": "GASTO"
  }'
```

**Crear categoría "Salario":**
```bash
curl -X POST http://localhost:8080/api/categorias \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Salario",
    "descripcion": "Ingresos principales",
    "tipo": "INGRESO"
  }'
```

#### 3. Listar categorías

**Listar todas:**
```bash
curl http://localhost:8080/api/categorias
```

**Listar solo gastos:**
```bash
curl http://localhost:8080/api/categorias/tipo/GASTO
```

**Listar solo ingresos:**
```bash
curl http://localhost:8080/api/categorias/tipo/INGRESO
```

#### 4. Crear gastos con categorías

**Gasto de Alimentación:**
```bash
curl -X POST http://localhost:8080/api/gastos \
  -H "Content-Type: application/json" \
  -d '{
    "descripcion": "Compra en Carrefour",
    "monto": 85.50,
    "fecha": "2026-05-04",
    "categoria": {"id": 1}
  }'
```

**Gasto de Transporte:**
```bash
curl -X POST http://localhost:8080/api/gastos \
  -H "Content-Type: application/json" \
  -d '{
    "descripcion": "Uber a la oficina",
    "monto": 15.00,
    "fecha": "2026-05-04",
    "categoria": {"id": 2}
  }'
```

**Gasto de Entretenimiento:**
```bash
curl -X POST http://localhost:8080/api/gastos \
  -H "Content-Type: application/json" \
  -d '{
    "descripcion": "Entrada de cine",
    "monto": 12.00,
    "fecha": "2026-05-03",
    "categoria": {"id": 3}
  }'
```

#### 5. Filtrar gastos por categoría

**Ver todos los gastos de Alimentación (ID=1):**
```bash
curl "http://localhost:8080/api/gastos?categoriaId=1"
```

**Ver todos los gastos de Transporte (ID=2):**
```bash
curl "http://localhost:8080/api/gastos?categoriaId=2"
```

**Ver todos los gastos sin filtro:**
```bash
curl "http://localhost:8080/api/gastos"
```

#### 6. Crear ingresos con categorías

**Ingreso de Salario:**
```bash
curl -X POST http://localhost:8080/api/ingresos \
  -H "Content-Type: application/json" \
  -d '{
    "descripcion": "Salario mensual",
    "monto": 2500.00,
    "fecha": "2026-05-01",
    "categoria": {"id": 4}
  }'
```

#### 7. Actualizar una categoría

```bash
curl -X PUT http://localhost:8080/api/categorias/1 \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Alimentación",
    "descripcion": "Todos los gastos en comida y mercado",
    "tipo": "GASTO"
  }'
```

#### 8. Actualizar un gasto

```bash
curl -X PUT http://localhost:8080/api/gastos/1 \
  -H "Content-Type: application/json" \
  -d '{
    "descripcion": "Compra actualizada en Carrefour",
    "monto": 95.50,
    "fecha": "2026-05-04",
    "categoria": {"id": 1}
  }'
```

#### 9. Eliminar un gasto

```bash
curl -X DELETE http://localhost:8080/api/gastos/1
```

#### 10. Eliminar una categoría

```bash
curl -X DELETE http://localhost:8080/api/categorias/1
```

---

### Opción 2: Usando Postman

#### Crear una colección de pruebas:

1. **Abre Postman**
2. **Crea una nueva colección** llamada "Control Finanzas"
3. **Crea las siguientes requests:**

**Request 1: POST Crear Categoría**
- Method: POST
- URL: `http://localhost:8080/api/categorias`
- Headers: `Content-Type: application/json`
- Body (raw):
```json
{
  "nombre": "Alimentación",
  "descripcion": "Gastos en comida y supermercado",
  "tipo": "GASTO"
}
```

**Request 2: GET Listar todas las categorías**
- Method: GET
- URL: `http://localhost:8080/api/categorias`

**Request 3: GET Listar categorías de gastos**
- Method: GET
- URL: `http://localhost:8080/api/categorias/tipo/GASTO`

**Request 4: POST Crear Gasto**
- Method: POST
- URL: `http://localhost:8080/api/gastos`
- Headers: `Content-Type: application/json`
- Body (raw):
```json
{
  "descripcion": "Compra en supermercado",
  "monto": 85.50,
  "fecha": "2026-05-04",
  "categoria": {"id": 1}
}
```

**Request 5: GET Filtrar gastos por categoría**
- Method: GET
- URL: `http://localhost:8080/api/gastos?categoriaId=1`

---

## Escenarios de Prueba Completos

### Escenario 1: Usuario crea sus categorías personalizadas

1. POST `/api/categorias` - Crear "Alimentación"
2. POST `/api/categorias` - Crear "Transporte"
3. POST `/api/categorias` - Crear "Entretenimiento"
4. GET `/api/categorias` - Verificar que se crearon todas
5. GET `/api/categorias/tipo/GASTO` - Verificar que todas son gastos

**Resultado esperado:** ✅ Se crean 3 categorías de tipo GASTO

---

### Escenario 2: Usuario registra gastos en diferentes categorías

1. POST `/api/gastos` - Gasto de $85.50 en Alimentación
2. POST `/api/gastos` - Gasto de $15.00 en Transporte
3. POST `/api/gastos` - Gasto de $12.00 en Entretenimiento
4. GET `/api/gastos` - Listar todos los gastos (3 total)
5. GET `/api/gastos?categoriaId=1` - Ver solo gastos de Alimentación (1)
6. GET `/api/gastos?categoriaId=2` - Ver solo gastos de Transporte (1)

**Resultado esperado:** ✅ Se crean 3 gastos y se pueden filtrar por categoría

---

### Escenario 3: Usuario intenta crear un gasto sin categoría

1. POST `/api/gastos` con body sin campo `categoria`

**Resultado esperado:** ❌ Error 400 Bad Request

---

### Escenario 4: Usuario intenta crear un gasto con categoría inexistente

1. POST `/api/gastos` con `"categoria": {"id": 999}`

**Resultado esperado:** ❌ Error 404 Not Found

---

### Escenario 5: Usuario intenta crear dos categorías con el mismo nombre

1. POST `/api/categorias` - Crear "Alimentación"
2. POST `/api/categorias` - Intentar crear otra "Alimentación"

**Resultado esperado:** ❌ Error 409 Conflict en la segunda

---

## Comandos PowerShell (Alternativa a curl)

Si usas PowerShell en Windows:

```powershell
# Ver todas las categorías
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

# Crear gasto
$gastoBody = @{
    descripcion = "Compra"
    monto = 85.50
    fecha = "2026-05-04"
    categoria = @{id = 1}
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8080/api/gastos" `
  -Method Post `
  -ContentType "application/json" `
  -Body $gastoBody
```

---

## Notas Importantes

- La aplicación corre en puerto **8080** por defecto
- Las fechas deben estar en formato **YYYY-MM-DD**
- Los montos deben ser **positivos**
- Los nombres de categorías son **únicos**
- Todas las transacciones se guardan en la base de datos **H2 en memoria** (se pierdon al reiniciar)

## Próximas Pruebas

Si implementas un frontend, prueba los endpoints desde JavaScript:

```javascript
// Crear categoría
fetch('http://localhost:8080/api/categorias', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    nombre: 'Alimentación',
    descripcion: 'Gastos en comida',
    tipo: 'GASTO'
  })
})
.then(res => res.json())
.then(data => console.log(data))

// Listar gastos por categoría
fetch('http://localhost:8080/api/gastos?categoriaId=1')
  .then(res => res.json())
  .then(data => console.log(data))
```
