# InterviewMate Frontend

Frontend en Next.js + TypeScript separado del backend original.

## Cambios principales

- Login con diseño más limpio en negro, blanco y azul claro.
- Redirección a la página de inicio después de ingresar.
- Inicio con bienvenida usando el nombre del usuario.
- Historial de sesiones con recomendaciones por pregunta.
- Configuración de entrevista con 3 módulos:
  - nivel 1, 2 y 3
  - profesión
  - cargo según profesión
- Entrevista en español con avatar IA.
- Grabación de audio en tiempo real.
- Botones para repetir pregunta, anular entrevista, enviar audio y continuar.
- Usuario demo para pruebas locales.

## Usuario demo

- Usuario: `camilo.demo`
- Contraseña: `Demo12345*`

## Ejecución local

1. Instala Node.js 20 o superior.
2. Abre una terminal en:

```powershell
cd "C:\Users\camil\Documents\New project\interviewmate-frontend"
```

3. Si PowerShell bloquea `npm`, usa:

```powershell
npm.cmd install
Copy-Item .env.example .env.local
npm.cmd run dev
```

4. Abre:

```text
http://localhost:3000
```

5. Ingresa con el usuario demo o crea uno nuevo.
6. Para transcripción local y mejor evaluación usa Chrome o Edge.

## Conexión actual

El frontend ya puede integrarse con tu backend para:

- `POST /auth/register`
- `POST /auth/login`
- `GET /auth/me`
- `GET /api/v1/results/me`

La generación de preguntas ya es dinámica en local según profesión y cargo. La evaluación ahora usa transcripción local del navegador y coincidencia con contenido, pero para una IA totalmente real y precisa todavía hará falta integrarla desde backend o con un proveedor de IA.
