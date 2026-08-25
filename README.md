# LogiTrack S.A. — Sistema de Gestión y Auditoría de Bodegas

Sistema backend centralizado desarrollado en **Spring Boot** para la gestión de inventarios distribuidos en múltiples bodegas, con control de movimientos, auditoría automática de cambios y seguridad basada en JWT.

---

## 📖 Descripción del proyecto

LogiTrack S.A. administra varias bodegas ubicadas en distintas ciudades, encargadas de almacenar productos y gestionar movimientos de inventario (entradas, salidas y transferencias). Anteriormente este control se realizaba manualmente en hojas de cálculo, sin trazabilidad ni control de accesos.

Este proyecto reemplaza ese proceso manual por un sistema backend robusto que permite:

- Controlar todos los movimientos de inventario entre bodegas.
- Registrar automáticamente los cambios realizados por cada usuario (auditoría).
- Proteger la información mediante autenticación **JWT** y roles (`ADMIN` / `EMPLEADO`).
- Exponer una API REST documentada con **Swagger/OpenAPI 3**.
- Consultar reportes y estadísticas de inventario en tiempo real.

### Objetivo general
Desarrollar un sistema de gestión y auditoría de bodegas que permita registrar transacciones de inventario y generar reportes auditables de los cambios realizados por cada usuario.

---

## 🧱 Arquitectura del proyecto

```
src/
 ├─ controller/     → Endpoints REST (Bodegas, Productos, Movimientos, Auth, Auditoría, Reportes)
 ├─ service/         → Lógica de negocio
 ├─ repository/      → Acceso a datos (Spring Data JPA)
 ├─ model/           → Entidades (Bodega, Producto, Movimiento, Usuario, Auditoria)
 ├─ config/          → Configuración general (Swagger, CORS, etc.)
 ├─ security/        → Spring Security + JWT (filtros, proveedor de tokens, roles)
 └─ exception/       → Manejo global de errores (@ControllerAdvice)

frontend/            → HTML/CSS/JS básico para probar login y consultas principales
schema.sql           → Script de creación de tablas
data.sql             → Datos de prueba iniciales
```

### Módulos funcionales

| Módulo | Descripción |
|---|---|
| **Bodegas** | CRUD completo (id, nombre, ubicación, capacidad, encargado) |
| **Productos** | CRUD completo (id, nombre, categoría, stock, precio) |
| **Movimientos** | Registro de ENTRADA / SALIDA / TRANSFERENCIA entre bodegas |
| **Auditoría** | Registro automático de INSERT/UPDATE/DELETE con usuario y valores anteriores/nuevos |
| **Seguridad** | Login/registro con JWT, rutas protegidas por rol |
| **Reportes** | Stock por bodega, productos más movidos, filtros por fecha/usuario |

---

## ⚙️ Tecnologías utilizadas

- **Java 17+**
- **Spring Boot 3.x**
- **Spring Data JPA**
- **Spring Security + JWT**
- **MySQL**
- **Swagger / OpenAPI 3**
- **Maven**
- **HTML / CSS / JavaScript** (frontend de prueba)

---

## 🚀 Instrucciones de instalación y ejecución

### Requisitos previos
- Java 17 o superior
- Maven 3.8+
- MySQL 8.x instalado y corriendo
- (Opcional) Postman o Swagger UI para probar los endpoints

### 1. Clonar el repositorio
```bash
git clone https://github.com/usuario/logitrack-backend.git
cd logitrack-backend
```

### 2. Configurar la base de datos
Crear la base de datos en MySQL:
```sql
CREATE DATABASE logitrack_db;
```

Editar `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/logitrack_db
spring.datasource.username=root
spring.datasource.password=tu_password
spring.jpa.hibernate.ddl-auto=update
spring.sql.init.mode=always

jwt.secret=clave_secreta_super_segura
jwt.expiration=3600000
```

### 3. Ejecutar scripts SQL (opcional si usas ddl-auto)
Los scripts `schema.sql` y `data.sql` se ejecutan automáticamente al iniciar la aplicación (ubicados en `src/main/resources/`).

### 4. Compilar y ejecutar
```bash
mvn clean install
mvn spring-boot:run
```

La API quedará disponible en:
```
http://localhost:8080
```

### 5. Acceder a la documentación Swagger
```
http://localhost:8080/swagger-ui/index.html
```

### 6. Ejecutar el frontend de prueba
Abrir el archivo `frontend/index.html` en el navegador, o servirlo con una extensión tipo Live Server. Asegúrate de que el backend esté corriendo en `http://localhost:8080`.

---

## 🔐 Autenticación

### Registro de usuario
```http
POST /auth/register
Content-Type: application/json

{
  "username": "jperez",
  "password": "123456",
  "rol": "EMPLEADO"
}
```

### Login
```http
POST /auth/login
Content-Type: application/json

{
  "username": "jperez",
  "password": "123456"
}
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "jperez",
  "rol": "EMPLEADO"
}
```

Para consumir los endpoints protegidos, incluir el token en el header:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## 📌 Ejemplos de endpoints

### Bodegas
| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/bodegas` | Listar todas las bodegas |
| GET | `/bodegas/{id}` | Consultar una bodega |
| POST | `/bodegas` | Crear una bodega |
| PUT | `/bodegas/{id}` | Actualizar una bodega |
| DELETE | `/bodegas/{id}` | Eliminar una bodega (solo ADMIN) |

### Productos
| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/productos` | Listar todos los productos |
| GET | `/productos/stock-bajo` | Productos con stock < 10 |
| POST | `/productos` | Crear un producto |
| PUT | `/productos/{id}` | Actualizar un producto |
| DELETE | `/productos/{id}` | Eliminar un producto (solo ADMIN) |

### Movimientos
| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/movimientos` | Listar movimientos |
| GET | `/movimientos/fecha?inicio=...&fin=...` | Movimientos por rango de fechas |
| POST | `/movimientos` | Registrar entrada/salida/transferencia |

### Auditoría
| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/auditorias` | Listar auditorías (solo ADMIN) |
| GET | `/auditorias/usuario/{username}` | Auditorías por usuario |
| GET | `/auditorias/tipo/{tipoOperacion}` | Auditorías por tipo (INSERT/UPDATE/DELETE) |

### Reportes
| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/reportes/resumen` | Stock total por bodega y productos más movidos |

---

## ⚠️ Manejo de errores

Todas las respuestas de error siguen un formato estandarizado gracias a `@ControllerAdvice`:

```json
{
  "timestamp": "2026-07-24T10:15:30",
  "status": 404,
  "error": "Not Found",
  "message": "Bodega con id 5 no encontrada",
  "path": "/bodegas/5"
}
```

Códigos manejados: `400` (validación), `401` (no autenticado), `403` (sin permisos), `404` (no encontrado), `500` (error interno).

---

## 🧪 Pruebas

- Colección de Postman disponible en `/docs/postman_collection.json` *(agregar según se genere)*
- Capturas de pruebas y Swagger disponibles en `/docs/capturas/`
- Se recomienda probar el flujo completo: registro → login → crear bodega/producto → registrar movimiento → consultar auditoría → ver reporte.

---

## 👥 Roles y permisos

| Rol | Permisos |
|---|---|
| **ADMIN** | Acceso completo: CRUD de bodegas/productos, eliminar registros, ver auditorías y reportes |
| **EMPLEADO** | Consultar bodegas/productos, registrar movimientos, ver su propio historial |

---

## 📂 Estructura de entregables

```
├── src/                    → Código fuente del backend
├── frontend/                → Interfaz de prueba (HTML/CSS/JS)
├── schema.sql                → Script de creación de tablas
├── data.sql                  → Datos de prueba
├── docs/
│   ├── diagrama_clases.png
│   ├── arquitectura.md
│   └── capturas/
├── README.md                 → Este documento
```

---

## 👨‍💻 Autores

| Integrante | Módulos desarrollados |
|---|---|
| Persona A | Bodegas, Productos, Movimientos, Scripts SQL |
| Persona B | Seguridad/JWT, Auditoría, Reportes, Frontend, Documentación Swagger |

---

## 📄 Licencia

Proyecto desarrollado con fines académicos para LogiTrack S.A.
