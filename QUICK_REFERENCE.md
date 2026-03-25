# 🎯 InterviewMate - Quick Reference Card

**Genera esta tarjeta rápida imprimible para tener siempre a mano los endpoints.**

---

## 🔐 AUTENTICACIÓN

```
POST /auth/register
├─ username: alice (3-50 chars)
├─ email: alice@example.com
└─ password: SecurePass123! (8+ chars)

POST /auth/login
├─ username: alice
├─ password: SecurePass123!
└─ Response: token JWT

GET /auth/me
├─ Auth: Bearer {token}
└─ Response: User data
```

---

## 👥 USUARIOS

```
GET /usuarios?page=0&size=10&sort=createdAt,desc
├─ Auth: ✅ Required
└─ Response: Page<User>

GET /usuarios/{id}
├─ Auth: ✅ Required
└─ Response: User

POST /usuarios
├─ Auth: ✅ Required
├─ Body: username, email, password
└─ Response: User

PUT /usuarios/{id}
├─ Auth: ✅ Required
├─ Body: username, email, password
└─ Response: User

DELETE /usuarios/{id}
├─ Auth: ✅ Required
└─ Response: 204 No Content
```

---

## 🎯 PERFIL PROFESIONAL

```
GET /usuarios/perfil
├─ Auth: ✅ Required
└─ Response: ProfileResponse

PUT /usuarios/perfil
├─ Auth: ✅ Required
├─ Body: { "perfilProfesional": "..." }
└─ Response: ProfileResponse
├─ NOTA: Obligatorio antes de iniciar entrevista
```

---

## 🎤 ENTREVISTAS

```
POST /entrevistas/start
├─ Auth: ✅ Required
├─ Body: 
│  ├─ tipoEntrevista: TECNICA|COMPORTAMENTAL|MIXTA
│  └─ nivelDificultad: BASICO|INTERMEDIO|AVANZADO
├─ Response: Interview + Questions
└─ Nota: Requiere perfil completo

POST /entrevistas/respuestas
├─ Auth: ✅ Required
├─ Body:
│  ├─ idPregunta: 1
│  └─ respuesta: "Mi respuesta..."
└─ Response: Answer
```

---

## 📊 MATRIZ DE ENDPOINTS

| # | Endpoint | Método | Auth | Error | Descrip |
|---|----------|--------|------|-------|---------|
| 1 | /auth/register | POST | ❌ | 400 | Registrar |
| 2 | /auth/login | POST | ❌ | 401 | Login |
| 3 | /auth/me | GET | ✅ | 401 | Usuario actual |
| 4 | /usuarios | GET | ✅ | 401 | Listar |
| 5 | /usuarios/{id} | GET | ✅ | 401,404 | Obtener |
| 6 | /usuarios | POST | ✅ | 400,401 | Crear |
| 7 | /usuarios/{id} | PUT | ✅ | 400,401,404 | Actualizar |
| 8 | /usuarios/{id} | DELETE | ✅ | 401,404 | Eliminar |
| 9 | /usuarios/perfil | GET | ✅ | 401,404 | Get perfil |
| 10 | /usuarios/perfil | PUT | ✅ | 400,401 | Update perfil |
| 11 | /entrevistas/start | POST | ✅ | 400,401 | Iniciar |
| 12 | /entrevistas/respuestas | POST | ✅ | 400,401,404 | Responder |

---

## ⚡ QUICK COMMANDS

### Register
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","email":"alice@example.com","password":"Pass123!"}'
```

### Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"Pass123!"}'
# Copia el token de la respuesta
```

### Actualizar Perfil
```bash
curl -X PUT http://localhost:8080/usuarios/perfil \
  -H "Authorization: Bearer {TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{"perfilProfesional":"Ingeniero con 5 años"}'
```

### Iniciar Entrevista
```bash
curl -X POST http://localhost:8080/entrevistas/start \
  -H "Authorization: Bearer {TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{"tipoEntrevista":"TECNICA","nivelDificultad":"INTERMEDIO"}'
```

### Responder Pregunta
```bash
curl -X POST http://localhost:8080/entrevistas/respuestas \
  -H "Authorization: Bearer {TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{"idPregunta":1,"respuesta":"Mi respuesta..."}'
```

---

## 🔑 VARIABLES POSTMAN

```
{{baseUrl}}           = http://localhost:8080
{{token}}             = (from POST /auth/login)
{{userId}}            = 1
{{username}}          = alice
{{password}}          = SecurePass123!
{{email}}             = alice@example.com
{{interviewId}}       = (from POST /entrevistas/start)
{{questionId}}        = 1
{{tipoEntrevista}}    = TECNICA
{{nivelDificultad}}   = INTERMEDIO
```

---

## ✅ CÓDIGOS DE ERROR

| Código | Razón | Solución |
|--------|-------|----------|
| 400 | Bad Request (validación) | Revisa los datos de entrada |
| 401 | Unauthorized (sin auth) | Incluye token en Authorization header |
| 404 | Not Found | Verifica el ID del recurso |
| 409 | Conflict (duplicado) | Username/Email ya existe |
| 500 | Server Error | Contacta al admin |

---

## 📋 PASOS PARA INICIAR ENTREVISTA

1. **Registrarse:**
   ```
   POST /auth/register
   username, email, password
   ```

2. **Login:**
   ```
   POST /auth/login
   username, password → Obtener token
   ```

3. **Completar Perfil (IMPORTANTE):**
   ```
   PUT /usuarios/perfil
   perfilProfesional: "..."
   ```

4. **Iniciar Entrevista:**
   ```
   POST /entrevistas/start
   tipoEntrevista, nivelDificultad
   → Recibe lista de preguntas
   ```

5. **Responder Preguntas:**
   ```
   POST /entrevistas/respuestas
   idPregunta, respuesta
   ```

---

## 🔐 TOKEN JWT

**Estructura:**
```
header.payload.signature
```

**Validez:** 1 hora desde emisión  
**Incluye:** username, issued time, expiration  
**En header:** `Authorization: Bearer {token}`

---

## 📱 POSTMAN SETUP

1. Importa: `InterviewMate.postman_collection.json`
2. Importa: `InterviewMate.postman_environment.json`
3. Selecciona environment en dropdown (arriba)
4. ¡Ejecuta los requests!

---

## 🧪 FLUJO DE PRUEBA

```
REGISTER ─→ LOGIN ─→ GET ME
                      ↓
              UPDATE PROFILE ─→ GET PROFILE
                                 ↓
                      START INTERVIEW ─→ LIST USERS
                           ↓              ↓
                      SUBMIT ANSWER    GET USER/{ID}
                                       ↓
                                  CREATE/UPDATE/DELETE
```

---

## 📞 INFO

| Item | Valor |
|------|-------|
| Base URL | http://localhost:8080 |
| Auth Type | JWT Bearer |
| Token TTL | 1 hora |
| Database | PostgreSQL |
| Language | Java 21 |
| Framework | Spring Boot 4.0.3 |

---

**Imprime esta tarjeta y tenla a mano mientras trabajas con la API.**

**Última actualización:** Marzo 2026

