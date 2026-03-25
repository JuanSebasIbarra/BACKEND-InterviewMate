# 📊 Resumen Visual - Documentación InterviewMate

---

## 📂 Estructura de Documentos

```
InterviewMate/
├── 📋 DOCUMENTATION_README.md          ← ÍNDICE PRINCIPAL (Empieza aquí)
├── 📖 API_DOCUMENTATION.md             ← Docs detalladas (técnico)
├── 📋 ENDPOINTS_SUMMARY.md             ← Resumen endpoints (rápido)
├── 🧪 TESTING_GUIDE.md                 ← Pruebas manuales (paso a paso)
├── ⚡ QUICK_REFERENCE.md               ← Tarjeta de referencia (imprimible)
├── ✅ IMPLEMENTATION_CHECKLIST.md      ← Estado del proyecto
├── 📊 DOCUMENTATION_SUMMARY.md         ← Este archivo
├── 🔗 InterviewMate.postman_collection.json
└── ⚙️  InterviewMate.postman_environment.json
```

---

## 🎯 Qué Documento Leer Según Tu Necesidad

### "Quiero entender el proyecto rápidamente" 
👉 Lee **DOCUMENTATION_README.md** + **ENDPOINTS_SUMMARY.md**  
⏱️ Tiempo: 10 minutos

### "Necesito documentación técnica completa"
👉 Lee **API_DOCUMENTATION.md**  
⏱️ Tiempo: 30 minutos

### "Quiero probar los endpoints manualmente"
👉 Lee **TESTING_GUIDE.md** + Importa Postman collection  
⏱️ Tiempo: 1-2 horas

### "Necesito una referencia rápida"
👉 Imprime **QUICK_REFERENCE.md** y tenla a mano  
⏱️ Tiempo: 2 minutos

### "Quiero saber qué falta por hacer"
👉 Lee **IMPLEMENTATION_CHECKLIST.md**  
⏱️ Tiempo: 15 minutos

---

## 🔢 Endpoints por Módulo

```
┌──────────────────────────────────────────────────┐
│          AUTENTICACIÓN (2 públicos)              │
├──────────────────────────────────────────────────┤
│  1. POST   /auth/register      ← Crear cuenta    │
│  2. POST   /auth/login         ← Obtener token   │
│  3. GET    /auth/me            ← Usuario actual  │
└──────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────┐
│         USUARIOS (5 operaciones CRUD)            │
├──────────────────────────────────────────────────┤
│  4. GET    /usuarios           ← Listar          │
│  5. GET    /usuarios/{id}      ← Obtener uno     │
│  6. POST   /usuarios           ← Crear           │
│  7. PUT    /usuarios/{id}      ← Actualizar      │
│  8. DELETE /usuarios/{id}      ← Eliminar        │
└──────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────┐
│      PERFIL PROFESIONAL (2 operaciones)          │
├──────────────────────────────────────────────────┤
│  9. GET    /usuarios/perfil    ← Obtener         │
│ 10. PUT    /usuarios/perfil    ← Actualizar      │
└──────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────┐
│       ENTREVISTAS (2 operaciones)                │
├──────────────────────────────────────────────────┤
│ 11. POST   /entrevistas/start  ← Iniciar (IA)    │
│ 12. POST   /entrevistas/respuestas ← Responder   │
└──────────────────────────────────────────────────┘

TOTAL: 12 endpoints
Públicos: 2
Requieren Auth: 10
```

---

## 📍 Matriz de Módulos

```
                    ┌─────────────────────────┐
                    │   AUTENTICACIÓN JWT     │
                    │  (Login + Register)     │
                    └────────────┬────────────┘
                                 │
                    ┌────────────┴────────────┐
                    │                         │
        ┌───────────▼──────────┐  ┌──────────▼──────────┐
        │     USUARIOS         │  │ PERFIL PROFESIONAL  │
        │  (CRUD + Listar)     │  │   (CV + Datos)      │
        └───────────┬──────────┘  └──────────┬──────────┘
                    │                         │
                    └────────────┬────────────┘
                                 │
                        ┌────────▼────────┐
                        │   ENTREVISTAS   │
                        │  (Preguntas+IA) │
                        └─────────────────┘
```

---

## 🔐 Flujo de Autenticación

```
┌─────────────────────────────────────┐
│   Usuario sin cuenta                │
└────────────┬────────────────────────┘
             │
             ▼
    ┌────────────────────┐
    │ POST /auth/register│
    │ (username, email,  │
    │  password)         │
    └────────┬───────────┘
             │
             ▼
    ┌────────────────────┐
    │  Usuario creado ✓  │
    └────────┬───────────┘
             │
             ▼
    ┌────────────────────────┐
    │ POST /auth/login       │
    │ (username, password)   │
    │        ↓               │
    │   Retorna TOKEN JWT    │
    └────────┬───────────────┘
             │
             ▼
    ┌────────────────────┐
    │ Todos los requests │
    │ posteriores usan:  │
    │ Authorization:     │
    │ Bearer {TOKEN}     │
    └────────────────────┘
```

---

## 📊 Tablas Principales

```
┌──────────────┐
│    USERS     │
├──────────────┤
│ id (PK)      │
│ username (U) │
│ email (U)    │
│ password (H) │
│ created_at   │
└──────────────┘
       │
       ├──→ ┌──────────────┐
       │    │ USER_ROLES   │
       │    ├──────────────┤
       │    │ user_id (FK) │
       │    │ role         │
       │    └──────────────┘
       │
       └──→ ┌──────────────────┐
            │ PROFESSIONAL_     │
            │ PROFILE          │
            ├──────────────────┤
            │ id (PK)          │
            │ user_id (FK)     │
            │ perfil_prof      │
            │ updated_at       │
            └──────────────────┘

       └──→ ┌──────────────────┐
            │   ENTREVISTA     │
            ├──────────────────┤
            │ id (PK)          │
            │ user_id (FK)     │
            │ tipo             │
            │ nivel_dificultad │
            │ estado           │
            │ created_at       │
            └────────┬─────────┘
                     │
                     └──→ ┌──────────────────┐
                          │   PREGUNTA       │
                          ├──────────────────┤
                          │ id (PK)          │
                          │ entrevista_id(FK)│
                          │ contenido        │
                          │ orden            │
                          └────────┬─────────┘
                                   │
                                   └──→ ┌──────────────────┐
                                        │   RESPUESTA      │
                                        ├──────────────────┤
                                        │ id (PK)          │
                                        │ pregunta_id (FK) │
                                        │ usuario_id (FK)  │
                                        │ contenido        │
                                        │ created_at       │
                                        └──────────────────┘
```

---

## 🔄 Flujo de Usuario Típico

```
INICIO
  │
  ├─ 1. POST /auth/register
  │   ├─ Input: username, email, password
  │   └─ Output: UserResponse
  │
  ├─ 2. POST /auth/login
  │   ├─ Input: username, password
  │   └─ Output: JWT Token
  │
  ├─ 3. PUT /usuarios/perfil
  │   ├─ Input: perfilProfesional (CV)
  │   └─ Output: ProfileResponse
  │
  ├─ 4. POST /entrevistas/start
  │   ├─ Input: tipoEntrevista, nivelDificultad
  │   └─ Output: InterviewResponse + Preguntas (generadas por IA)
  │
  ├─ 5. POST /entrevistas/respuestas (x N preguntas)
  │   ├─ Input: idPregunta, respuesta
  │   └─ Output: AnswerResponse
  │
  └─ FIN: Usuario completó la entrevista

FLUJO SECUNDARIO (Admin)
  │
  ├─ 1. POST /auth/login (como admin)
  ├─ 2. GET /usuarios (listar todos)
  ├─ 3. GET /usuarios/{id} (detalle)
  ├─ 4. PUT /usuarios/{id} (actualizar)
  ├─ 5. DELETE /usuarios/{id} (eliminar)
  └─ FIN
```

---

## 📚 Documentos por Tipo

### 📖 Técnico/Detallado
- **API_DOCUMENTATION.md** - Documentación completa con ejemplos
- **IMPLEMENTATION_CHECKLIST.md** - Estado técnico del proyecto

### 📋 Resumen/Referencia
- **ENDPOINTS_SUMMARY.md** - Lista compacta de endpoints
- **QUICK_REFERENCE.md** - Tarjeta de referencia (imprimible)
- **DOCUMENTATION_README.md** - Índice de todo

### 🧪 Práctico/Testing
- **TESTING_GUIDE.md** - Guía paso a paso para probar
- **InterviewMate.postman_collection.json** - Colección Postman
- **InterviewMate.postman_environment.json** - Variables Postman

---

## 🎯 Casos de Uso

### Caso 1: Nuevo Usuario (sin cuenta)
```
1. Leer: QUICK_REFERENCE.md (2 min)
2. POST /auth/register (2 min)
3. POST /auth/login (1 min)
4. PUT /usuarios/perfil (2 min)
5. POST /entrevistas/start (2 min)
⏱️ Total: ~10 minutos
```

### Caso 2: Developer Testing Endpoints
```
1. Leer: TESTING_GUIDE.md (20 min)
2. Importar Postman collection (2 min)
3. Ejecutar tests en orden (60 min)
⏱️ Total: ~1.5 horas
```

### Caso 3: Technical Documentation
```
1. Leer: API_DOCUMENTATION.md (30 min)
2. Revisar: IMPLEMENTATION_CHECKLIST.md (15 min)
3. Consultar: ENDPOINTS_SUMMARY.md (10 min)
⏱️ Total: ~1 hora
```

### Caso 4: Integration (Otro Backend)
```
1. Leer: ENDPOINTS_SUMMARY.md (10 min)
2. Revisar ejemplos en: API_DOCUMENTATION.md (20 min)
3. Implementar llamadas HTTP
⏱️ Total: 30+ minutos (según complejidad)
```

---

## 📈 Estadísticas

```
Total de Endpoints:        12
├─ Públicos:              2 (15%)
└─ Privados (Auth):       10 (85%)

Por Método HTTP:
├─ GET:                   5
├─ POST:                  4
├─ PUT:                   2
└─ DELETE:                1

Por Módulo:
├─ Autenticación:         3
├─ Usuarios:              5
├─ Perfil Profesional:    2
└─ Entrevistas:           2

Documentación:
├─ Palabras en docs:      ~15,000
├─ Archivos .md:          6
├─ Ejemplos cURL:         20+
├─ Ejemplos JSON:         50+
└─ Variables Postman:     10
```

---

## ✅ Checklist Rápido

- [ ] Leí DOCUMENTATION_README.md
- [ ] Leí ENDPOINTS_SUMMARY.md
- [ ] Importé la colección Postman
- [ ] Ejecuté los tests de TESTING_GUIDE.md
- [ ] Tengo el token guardado
- [ ] Completé mi perfil profesional
- [ ] Inicié una entrevista
- [ ] Respondí una pregunta
- [ ] Entiendo el flujo completo
- [ ] Estoy listo para integrar

---

## 🚀 Próximos Pasos

### Para Developers
1. Importar Postman collection
2. Leer TESTING_GUIDE.md
3. Ejecutar tests manuales
4. Empezar con pruebas unitarias

### Para PMs/Stakeholders
1. Leer DOCUMENTATION_README.md
2. Revisar IMPLEMENTATION_CHECKLIST.md
3. Agendar review de features

### Para Ops/DevOps
1. Revisar Dockerfile
2. Preparar variables de entorno
3. Configurar base de datos
4. Preparar deployment

---

## 📞 Resumen Rápido

| Aspecto | Valor |
|--------|-------|
| **Endpoints** | 12 (2 públicos, 10 privados) |
| **Documentos** | 6 .md + 2 Postman JSON |
| **Autenticación** | JWT Bearer Token |
| **Base de Datos** | PostgreSQL |
| **Framework** | Spring Boot 4.0.3 |
| **Java** | 21 |
| **API Version** | 1.0 |
| **Status** | ✅ LISTO PARA TESTING |

---

**Última actualización:** Marzo 2026  
**Preparado por:** InterviewMate Development Team  
**Versión de Documentación:** 1.0

