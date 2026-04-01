# AI Service Usage Guide

Este documento explica como configurar y usar la integracion de IA en `InterviewMate` para los dos modos del producto:

- `Interview mode`: simulacion de entrevista con generacion de preguntas y salida de voz (TTS).
- `Study mode`: transcripcion de audio y generacion de contenido en texto (preguntas, respuestas, plan).

## 1) Donde esta la configuracion

- Propiedades base: `src/main/resources/application.properties`
- Binding tipado: `src/main/java/com/interviewmate/InterviewMate/config/AiServiceProperties.java`
- Validacion al iniciar: `src/main/java/com/interviewmate/InterviewMate/config/AiServiceConfigValidator.java`
- Plantilla de entorno: `.env.example`

## 2) Variables de entorno

### Interview mode

- `AI_INTERVIEW_ENABLED` (true/false)
- `AI_INTERVIEW_PROVIDER` (default: `openai`)
- `AI_INTERVIEW_API_KEY` (requerida si `AI_INTERVIEW_ENABLED=true`)
- `AI_INTERVIEW_BASE_URL` (default: `https://api.openai.com/v1`)
- `AI_INTERVIEW_CHAT_MODEL` (default: `gpt-4o-mini`)
- `AI_INTERVIEW_TTS_MODEL` (default: `gpt-4o-mini-tts`)
- `AI_INTERVIEW_VOICE` (default: `alloy`)

### Study mode

- `AI_STUDY_ENABLED` (true/false)
- `AI_STUDY_PROVIDER` (default: `openai`)
- `AI_STUDY_API_KEY` (requerida si `AI_STUDY_ENABLED=true`)
- `AI_STUDY_BASE_URL` (default: `https://api.openai.com/v1`)
- `AI_STUDY_TRANSCRIPTION_MODEL` (default: `whisper-1`)
- `AI_STUDY_TEXT_MODEL` (default: `gpt-4o-mini`)

## 3) Regla de validacion en arranque

Al iniciar la app:

- Si `AI_INTERVIEW_ENABLED=true` y no hay `AI_INTERVIEW_API_KEY`, el backend falla en startup.
- Si `AI_STUDY_ENABLED=true` y no hay `AI_STUDY_API_KEY`, el backend falla en startup.

Esto evita deploys incompletos en produccion.

## 4) Ejemplo local (.env)

```dotenv
AI_INTERVIEW_ENABLED=true
AI_INTERVIEW_API_KEY=replace-with-interview-key
AI_STUDY_ENABLED=true
AI_STUDY_API_KEY=replace-with-study-key
```

Y luego correr la app normalmente.

## 5) Ejemplo en Render

En Render -> Environment, definir:

- `AI_INTERVIEW_ENABLED=true`
- `AI_INTERVIEW_API_KEY=<secret>`
- `AI_STUDY_ENABLED=true`
- `AI_STUDY_API_KEY=<secret>`
- (Opcional) modelos/base URL si quieres customizar proveedor.

No guardes claves en `application.properties` ni en commits.

## 6) Estado actual del modulo

Actualmente los servicios de negocio estan en modo `stub`:

- `AiInterviewServiceImpl` genera y evalua contenido mock.
- `StudyServiceImpl` genera preguntas mock.

La configuracion de este documento deja lista la base para conectar proveedor real de IA sin cambiar contratos.

## 7) Seguridad recomendada

- Rotar cualquier API key que se haya expuesto en logs, commits o capturas.
- Usar secretos del proveedor de despliegue (Render/Vercel), no defaults hardcodeados.
- Mantener `AI_*_ENABLED=false` en entornos donde no se use IA.

