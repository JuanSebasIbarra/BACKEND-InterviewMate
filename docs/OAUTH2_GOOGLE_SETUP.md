# Google OAuth2 Setup (InterviewMate)

## 1) Variables de entorno

Configura estas variables antes de ejecutar la API:

- `GOOGLE_CLIENT_ID`
- `GOOGLE_CLIENT_SECRET`
- `APP_OAUTH2_AUTHORIZED_REDIRECT_URI` (opcional, por defecto `http://localhost:5173/dashboard`)
- `APP_OAUTH2_ERROR_REDIRECT_URI` (opcional, por defecto `http://localhost:5173/login`)
- `APP_OAUTH2_COOKIE_NAME` (opcional, por defecto `interviewmate_auth`)

## 2) Google Cloud Console

En tu OAuth Client (Web application) configura:

- Authorized JavaScript origins: `http://localhost:8080`
- Authorized redirect URIs: `http://localhost:8080/login/oauth2/code/google`

## 3) Endpoints disponibles

- `GET /auth/oauth2/google` inicia el flujo OAuth2 (redirige a Google)
- `GET /login/oauth2/code/google` callback de Google
- `POST /auth/login` login local tradicional
- `POST /auth/register` registro local tradicional

## 4) Respuesta de login social

Al finalizar OAuth2 exitoso, el backend:

- genera el JWT
- guarda el token en cookie `interviewmate_auth` (configurable)
- redirige al frontend en `APP_OAUTH2_AUTHORIZED_REDIRECT_URI` (por defecto `/dashboard`)

Si OAuth2 falla, limpia cookie y redirige a `APP_OAUTH2_ERROR_REDIRECT_URI` (por defecto `/login`) con `error` y `message`.

## 5) Notas

- Si el email de Google no esta verificado, se rechaza el login.
- Si el email ya existe con otro `googleId`, se rechaza para evitar account takeover.
- Los usuarios creados por Google quedan con rol `ROLE_USER`.

