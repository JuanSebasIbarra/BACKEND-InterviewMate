# 📚 Documentación de APIs - InterviewMate

Este directorio contiene la documentación completa de todos los endpoints de la API de InterviewMate.

---

## 📄 Archivos Generados

### 1. **API_DOCUMENTATION.md** 📖
Documentación **completa y detallada** de todos los endpoints.

**Contenido:**
- ✅ Configuración general
- ✅ Sistema de autenticación JWT
- ✅ Todos los endpoints organizados por módulo
- ✅ Ejemplos de request/response
- ✅ Códigos de error
- ✅ Pruebas de ejemplo
- ✅ Matriz de autenticación

**Cuándo usar:** Referencia técnica detallada para desarrolladores.

---

### 2. **ENDPOINTS_SUMMARY.md** 📋
**Resumen ejecutivo** de todos los endpoints en formato compacto.

**Contenido:**
- ✅ Lista de todos los endpoints
- ✅ Ejemplos cURL para cada uno
- ✅ Tabla de endpoints
- ✅ Sistema de autenticación
- ✅ Quick start en 5 pasos

**Cuándo usar:** Vista rápida de todos los endpoints disponibles.

---

### 3. **TESTING_GUIDE.md** 🧪
**Guía paso a paso** para ejecutar pruebas manuales.

**Contenido:**
- ✅ Orden recomendado de pruebas
- ✅ Configuración inicial
- ✅ Tests organizados por fases
- ✅ Respuestas esperadas
- ✅ Pruebas de error
- ✅ Checklist de pruebas

**Cuándo usar:** Para ejecutar pruebas manuales de todos los endpoints.

---

### 4. **InterviewMate.postman_collection.json** 🔗
**Colección de Postman** con todos los endpoints pre-configurados.

**Qué incluye:**
- ✅ 12 endpoints organizados en 4 carpetas
- ✅ Ejemplos de request/response
- ✅ Variables `{{baseUrl}}` y `{{token}}`
- ✅ Autenticación Bearer preconfigurada

**Cómo importar:**
1. Abre Postman
2. **File** → **Import**
3. Selecciona este archivo
4. ✅ ¡Listo!

---

### 5. **InterviewMate.postman_environment.json** ⚙️
**Variables de entorno** pre-configuradas para Postman.

**Variables incluidas:**
- `baseUrl` = http://localhost:8080
- `token` = (se obtiene de login)
- `userId`, `username`, `password`, `email`
- `interviewId`, `questionId`
- `tipoEntrevista`, `nivelDificultad`

**Cómo importar:**
1. Abre Postman
2. Click en **Environments** (lado izquierdo)
3. **Import**
4. Selecciona este archivo
5. ✅ ¡Listo!

---

## 🚀 Inicio Rápido

### Opción A: Usar la Documentación (Mejor para aprender)

1. Lee **ENDPOINTS_SUMMARY.md** para una visión general
2. Lee **API_DOCUMENTATION.md** para detalles específicos
3. Sigue **TESTING_GUIDE.md** para probar

### Opción B: Usar Postman (Mejor para testing)

1. Importa **InterviewMate.postman_collection.json**
2. Importa **InterviewMate.postman_environment.json**
3. Selecciona el environment en Postman
4. ¡Ejecuta los requests!

### Opción C: Usar cURL (Mejor para scripts)

Ve a **ENDPOINTS_SUMMARY.md** o **TESTING_GUIDE.md** y copia los comandos cURL.

---

## 📌 Estructura de Endpoints

```
Autenticación (Sin Auth)
├── POST   /auth/register         → Registrar usuario
├── POST   /auth/login            → Login y obtener token
└── GET    /auth/me               → Obtener usuario actual

Usuarios (Con Auth)
├── GET    /usuarios              → Listar usuarios
├── GET    /usuarios/{id}         → Obtener usuario por ID
├── POST   /usuarios              → Crear usuario
├── PUT    /usuarios/{id}         → Actualizar usuario
└── DELETE /usuarios/{id}         → Eliminar usuario

Perfil Profesional (Con Auth)
├── GET    /usuarios/perfil       → Obtener perfil
└── PUT    /usuarios/perfil       → Actualizar perfil

Entrevistas (Con Auth)
├── POST   /entrevistas/start     → Iniciar entrevista
└── POST   /entrevistas/respuestas → Enviar respuesta
```

---

## 🔐 Autenticación

**Sistema:** JWT (JSON Web Token)  
**Algoritmo:** HMAC-SHA256  
**Duración:** 1 hora

### Flujo:
```
1. POST /auth/register  →  Crear usuario
2. POST /auth/login     →  Obtener token
3. GET  /auth/me        →  Usar token (Authorization: Bearer {token})
4. ...                  →  Todos los demás endpoints requieren token
```

---

## 📊 Matriz de Endpoints

| Endpoint | Método | Auth | Estado |
|----------|--------|------|--------|
| `/auth/register` | POST | ❌ | ✅ |
| `/auth/login` | POST | ❌ | ✅ |
| `/auth/me` | GET | ✅ | ✅ |
| `/usuarios` | GET | ✅ | ✅ |
| `/usuarios/{id}` | GET | ✅ | ✅ |
| `/usuarios` | POST | ✅ | ✅ |
| `/usuarios/{id}` | PUT | ✅ | ✅ |
| `/usuarios/{id}` | DELETE | ✅ | ✅ |
| `/usuarios/perfil` | GET | ✅ | ✅ |
| `/usuarios/perfil` | PUT | ✅ | ✅ |
| `/entrevistas/start` | POST | ✅ | ✅ |
| `/entrevistas/respuestas` | POST | ✅ | ✅ |

---

## 💡 Casos de Uso Comunes

### Caso 1: Nuevo Usuario
```
1. POST /auth/register   → Crear cuenta
2. POST /auth/login      → Obtener token
3. PUT /usuarios/perfil  → Completar perfil
4. POST /entrevistas/start → Iniciar entrevista
```

### Caso 2: Usuario Existente
```
1. POST /auth/login      → Obtener token
2. GET /auth/me          → Verificar datos
3. POST /entrevistas/start → Iniciar entrevista
4. POST /entrevistas/respuestas → Responder preguntas
```

### Caso 3: Admin - Gestionar Usuarios
```
1. POST /auth/login      → Login como admin
2. GET /usuarios         → Listar todos
3. POST /usuarios        → Crear nuevo usuario
4. PUT /usuarios/{id}    → Actualizar usuario
5. DELETE /usuarios/{id} → Eliminar usuario
```

---

## 🧪 Ejemplo de Flujo Completo

### 1. Registrarse
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice",
    "email": "alice@example.com",
    "password": "SecurePass123!"
  }'
```

### 2. Login
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"SecurePass123!"}'

# Respuesta incluye: token
```

### 3. Actualizar Perfil
```bash
TOKEN="eyJhbGciOiJIUzI1NiJ9..."

curl -X PUT http://localhost:8080/usuarios/perfil \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "perfilProfesional": "Ingeniero Software con 5 años de experiencia en Java"
  }'
```

### 4. Iniciar Entrevista
```bash
curl -X POST http://localhost:8080/entrevistas/start \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "tipoEntrevista": "TECNICA",
    "nivelDificultad": "INTERMEDIO"
  }'

# Respuesta incluye: lista de preguntas
```

### 5. Responder Pregunta
```bash
curl -X POST http://localhost:8080/entrevistas/respuestas \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "idPregunta": 1,
    "respuesta": "Mi respuesta detallada a la pregunta..."
  }'
```

---

## ⚠️ Errores Comunes

### Error 1: Token Inválido
**Síntoma:** `401 Unauthorized`  
**Causa:** Token expirado o mal formado  
**Solución:** Haz login nuevamente con `POST /auth/login`

### Error 2: Usuario Duplicado
**Síntoma:** `400 Bad Request` - "Username already taken"  
**Causa:** El username ya existe  
**Solución:** Usa un username diferente

### Error 3: Perfil No Completado
**Síntoma:** `400 Bad Request` - "Usuario debe completar su perfil"  
**Causa:** No has completado `PUT /usuarios/perfil` antes de iniciar entrevista  
**Solución:** Llena tu perfil profesional primero

### Error 4: Validación Fallida
**Síntoma:** `400 Bad Request` con fieldErrors  
**Causa:** Datos inválidos (email mal formado, password muy corto, etc.)  
**Solución:** Revisa los requerimientos en `API_DOCUMENTATION.md`

---

## 📱 Variables Postman

Después de importar, configura estas variables:

| Variable | Valor Inicial | Cómo Obtener |
|----------|---------------|-------------|
| `baseUrl` | http://localhost:8080 | Predefinido |
| `token` | (vacío) | POST /auth/login |
| `userId` | 1 | GET /usuarios |
| `interviewId` | (vacío) | POST /entrevistas/start |

---

## 🔄 Flujo de Autenticación

```
┌─────────────────────────────────────────────┐
│  Cliente sin autenticar                      │
└──────────────┬──────────────────────────────┘
               │
               ▼
        POST /auth/register
        ó POST /auth/login
               │
               ▼
        ┌──────────────────┐
        │ JWT Token        │
        │ Válido 1 hora    │
        └────────┬─────────┘
                 │
                 ▼
        ┌─────────────────────────────────────┐
        │ Todos los demás endpoints           │
        │ Requieren Bearer Token en header    │
        └─────────────────────────────────────┘
```

---

## 📞 Soporte

**URL Base:** http://localhost:8080  
**Database:** PostgreSQL  
**Server:** Spring Boot 4.0.3  
**Java:** 21

---

## 📝 Notas Importantes

1. **Seguridad:** El token expira en 1 hora. Después debes hacer login nuevamente.
2. **HTTPS:** En producción, siempre usa HTTPS, nunca HTTP.
3. **Secreto JWT:** Cambia el `app.jwt.secret` en `application.properties` antes de deploy.
4. **Rate Limiting:** Considera implementar rate limiting para login y register.
5. **Logs:** Los logs se guardan en el servidor. Revísalos si hay errores.

---

## ✅ Checklist Previo al Desarrollo

- [ ] Importé la colección Postman
- [ ] Importé el environment Postman
- [ ] He registrado un usuario de prueba
- [ ] He completado el perfil profesional
- [ ] He iniciado una entrevista
- [ ] He respondido una pregunta
- [ ] Entiendo el flujo de autenticación JWT

---

**Versión:** 1.0  
**Última actualización:** Marzo 2026  
**Autor:** InterviewMate Development Team

---

## 📚 Documentos Relacionados

- [API_DOCUMENTATION.md](./API_DOCUMENTATION.md) - Documentación completa
- [ENDPOINTS_SUMMARY.md](./ENDPOINTS_SUMMARY.md) - Resumen de endpoints
- [TESTING_GUIDE.md](./TESTING_GUIDE.md) - Guía de pruebas
- [InterviewMate.postman_collection.json](./InterviewMate.postman_collection.json) - Colección Postman
- [InterviewMate.postman_environment.json](./InterviewMate.postman_environment.json) - Environment Postman

