# Despliegue en Coolify

La aplicación se publica como una sola imagen. El Dockerfile de la raíz compila el frontend con pnpm y el backend con Gradle, y deja la SPA dentro del jar. Coolify usa el build pack Dockerfile.

## Servicio

- Puerto del contenedor: `8080`.
- Health check: `GET /actuator/health`.
- No hace falta un proxy aparte ni CORS: el navegador habla con el mismo origen.

## Base de datos

Crea un Postgres en Coolify y entrega estas variables al contenedor de la aplicación:

- `SPRING_DATASOURCE_URL` con el host interno, por ejemplo `jdbc:postgresql://postgres:5432/boilerplate`.
- `SPRING_DATASOURCE_USERNAME`.
- `SPRING_DATASOURCE_PASSWORD`.

Flyway aplica las migraciones al arrancar. Hibernate solo valida el esquema (`ddl-auto=validate`).

## WorkOS

Define en el servicio, no en la imagen:

- `WORKOS_CLIENT_ID`
- `WORKOS_ISSUER` si no usas el emisor por defecto `https://api.workos.com`
- `WORKOS_AUDIENCE` solo si la plantilla JWT incluye `aud`
- `WORKOS_API_HOSTNAME` si configuraste un dominio de autenticación
- `WORKOS_REDIRECT_URI` con la URL pública, por ejemplo `https://app.ejemplo.cl`

Esa redirect URI también se registra en el dashboard de WorkOS. La SPA la recibe por `GET /api/config`.

## Observabilidad

Por defecto no se exporta telemetría. Para enviarla a un colector OTLP:

- `OTLP_METRICS_ENABLED=true` y `OTLP_METRICS_URL`
- `OTLP_TRACES_ENABLED=true` y `OTLP_TRACES_ENDPOINT`
- `OTLP_LOGS_ENABLED=true` y `OTLP_LOGS_ENDPOINT`

Los endpoints HTTP de Micrometer y OpenTelemetry incluyen el path (`/v1/metrics`, `/v1/traces`, `/v1/logs`).

## Registro

En `main`, GitHub Actions publica `ghcr.io/overrid3cl/springboot-react-boilerplate`. Coolify puede construir desde el Dockerfile del repositorio o tirar de esa imagen. Si tira de GHCR, el runtime igual necesita las variables de arriba: el client id no queda compilado en el frontend.
