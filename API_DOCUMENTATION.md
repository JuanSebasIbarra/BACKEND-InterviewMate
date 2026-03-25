# 📚 API Documentation - InterviewMate

**Proyecto:** InterviewMate  
**Stack:** Java 21 · Spring Boot 4.0.3 · Spring Security · JWT (JJWT 0.11.5)  
**URL Base:** `http://localhost:8080`  
**Última actualización:** Marzo 2026

---

## 📋 Tabla de Contenidos

1. [Configuración General](#configuración-general)
2. [Autenticación](#autenticación)
3. [Endpoints - Autenticación (Sin Auth)](#endpoints---autenticación-sin-auth)
4. [Endpoints - Usuarios (Con Auth)](#endpoints---usuarios-con-auth)
5. [Endpoints - Entrevistas (Con Auth)](#endpoints---entrevistas-con-auth)
6. [Códigos de Error](#códigos-de-error)
7. [Ejemplos de Requests](#ejemplos-de-requests)

---

## Configuración General

### Headers Requeridos

**Para requests autenticados:**

```
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

### URL Base

```
http://localhost:8080
```

### Variables de Configuración

```properties
# application.properties
server.port=8080
app.jwt.secret=your-super-secret-key-minimum-32-characters-long
app.jwt.expiration-ms=3600000  # 1 hora en milisegundos
```

---

## Autenticación

### 🔐 Sistema JWT (JSON Web Token)

**Flujo:**

1. Usuario se registra o inicia sesión
2. Backend genera un token JWT firmado con HMAC-SHA256
3. Token expira en **1 hora** (configurable)
4. Cliente incluye token en el header `Authorization: Bearer {token}` para requests posteriores
5. Backend valida el token en cada request protegido

**Token estructura:**

```
Header.Payload.Signature
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGljZSIsImlhdCI6MTcxNjU0MzIwMCwiZXhwIjoxNzE2NTQ2ODAwfQ.signature
```

**Información en el payload:**
- `sub`: Username del usuario
- `iat`: Issued At (timestamp de emisión)
- `exp`: Expiration (timestamp de expiración)

### 🛡️ Endpoints Protegidos

**Requieren token JWT:**
- ✅ `GET /usuarios` - Listar usuarios
- ✅ `GET /usuarios/{id}` - Obtener usuario por ID
- ✅ `POST /usuarios` - Crear usuario (admin)
- ✅ `PUT /usuarios/{id}` - Actualizar usuario
- ✅ `DELETE /usuarios/{id}` - Eliminar usuario
- ✅ `GET /usuarios/perfil` - Obtener perfil profesional
- ✅ `PUT /usuarios/perfil` - Actualizar perfil profesional
- ✅ `GET /auth/me` - Obtener datos del usuario autenticado
- ✅ `POST /entrevistas/start` - Iniciar entrevista
- ✅ `POST /entrevistas/respuestas` - Enviar respuesta

**No requieren token:**
- 🔓 `POST /auth/register` - Registrar nuevo usuario
- 🔓 `POST /auth/login` - Iniciar sesión

---

## Endpoints - Autenticación (Sin Auth)

### 1. POST /auth/register

**Registrar un nuevo usuario**

```http
POST /auth/register
Content-Type: application/json

{
  "username": "alice",
  "email": "alice@example.com",
  "password": "MySecurePass123!"
}
```

**Validaciones:**
- `username`: Requerido, 3-50 caracteres, único
- `email`: Requerido, formato válido, único
- `password`: Requerido, mínimo 8 caracteres

**Response 200 OK:**

```json
{
  "id": 1,
  "username": "alice",
  "email": "alice@example.com",
  "roles": ["ROLE_USER"],
  "createdAt": "2026-03-25T14:30:00Z"
}
```

**Response 400 Bad Request:**

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Username 'alice' is already taken",
  "timestamp": "2026-03-25T14:30:00Z"
}
```

---

### 2. POST /auth/login

**Iniciar sesión y obtener JWT**

```http
POST /auth/login
Content-Type: application/json

{
  "username": "alice",
  "password": "MySecurePass123!"
}
```

**Validaciones:**
- `username`: Requerido
- `password`: Requerido

**Response 200 OK:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGljZSIsImlhdCI6MTcxNjU0MzIwMCwiZXhwIjoxNzE2NTQ2ODAwfQ.signature",
  "tokenType": "Bearer",
  "expiresAt": "2026-03-25T15:30:00Z",
  "username": "alice"
}
```

**Response 401 Unauthorized:**

```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid username or password",
  "timestamp": "2026-03-25T14:30:00Z"
}
```

> **Nota de seguridad:** Los mensajes de error nunca revelan si el usuario existe o si la contraseña es incorrecta. Ambos casos retornan el mismo mensaje para prevenir enumeración de usuarios.

---

### 3. GET /auth/me

**Obtener datos del usuario autenticado**

```http
GET /auth/me
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Response 200 OK:**

```json
{
  "id": 1,
  "username": "alice",
  "email": "alice@example.com",
  "roles": ["ROLE_USER"],
  "createdAt": "2026-03-25T14:00:00Z"
}
```

**Response 401 Unauthorized:**

```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid or missing token",
  "timestamp": "2026-03-25T14:30:00Z"
}
```

---

## Endpoints - Usuarios (Con Auth) 🔒

### 1. GET /usuarios

**Listar todos los usuarios (paginado)**

```http
GET /usuarios?page=0&size=10&sort=createdAt,desc
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Query Parameters:**
- `page`: Número de página (default: 0)
- `size`: Elementos por página (default: 10)
- `sort`: Campo y dirección de ordenamiento (default: id,asc)

**Response 200 OK:**

```json
{
  "content": [
    {
      "id": 1,
      "username": "alice",
      "email": "alice@example.com",
      "roles": ["ROLE_USER"],
      "createdAt": "2026-03-25T14:00:00Z"
    },
    {
      "id": 2,
      "username": "bob",
      "email": "bob@example.com",
      "roles": ["ROLE_USER"],
      "createdAt": "2026-03-24T10:00:00Z"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
    },
    "offset": 0,
    "unpaged": false
  },
  "totalPages": 1,
  "totalElements": 2,
  "last": true,
  "size": 10,
  "number": 0,
  "sort": {
    "empty": false,
    "sorted": true,
    "unsorted": false
  },
  "numberOfElements": 2,
  "first": true,
  "empty": false
}
```

---

### 2. GET /usuarios/{id}

**Obtener usuario por ID**

```http
GET /usuarios/1
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Response 200 OK:**

```json
{
  "id": 1,
  "username": "alice",
  "email": "alice@example.com",
  "roles": ["ROLE_USER"],
  "createdAt": "2026-03-25T14:00:00Z"
}
```

**Response 404 Not Found:**

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "User not found with id: 999",
  "timestamp": "2026-03-25T14:30:00Z"
}
```

---

### 3. POST /usuarios

**Crear nuevo usuario (admin)**

```http
POST /usuarios
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json

{
  "username": "charlie",
  "email": "charlie@example.com",
  "password": "SecurePass456!"
}
```

**Response 200 OK:**

```json
{
  "id": 3,
  "username": "charlie",
  "email": "charlie@example.com",
  "roles": ["ROLE_USER"],
  "createdAt": "2026-03-25T14:30:00Z"
}
```

---

### 4. PUT /usuarios/{id}

**Actualizar usuario**

```http
PUT /usuarios/1
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json

{
  "username": "alice_updated",
  "email": "alice_new@example.com",
  "password": "NewPassword789!"
}
```

**Response 200 OK:**

```json
{
  "id": 1,
  "username": "alice_updated",
  "email": "alice_new@example.com",
  "roles": ["ROLE_USER"],
  "createdAt": "2026-03-25T14:00:00Z"
}
```

---

### 5. DELETE /usuarios/{id}

**Eliminar usuario**

```http
DELETE /usuarios/1
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Response 204 No Content:**

```
(vacío)
```

---

### 6. GET /usuarios/perfil

**Obtener perfil profesional del usuario autenticado**

```http
GET /usuarios/perfil
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Response 200 OK:**

```json
{
  "userId": 1,
  "username": "alice",
  "perfilProfesional": "Ingeniero de Software con 5 años de experiencia en Java y Spring Boot. Especialista en arquitectura de microservicios.",
  "updatedAt": "2026-03-25T14:00:00Z"
}
```

**Response 404 Not Found:**

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Profile not found for user: unknown",
  "timestamp": "2026-03-25T14:30:00Z"
}
```

---

### 7. PUT /usuarios/perfil

**Actualizar perfil profesional del usuario autenticado**

```http
PUT /usuarios/perfil
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json

{
  "perfilProfesional": "Desarrollador Full Stack con experiencia en Java, React y Cloud Computing. Actualmente enfocado en arquitectura de software."
}
```

**Response 200 OK:**

```json
{
  "userId": 1,
  "username": "alice",
  "perfilProfesional": "Desarrollador Full Stack con experiencia en Java, React y Cloud Computing. Actualmente enfocado en arquitectura de software.",
  "updatedAt": "2026-03-25T14:35:00Z"
}
```

---

## Endpoints - Entrevistas (Con Auth) 🔒

### 1. POST /entrevistas/start

**Iniciar una nueva sesión de entrevista**

Genera preguntas automáticamente usando IA basadas en el perfil profesional del usuario.

```http
POST /entrevistas/start
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json

{
  "tipoEntrevista": "TECNICA",
  "nivelDificultad": "INTERMEDIO"
}
```

**Body Parameters:**

| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| `tipoEntrevista` | String | Tipo de entrevista (TECNICA, COMPORTAMENTAL, MIXTA) |
| `nivelDificultad` | String | Nivel de dificultad (BASICO, INTERMEDIO, AVANZADO) |

**Response 200 OK:**

```json
{
  "id": 1,
  "title": "Entrevista Técnica - Nivel Intermedio",
  "description": "Sesión de entrevista generada el 2026-03-25 para evaluar habilidades técnicas",
  "status": "EN_PROGRESO",
  "questions": [
    {
      "id": 1,
      "content": "¿Cuál es la diferencia entre una clase abstracta e una interfaz en Java?",
      "tipo": "TECNICA",
      "nivelDificultad": "INTERMEDIO",
      "orden": 1,
      "createdAt": "2026-03-25T14:30:00Z"
    },
    {
      "id": 2,
      "content": "Explica qué es inyección de dependencias y por qué es importante",
      "tipo": "TECNICA",
      "nivelDificultad": "INTERMEDIO",
      "orden": 2,
      "createdAt": "2026-03-25T14:30:00Z"
    }
  ],
  "createdAt": "2026-03-25T14:30:00Z",
  "updatedAt": "2026-03-25T14:30:00Z"
}
```

**Response 400 Bad Request:**

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "tipoEntrevista es obligatorio",
  "timestamp": "2026-03-25T14:30:00Z"
}
```

**Response 400 Bad Request (Sin perfil):**

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Usuario debe completar su perfil profesional antes de iniciar una entrevista",
  "timestamp": "2026-03-25T14:30:00Z"
}
```

---

### 2. POST /entrevistas/respuestas

**Enviar respuesta a una pregunta de la entrevista**

```http
POST /entrevistas/respuestas
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json

{
  "idPregunta": 1,
  "respuesta": "Una clase abstracta se usa para compartir código entre clases relacionadas, mientras que una interfaz define un contrato que las clases pueden implementar..."
}
```

**Body Parameters:**

| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| `idPregunta` | Long | ID de la pregunta |
| `respuesta` | String | Texto de la respuesta del usuario |

**Response 200 OK:**

```json
{
  "id": 1,
  "idPregunta": 1,
  "idEntrevista": 1,
  "respuesta": "Una clase abstracta se usa para compartir código entre clases relacionadas, mientras que una interfaz define un contrato que las clases pueden implementar...",
  "tiempoRespuesta": 45,
  "createdAt": "2026-03-25T14:35:00Z"
}
```

**Response 404 Not Found:**

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Question not found with id: 999",
  "timestamp": "2026-03-25T14:30:00Z"
}
```

---

## Códigos de Error

| Código | Descripción | Causa |
|--------|-------------|-------|
| **200** | OK | Request exitoso |
| **201** | Created | Recurso creado exitosamente |
| **204** | No Content | Request exitoso, sin contenido en respuesta |
| **400** | Bad Request | Validación fallida o datos inválidos |
| **401** | Unauthorized | Token faltante, inválido o expirado |
| **403** | Forbidden | Usuario sin permisos para acceder al recurso |
| **404** | Not Found | Recurso no encontrado |
| **409** | Conflict | Recurso ya existe (ej: username duplicado) |
| **500** | Internal Server Error | Error inesperado en el servidor |

---

## Ejemplos de Requests

### Ejemplo 1: Flujo Completo de Registro y Entrevista

#### Paso 1: Registrar usuario

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice",
    "email": "alice@example.com",
    "password": "SecurePass123!"
  }'
```

**Response:**

```json
{
  "id": 1,
  "username": "alice",
  "email": "alice@example.com",
  "roles": ["ROLE_USER"],
  "createdAt": "2026-03-25T14:00:00Z"
}
```

#### Paso 2: Login y obtener token

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice",
    "password": "SecurePass123!"
  }'
```

**Response:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGljZSIsImlhdCI6MTcxNjU0MzIwMCwiZXhwIjoxNzE2NTQ2ODAwfQ.signature",
  "tokenType": "Bearer",
  "expiresAt": "2026-03-25T15:30:00Z",
  "username": "alice"
}
```

#### Paso 3: Actualizar perfil profesional

```bash
TOKEN="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGljZSIsImlhdCI6MTcxNjU0MzIwMCwiZXhwIjoxNzE2NTQ2ODAwfQ.signature"

curl -X PUT http://localhost:8080/usuarios/perfil \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "perfilProfesional": "Ingeniero de Software con 5 años de experiencia en Java y Spring Boot"
  }'
```

#### Paso 4: Iniciar entrevista

```bash
curl -X POST http://localhost:8080/entrevistas/start \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "tipoEntrevista": "TECNICA",
    "nivelDificultad": "INTERMEDIO"
  }'
```

#### Paso 5: Enviar respuesta a pregunta

```bash
curl -X POST http://localhost:8080/entrevistas/respuestas \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "idPregunta": 1,
    "respuesta": "Una clase abstracta permite compartir código, mientras que una interfaz define un contrato..."
  }'
```

---

## 🔐 Matriz de Autenticación

| Endpoint | Método | Auth | Descripción |
|----------|--------|------|-------------|
| `/auth/register` | POST | ❌ | Registro público |
| `/auth/login` | POST | ❌ | Login público |
| `/auth/me` | GET | ✅ | Obtener usuario actual |
| `/usuarios` | GET | ✅ | Listar usuarios |
| `/usuarios/{id}` | GET | ✅ | Obtener usuario por ID |
| `/usuarios` | POST | ✅ | Crear usuario (admin) |
| `/usuarios/{id}` | PUT | ✅ | Actualizar usuario |
| `/usuarios/{id}` | DELETE | ✅ | Eliminar usuario |
| `/usuarios/perfil` | GET | ✅ | Obtener perfil profesional |
| `/usuarios/perfil` | PUT | ✅ | Actualizar perfil profesional |
| `/entrevistas/start` | POST | ✅ | Iniciar entrevista |
| `/entrevistas/respuestas` | POST | ✅ | Enviar respuesta |

---

## 📝 Variables de Entorno Recomendadas

```bash
# .env
DATABASE_URL=jdbc:postgresql://localhost:5432/interviewmate
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=password
JWT_SECRET=your-super-secret-key-minimum-32-characters-long
JWT_EXPIRATION_MS=3600000
SPRING_PROFILES_ACTIVE=dev
```

---

**Última actualización:** Marzo 2026  
**Versión API:** 1.0  
**Autor:** InterviewMate Development Team

