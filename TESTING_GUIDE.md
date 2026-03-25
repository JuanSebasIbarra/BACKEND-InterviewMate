# 🧪 Guía de Pruebas - InterviewMate API

**Documento para ejecutar pruebas manuales de todos los endpoints**

---

## 📌 Configuración Inicial

### 1. Base URL
```
http://localhost:8080
```

### 2. Variables Globales en Postman

Antes de comenzar, define estas variables en **Postman**:

**Environment Variables:**
```
baseUrl = http://localhost:8080
token = (se actualiza después de login)
userId = 1
interviewId = (se actualiza después de iniciar entrevista)
questionId = 1
```

### 3. Orden Recomendado de Pruebas

```
1. Register → 2. Login → 3. Get Current User
   ↓
4. Update Profile → 5. Get Profile
   ↓
6. Start Interview → 7. Submit Answer
   ↓
8. List Users → 9. Get User by ID
   ↓
10. Create User → 11. Update User → 12. Delete User
```

---

## 🔐 FASE 1: Autenticación

### Test 1.1: Registrar Nuevo Usuario

**Endpoint:**
```
POST http://localhost:8080/auth/register
```

**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
  "username": "alice",
  "email": "alice@example.com",
  "password": "SecurePass123!"
}
```

**Expected Response (200 OK):**
```json
{
  "id": 1,
  "username": "alice",
  "email": "alice@example.com",
  "roles": ["ROLE_USER"],
  "createdAt": "2026-03-25T14:00:00Z"
}
```

**cURL:**
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice",
    "email": "alice@example.com",
    "password": "SecurePass123!"
  }'
```

---

### Test 1.2: Login y Obtener Token

**Endpoint:**
```
POST http://localhost:8080/auth/login
```

**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
  "username": "alice",
  "password": "SecurePass123!"
}
```

**Expected Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGljZSIsImlhdCI6MTcxNjU0MzIwMCwiZXhwIjoxNzE2NTQ2ODAwfQ.signature",
  "tokenType": "Bearer",
  "expiresAt": "2026-03-25T15:30:00Z",
  "username": "alice"
}
```

**Nota:** Copiar el valor de `token` para usarlo en los siguientes requests.

**cURL:**
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice",
    "password": "SecurePass123!"
  }'
```

---

### Test 1.3: Obtener Datos del Usuario Autenticado

**Endpoint:**
```
GET http://localhost:8080/auth/me
```

**Headers:**
```
Authorization: Bearer {TOKEN}
```

**Expected Response (200 OK):**
```json
{
  "id": 1,
  "username": "alice",
  "email": "alice@example.com",
  "roles": ["ROLE_USER"],
  "createdAt": "2026-03-25T14:00:00Z"
}
```

**cURL:**
```bash
TOKEN="eyJhbGciOiJIUzI1NiJ9..."

curl -X GET http://localhost:8080/auth/me \
  -H "Authorization: Bearer $TOKEN"
```

---

## 👥 FASE 2: Gestión de Usuarios

### Test 2.1: Listar Usuarios (Paginado)

**Endpoint:**
```
GET http://localhost:8080/usuarios?page=0&size=10&sort=createdAt,desc
```

**Headers:**
```
Authorization: Bearer {TOKEN}
```

**Query Parameters:**
```
page=0       (página 0)
size=10      (10 elementos por página)
sort=createdAt,desc  (ordenar por fecha de creación descendente)
```

**Expected Response (200 OK):**
```json
{
  "content": [
    {
      "id": 1,
      "username": "alice",
      "email": "alice@example.com",
      "roles": ["ROLE_USER"],
      "createdAt": "2026-03-25T14:00:00Z"
    }
  ],
  "pageable": {},
  "totalPages": 1,
  "totalElements": 1,
  "last": true,
  "size": 10,
  "number": 0,
  "numberOfElements": 1,
  "first": true,
  "empty": false
}
```

**cURL:**
```bash
TOKEN="eyJhbGciOiJIUzI1NiJ9..."

curl -X GET "http://localhost:8080/usuarios?page=0&size=10&sort=createdAt,desc" \
  -H "Authorization: Bearer $TOKEN"
```

---

### Test 2.2: Obtener Usuario por ID

**Endpoint:**
```
GET http://localhost:8080/usuarios/1
```

**Headers:**
```
Authorization: Bearer {TOKEN}
```

**Expected Response (200 OK):**
```json
{
  "id": 1,
  "username": "alice",
  "email": "alice@example.com",
  "roles": ["ROLE_USER"],
  "createdAt": "2026-03-25T14:00:00Z"
}
```

**cURL:**
```bash
TOKEN="eyJhbGciOiJIUzI1NiJ9..."

curl -X GET http://localhost:8080/usuarios/1 \
  -H "Authorization: Bearer $TOKEN"
```

---

### Test 2.3: Crear Usuario

**Endpoint:**
```
POST http://localhost:8080/usuarios
```

**Headers:**
```
Authorization: Bearer {TOKEN}
Content-Type: application/json
```

**Body:**
```json
{
  "username": "bob",
  "email": "bob@example.com",
  "password": "SecurePass456!"
}
```

**Expected Response (200 OK):**
```json
{
  "id": 2,
  "username": "bob",
  "email": "bob@example.com",
  "roles": ["ROLE_USER"],
  "createdAt": "2026-03-25T15:00:00Z"
}
```

**cURL:**
```bash
TOKEN="eyJhbGciOiJIUzI1NiJ9..."

curl -X POST http://localhost:8080/usuarios \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "bob",
    "email": "bob@example.com",
    "password": "SecurePass456!"
  }'
```

---

### Test 2.4: Actualizar Usuario

**Endpoint:**
```
PUT http://localhost:8080/usuarios/1
```

**Headers:**
```
Authorization: Bearer {TOKEN}
Content-Type: application/json
```

**Body:**
```json
{
  "username": "alice_updated",
  "email": "alice_new@example.com",
  "password": "NewPassword789!"
}
```

**Expected Response (200 OK):**
```json
{
  "id": 1,
  "username": "alice_updated",
  "email": "alice_new@example.com",
  "roles": ["ROLE_USER"],
  "createdAt": "2026-03-25T14:00:00Z"
}
```

**cURL:**
```bash
TOKEN="eyJhbGciOiJIUzI1NiJ9..."

curl -X PUT http://localhost:8080/usuarios/1 \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice_updated",
    "email": "alice_new@example.com",
    "password": "NewPassword789!"
  }'
```

---

### Test 2.5: Eliminar Usuario

**Endpoint:**
```
DELETE http://localhost:8080/usuarios/1
```

**Headers:**
```
Authorization: Bearer {TOKEN}
```

**Expected Response (204 No Content):**
```
(vacío)
```

**cURL:**
```bash
TOKEN="eyJhbGciOiJIUzI1NiJ9..."

curl -X DELETE http://localhost:8080/usuarios/1 \
  -H "Authorization: Bearer $TOKEN"
```

---

## 🎯 FASE 3: Perfil Profesional

### Test 3.1: Obtener Perfil Profesional

**Endpoint:**
```
GET http://localhost:8080/usuarios/perfil
```

**Headers:**
```
Authorization: Bearer {TOKEN}
```

**Expected Response (200 OK):**
```json
{
  "userId": 1,
  "username": "alice",
  "perfilProfesional": "Ingeniero de Software con 5 años de experiencia en Java y Spring Boot",
  "updatedAt": "2026-03-25T14:00:00Z"
}
```

**cURL:**
```bash
TOKEN="eyJhbGciOiJIUzI1NiJ9..."

curl -X GET http://localhost:8080/usuarios/perfil \
  -H "Authorization: Bearer $TOKEN"
```

---

### Test 3.2: Actualizar Perfil Profesional

**Endpoint:**
```
PUT http://localhost:8080/usuarios/perfil
```

**Headers:**
```
Authorization: Bearer {TOKEN}
Content-Type: application/json
```

**Body:**
```json
{
  "perfilProfesional": "Desarrollador Full Stack con 5 años de experiencia en Java, React y Cloud Computing. Especialista en arquitectura de microservicios y DevOps."
}
```

**Expected Response (200 OK):**
```json
{
  "userId": 1,
  "username": "alice",
  "perfilProfesional": "Desarrollador Full Stack con 5 años de experiencia en Java, React y Cloud Computing. Especialista en arquitectura de microservicios y DevOps.",
  "updatedAt": "2026-03-25T14:35:00Z"
}
```

**Nota:** El perfil profesional es **obligatorio** antes de iniciar una entrevista.

**cURL:**
```bash
TOKEN="eyJhbGciOiJIUzI1NiJ9..."

curl -X PUT http://localhost:8080/usuarios/perfil \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "perfilProfesional": "Desarrollador Full Stack con 5 años de experiencia en Java, React y Cloud Computing. Especialista en arquitectura de microservicios y DevOps."
  }'
```

---

## 🎤 FASE 4: Entrevistas

### Test 4.1: Iniciar Entrevista

**Endpoint:**
```
POST http://localhost:8080/entrevistas/start
```

**Headers:**
```
Authorization: Bearer {TOKEN}
Content-Type: application/json
```

**Body:**
```json
{
  "tipoEntrevista": "TECNICA",
  "nivelDificultad": "INTERMEDIO"
}
```

**Parámetros permitidos:**
```
tipoEntrevista: TECNICA | COMPORTAMENTAL | MIXTA
nivelDificultad: BASICO | INTERMEDIO | AVANZADO
```

**Expected Response (200 OK):**
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

**cURL:**
```bash
TOKEN="eyJhbGciOiJIUzI1NiJ9..."

curl -X POST http://localhost:8080/entrevistas/start \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "tipoEntrevista": "TECNICA",
    "nivelDificultad": "INTERMEDIO"
  }'
```

---

### Test 4.2: Enviar Respuesta a Pregunta

**Endpoint:**
```
POST http://localhost:8080/entrevistas/respuestas
```

**Headers:**
```
Authorization: Bearer {TOKEN}
Content-Type: application/json
```

**Body:**
```json
{
  "idPregunta": 1,
  "respuesta": "Una clase abstracta se usa para compartir código común entre clases relacionadas mediante herencia, mientras que una interfaz define un contrato que las clases deben implementar. Las clases pueden heredar de una sola clase abstracta pero pueden implementar múltiples interfaces."
}
```

**Expected Response (200 OK):**
```json
{
  "id": 1,
  "idPregunta": 1,
  "idEntrevista": 1,
  "respuesta": "Una clase abstracta se usa para compartir código común entre clases relacionadas mediante herencia, mientras que una interfaz define un contrato que las clases deben implementar. Las clases pueden heredar de una sola clase abstracta pero pueden implementar múltiples interfaces.",
  "tiempoRespuesta": 45,
  "createdAt": "2026-03-25T14:35:00Z"
}
```

**cURL:**
```bash
TOKEN="eyJhbGciOiJIUzI1NiJ9..."

curl -X POST http://localhost:8080/entrevistas/respuestas \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "idPregunta": 1,
    "respuesta": "Una clase abstracta se usa para compartir código común entre clases relacionadas mediante herencia..."
  }'
```

---

## ⚠️ Pruebas de Error

### Error 1: Token Inválido

**Endpoint:**
```
GET http://localhost:8080/usuarios
```

**Headers:**
```
Authorization: Bearer invalid_token_123
```

**Expected Response (401 Unauthorized):**
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid or expired token",
  "timestamp": "2026-03-25T14:30:00Z"
}
```

---

### Error 2: Usuario No Encontrado

**Endpoint:**
```
GET http://localhost:8080/usuarios/999
```

**Headers:**
```
Authorization: Bearer {TOKEN}
```

**Expected Response (404 Not Found):**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "User not found with id: 999",
  "timestamp": "2026-03-25T14:30:00Z"
}
```

---

### Error 3: Usuario Duplicado

**Endpoint:**
```
POST http://localhost:8080/auth/register
```

**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
  "username": "alice",
  "email": "alice@example.com",
  "password": "SecurePass123!"
}
```

**Expected Response (400 Bad Request):**
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Username 'alice' is already taken",
  "timestamp": "2026-03-25T14:30:00Z"
}
```

---

### Error 4: Datos Inválidos (Validación)

**Endpoint:**
```
POST http://localhost:8080/auth/register
```

**Headers:**
```
Content-Type: application/json
```

**Body (email inválido):**
```json
{
  "username": "bob",
  "email": "invalid_email",
  "password": "pass123"
}
```

**Expected Response (400 Bad Request):**
```json
{
  "status": 400,
  "error": "Validation failed",
  "fieldErrors": {
    "email": "Email must be a valid format",
    "password": "Password must be between 8 and 100 characters"
  },
  "timestamp": "2026-03-25T14:30:00Z"
}
```

---

### Error 5: Perfil Profesional No Completado

**Endpoint:**
```
POST http://localhost:8080/entrevistas/start
```

**Headers:**
```
Authorization: Bearer {TOKEN}
Content-Type: application/json
```

**Body (usuario sin perfil):**
```json
{
  "tipoEntrevista": "TECNICA",
  "nivelDificultad": "INTERMEDIO"
}
```

**Expected Response (400 Bad Request):**
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Usuario debe completar su perfil profesional antes de iniciar una entrevista",
  "timestamp": "2026-03-25T14:30:00Z"
}
```

---

## 📊 Checklist de Pruebas

- [ ] Test 1.1: Registrar usuario
- [ ] Test 1.2: Login y obtener token
- [ ] Test 1.3: Obtener datos del usuario autenticado
- [ ] Test 2.1: Listar usuarios
- [ ] Test 2.2: Obtener usuario por ID
- [ ] Test 2.3: Crear usuario
- [ ] Test 2.4: Actualizar usuario
- [ ] Test 2.5: Eliminar usuario
- [ ] Test 3.1: Obtener perfil profesional
- [ ] Test 3.2: Actualizar perfil profesional
- [ ] Test 4.1: Iniciar entrevista
- [ ] Test 4.2: Enviar respuesta
- [ ] Error 1: Token inválido
- [ ] Error 2: Usuario no encontrado
- [ ] Error 3: Usuario duplicado
- [ ] Error 4: Datos inválidos
- [ ] Error 5: Perfil no completado

---

## 🚀 Cómo Importar la Colección en Postman

1. Abre **Postman**
2. Click en **File** → **Import**
3. Selecciona el archivo `InterviewMate.postman_collection.json`
4. Click en **Import**
5. Configura la variable `baseUrl` a `http://localhost:8080`
6. ¡Listo! Ahora puedes ejecutar los requests

---

## 💡 Tips

- **Guarda el token:** Después de login, copia el token en la variable `{{token}}`
- **Usa variables:** En Postman, crea variables para `baseUrl` y `token`
- **Valida JSON:** Antes de enviar, asegúrate que el JSON sea válido
- **Lee los errores:** Presta atención a los mensajes de error para debuggear
- **Prueba en orden:** Sigue el orden recomendado para evitar errores

---

**Última actualización:** Marzo 2026  
**Versión:** 1.0  
**Autor:** InterviewMate Development Team

