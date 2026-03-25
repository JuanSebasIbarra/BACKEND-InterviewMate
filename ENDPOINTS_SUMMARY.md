# 📋 Resumen de Endpoints - InterviewMate

**URL Base:** `http://localhost:8080`  
**Fecha:** Marzo 2026

---

## 🔐 Autenticación (SIN Token)

### 1. POST /auth/register
- **Descripción:** Registrar nuevo usuario
- **Requiere Auth:** ❌ No
- **Validaciones:**
  - `username`: 3-50 caracteres, único
  - `email`: Formato válido, único
  - `password`: Min 8 caracteres
- **Response:** `200 OK` + UserResponse

**Ejemplo:**
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","email":"alice@example.com","password":"SecurePass123!"}'
```

---

### 2. POST /auth/login
- **Descripción:** Iniciar sesión y obtener JWT
- **Requiere Auth:** ❌ No
- **Response:** `200 OK` + LoginResponse (con token JWT)

**Ejemplo:**
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"SecurePass123!"}'
```

---

### 3. GET /auth/me
- **Descripción:** Obtener datos del usuario autenticado
- **Requiere Auth:** ✅ Sí (Bearer Token)
- **Response:** `200 OK` + UserResponse

**Ejemplo:**
```bash
curl -X GET http://localhost:8080/auth/me \
  -H "Authorization: Bearer {TOKEN}"
```

---

## 👥 Usuarios (CON Token)

### 4. GET /usuarios
- **Descripción:** Listar todos los usuarios (paginado)
- **Requiere Auth:** ✅ Sí (Bearer Token)
- **Query Parameters:**
  - `page`: Número de página (default: 0)
  - `size`: Elementos por página (default: 10)
  - `sort`: Campo y dirección (default: id,asc)
- **Response:** `200 OK` + Page<UserResponse>

**Ejemplo:**
```bash
curl -X GET "http://localhost:8080/usuarios?page=0&size=10&sort=createdAt,desc" \
  -H "Authorization: Bearer {TOKEN}"
```

---

### 5. GET /usuarios/{id}
- **Descripción:** Obtener usuario por ID
- **Requiere Auth:** ✅ Sí (Bearer Token)
- **Path Parameters:**
  - `id`: ID del usuario (Long)
- **Response:** `200 OK` + UserResponse

**Ejemplo:**
```bash
curl -X GET http://localhost:8080/usuarios/1 \
  -H "Authorization: Bearer {TOKEN}"
```

---

### 6. POST /usuarios
- **Descripción:** Crear nuevo usuario (generalmente admin)
- **Requiere Auth:** ✅ Sí (Bearer Token)
- **Body:**
  - `username`: String (3-50 chars, único)
  - `email`: String (válido, único)
  - `password`: String (min 8 chars)
- **Response:** `200 OK` + UserResponse

**Ejemplo:**
```bash
curl -X POST http://localhost:8080/usuarios \
  -H "Authorization: Bearer {TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{"username":"bob","email":"bob@example.com","password":"SecurePass456!"}'
```

---

### 7. PUT /usuarios/{id}
- **Descripción:** Actualizar usuario existente
- **Requiere Auth:** ✅ Sí (Bearer Token)
- **Path Parameters:**
  - `id`: ID del usuario (Long)
- **Body:** UserRequest (username, email, password)
- **Response:** `200 OK` + UserResponse

**Ejemplo:**
```bash
curl -X PUT http://localhost:8080/usuarios/1 \
  -H "Authorization: Bearer {TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{"username":"alice_v2","email":"alice2@example.com","password":"NewPass789!"}'
```

---

### 8. DELETE /usuarios/{id}
- **Descripción:** Eliminar usuario
- **Requiere Auth:** ✅ Sí (Bearer Token)
- **Path Parameters:**
  - `id`: ID del usuario (Long)
- **Response:** `204 No Content`

**Ejemplo:**
```bash
curl -X DELETE http://localhost:8080/usuarios/1 \
  -H "Authorization: Bearer {TOKEN}"
```

---

## 🎯 Perfil Profesional (CON Token)

### 9. GET /usuarios/perfil
- **Descripción:** Obtener perfil profesional del usuario autenticado
- **Requiere Auth:** ✅ Sí (Bearer Token)
- **Response:** `200 OK` + ProfileResponse

**Ejemplo:**
```bash
curl -X GET http://localhost:8080/usuarios/perfil \
  -H "Authorization: Bearer {TOKEN}"
```

---

### 10. PUT /usuarios/perfil
- **Descripción:** Actualizar perfil profesional del usuario autenticado
- **Requiere Auth:** ✅ Sí (Bearer Token)
- **Body:**
  - `perfilProfesional`: String (texto del CV o descripción)
- **Response:** `200 OK` + ProfileResponse
- **Nota:** Este perfil es **obligatorio** para iniciar una entrevista

**Ejemplo:**
```bash
curl -X PUT http://localhost:8080/usuarios/perfil \
  -H "Authorization: Bearer {TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{"perfilProfesional":"Ingeniero de Software con 5 años de experiencia en Java y Spring Boot"}'
```

---

## 🎤 Entrevistas (CON Token)

### 11. POST /entrevistas/start
- **Descripción:** Iniciar nueva sesión de entrevista (genera preguntas con IA)
- **Requiere Auth:** ✅ Sí (Bearer Token)
- **Prerequisito:** Usuario debe tener perfil profesional completo
- **Body:**
  - `tipoEntrevista`: TECNICA | COMPORTAMENTAL | MIXTA
  - `nivelDificultad`: BASICO | INTERMEDIO | AVANZADO
- **Response:** `200 OK` + InterviewResponse (con lista de preguntas)

**Ejemplo:**
```bash
curl -X POST http://localhost:8080/entrevistas/start \
  -H "Authorization: Bearer {TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{"tipoEntrevista":"TECNICA","nivelDificultad":"INTERMEDIO"}'
```

---

### 12. POST /entrevistas/respuestas
- **Descripción:** Enviar respuesta a una pregunta de la entrevista
- **Requiere Auth:** ✅ Sí (Bearer Token)
- **Body:**
  - `idPregunta`: Long (ID de la pregunta)
  - `respuesta`: String (texto de la respuesta)
- **Response:** `200 OK` + AnswerResponse

**Ejemplo:**
```bash
curl -X POST http://localhost:8080/entrevistas/respuestas \
  -H "Authorization: Bearer {TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{"idPregunta":1,"respuesta":"Una clase abstracta permite compartir código..."}'
```

---

## 📊 Matriz de Endpoints

| # | Endpoint | Método | Auth | Descripción |
|---|----------|--------|------|-------------|
| 1 | `/auth/register` | POST | ❌ | Registrar usuario |
| 2 | `/auth/login` | POST | ❌ | Login y obtener token |
| 3 | `/auth/me` | GET | ✅ | Obtener usuario actual |
| 4 | `/usuarios` | GET | ✅ | Listar usuarios |
| 5 | `/usuarios/{id}` | GET | ✅ | Obtener usuario por ID |
| 6 | `/usuarios` | POST | ✅ | Crear usuario |
| 7 | `/usuarios/{id}` | PUT | ✅ | Actualizar usuario |
| 8 | `/usuarios/{id}` | DELETE | ✅ | Eliminar usuario |
| 9 | `/usuarios/perfil` | GET | ✅ | Obtener perfil profesional |
| 10 | `/usuarios/perfil` | PUT | ✅ | Actualizar perfil profesional |
| 11 | `/entrevistas/start` | POST | ✅ | Iniciar entrevista |
| 12 | `/entrevistas/respuestas` | POST | ✅ | Enviar respuesta |

---

## 🔑 Sistema de Autenticación

**Tipo:** JWT (JSON Web Token)  
**Algoritmo:** HMAC-SHA256  
**Expiración:** 1 hora (configurable)

### Cómo usar el token:

1. **Registrarse:** POST /auth/register
2. **Login:** POST /auth/login → Obtener `token`
3. **Usar token en requests:** `Authorization: Bearer {token}`

**Header:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGljZSIsImlhdCI6MTcxNjU0MzIwMCwiZXhwIjoxNzE2NTQ2ODAwfQ.signature
```

---

## ✅ Códigos de Respuesta

| Código | Significado | Caso |
|--------|-------------|------|
| 200 | OK | Request exitoso |
| 201 | Created | Recurso creado |
| 204 | No Content | Eliminación exitosa |
| 400 | Bad Request | Validación fallida |
| 401 | Unauthorized | Token inválido/expirado |
| 404 | Not Found | Recurso no encontrado |
| 409 | Conflict | Recurso duplicado |
| 500 | Server Error | Error interno |

---

## 🛠️ Variables Postman Recomendadas

```
{{baseUrl}}      = http://localhost:8080
{{token}}        = (obtener de POST /auth/login)
{{userId}}       = 1
{{interviewId}}  = (obtener de POST /entrevistas/start)
{{questionId}}   = 1
```

---

## 📱 Estructura de Request/Response

### Request Típico (Autenticado):
```http
POST /entrevistas/start
Host: localhost:8080
Authorization: Bearer {TOKEN}
Content-Type: application/json

{
  "tipoEntrevista": "TECNICA",
  "nivelDificultad": "INTERMEDIO"
}
```

### Response Típico:
```json
{
  "id": 1,
  "title": "Entrevista Técnica - Nivel Intermedio",
  "status": "EN_PROGRESO",
  "questions": [...],
  "createdAt": "2026-03-25T14:30:00Z"
}
```

---

## ⚡ Quick Start

**1. Registrarse:**
```bash
POST http://localhost:8080/auth/register
{"username":"alice","email":"alice@example.com","password":"SecurePass123!"}
```

**2. Login:**
```bash
POST http://localhost:8080/auth/login
{"username":"alice","password":"SecurePass123!"}
# Copia el token de la respuesta
```

**3. Actualizar perfil:**
```bash
PUT http://localhost:8080/usuarios/perfil
Authorization: Bearer {TOKEN}
{"perfilProfesional":"Ingeniero Software con 5 años en Java"}
```

**4. Iniciar entrevista:**
```bash
POST http://localhost:8080/entrevistas/start
Authorization: Bearer {TOKEN}
{"tipoEntrevista":"TECNICA","nivelDificultad":"INTERMEDIO"}
```

**5. Responder pregunta:**
```bash
POST http://localhost:8080/entrevistas/respuestas
Authorization: Bearer {TOKEN}
{"idPregunta":1,"respuesta":"Mi respuesta aquí..."}
```

---

**Versión:** 1.0  
**Última actualización:** Marzo 2026  
**Autor:** InterviewMate Development Team

