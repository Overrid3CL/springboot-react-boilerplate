# Boilerplate Spring Boot + React

Base full-stack de la organización: API Spring Boot y SPA React servidas desde el mismo jar, con sesión de WorkOS AuthKit.

## Stack

| Capa | Elección |
| --- | --- |
| Backend | Spring Boot 4.1.1, Java 25 (Temurin), Gradle 9.8 (Kotlin DSL y version catalog) |
| Web | Spring MVC con virtual threads |
| Datos | PostgreSQL 18.6, Spring Data JPA, Flyway |
| Seguridad | Spring Security 7.1 como resource server OAuth2. Valida el JWT de WorkOS con su JWKS |
| API | Bean Validation, Problem Detail (RFC 9457), springdoc-openapi 3.1.1 |
| Módulos | Spring Modulith 2.1.1 |
| Observabilidad | Actuator, Micrometer y exportación OTLP (apagada por defecto) |
| Frontend | React 19.3, Vite 8, TypeScript 6.0, pnpm 12, TanStack Router y TanStack Query 5 |
| Cliente | `@hey-api/openapi-ts` 0.99.0 con el plugin de TanStack Query |
| UI | shadcn/ui, Tailwind CSS 4, React Hook Form, Zod y Zustand |
| Pruebas | JUnit y Testcontainers 2, Vitest y Playwright |

TypeScript 7.0 ya existe, pero `typescript-eslint` 8.71 solo admite hasta 6.0. Por eso el frontend queda en TypeScript 6.0.3. PostgreSQL 19 sigue en beta, así que la base local es 18.6.

## Estructura

- `backend/`: API, migraciones y tests.
- `frontend/`: SPA. El cliente generado vive en `frontend/src/client` y no se edita a mano.
- `infra/`: notas de despliegue en Coolify.
- `compose.yaml`: Postgres para desarrollo.
- `Dockerfile`: imagen única. La SPA compilada queda dentro del jar.

La SPA y la API comparten origen. No hay CORS. En desarrollo, Vite proxea `/api` hacia el puerto 8080.

## Arranque local

Requisitos: JDK 25 (Gradle puede descargarlo con el resolver de Foojay), Node.js 24, pnpm 12, Docker.

```bash
cp .env.example .env
```

Completa al menos `WORKOS_CLIENT_ID`. Exporta esas variables en la terminal del backend o cárgalas con tu herramienta habitual. Spring Boot no lee el archivo `.env` por sí solo.

```bash
docker compose up -d
cd backend
./gradlew bootRun
```

En otra terminal:

```bash
cd frontend
pnpm install
pnpm dev
```

- SPA: http://localhost:5173
- API: http://localhost:8080
- OpenAPI: http://localhost:8080/swagger-ui.html
- Salud: http://localhost:8080/actuator/health

`bootRun` levanta `compose.yaml` si Docker está disponible (`spring-boot-docker-compose`, solo en desarrollo). Las credenciales por defecto son `boilerplate` / `boilerplate` en la base `boilerplate`.

## WorkOS AuthKit

1. Crea una aplicación AuthKit en el dashboard de WorkOS.
2. Copia el client id a `WORKOS_CLIENT_ID`.
3. En Redirects, agrega `http://localhost:5173` y la URL pública de Coolify.
4. Activa organizaciones y define al menos un rol (por ejemplo `admin` o `member`).
5. El access token es un JWT. La API lo valida contra `https://api.workos.com/sso/jwks/{clientId}`.
6. El emisor por defecto es `https://api.workos.com`. Si usas un dominio de autenticación propio, copia el claim `iss` de un token real a `WORKOS_ISSUER`. Algunos documentos de WorkOS lo muestran con barra final: tiene que coincidir exactamente.
7. Los tokens de sesión no traen `aud`. En ese caso la API acepta el claim `client_id` igual a `WORKOS_CLIENT_ID`. Si creas una plantilla JWT con audiencia, pon el mismo valor en `WORKOS_AUDIENCE` y el token deberá incluirla.

La SPA pide `GET /api/config` (público) y arma el SDK de AuthKit con el client id. No hace falta hornearlo en la imagen. El access token se envía como `Authorization: Bearer` a `/api/**`.

En cada request autenticado se asegura un usuario local (`users.external_subject` = `sub`), una organización (`organizations.external_id` = `org_id`) y una membresía con el rol del token. Así el IdP se puede cambiar sin rehacer el modelo.

El rol del JWT pasa a `ROLE_{ROL}` y cada permiso queda como authority. Las notas de ejemplo solo se leen y escriben dentro de la organización del token. Si el token no trae `org_id`, la API responde 403.

## Variables

| Variable | Uso |
| --- | --- |
| `WORKOS_CLIENT_ID` | Obligatorio. Client id de AuthKit |
| `WORKOS_ISSUER` | Emisor del JWT. Default `https://api.workos.com` |
| `WORKOS_AUDIENCE` | Opcional. Si existe, se exige el claim `aud` |
| `WORKOS_JWKS_URI` | Opcional. Default al JWKS del client id |
| `WORKOS_API_HOSTNAME` | Opcional. Dominio de autenticación para la SPA |
| `WORKOS_REDIRECT_URI` | Opcional. Si falta, la SPA usa el origen del navegador |
| `SPRING_DATASOURCE_URL` | JDBC. En Coolify apunta al Postgres del proyecto |
| `SPRING_DATASOURCE_USERNAME` | Usuario de la base |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de la base |
| `OTLP_METRICS_ENABLED` | `true` para exportar métricas |
| `OTLP_METRICS_URL` | Default `http://localhost:4318/v1/metrics` |
| `OTLP_TRACES_ENABLED` | `true` para exportar trazas |
| `OTLP_TRACES_ENDPOINT` | Default `http://localhost:4318/v1/traces` |
| `OTLP_LOGS_ENABLED` | `true` para exportar logs |
| `OTLP_LOGS_ENDPOINT` | Default `http://localhost:4318/v1/logs` |

No hay secretos en el repositorio. `.env.example` solo trae valores vacíos o de desarrollo.

## Módulo de ejemplo

`notes` es un CRUD en `/api/notes`, protegido y filtrado por organización. `identity` es el otro módulo de Spring Modulith. `ModularityTests` verifica que no se crucen los paquetes internos.

## Pruebas

```bash
cd backend && ./gradlew test
cd frontend && pnpm lint && pnpm typecheck && pnpm test && pnpm exec playwright install --with-deps chromium && pnpm test:e2e
```

Los tests de API firman un JWT local y publican el JWKS en un servidor embebido. No llaman a WorkOS. Cubren 401 sin token, 200 con JWT válido y que una organización no lee notas de otra. Playwright solo comprueba que la página de acceso se renderiza.

Si cambias un endpoint:

```bash
cd backend && ./gradlew exportOpenApi
cd frontend && pnpm openapi:generate
```

El test de backend falla si `frontend/openapi/openapi.json` no coincide con la API. El workflow de GitHub falla si el cliente generado no está commiteado.

## Imagen y Coolify

```bash
docker build -t boilerplate .
```

La imagen escucha en el puerto 8080 y el healthcheck pega a `/actuator/health`.

En Coolify:

1. Recurso nuevo con el build pack Dockerfile. El archivo está en la raíz.
2. Puerto expuesto: 8080.
3. Health check: `/actuator/health`.
4. Agrega un Postgres y define `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` y `SPRING_DATASOURCE_PASSWORD`.
5. Define las variables de WorkOS de la tabla anterior. `WORKOS_REDIRECT_URI` debe ser la URL pública, y esa misma URL tiene que estar en el dashboard.
6. El client id lo lee la SPA en runtime desde `/api/config`. No hace falta un build arg.

Más detalle en `infra/README.md`.

## CI

En cada pull request corren los tests del backend (Testcontainers), lint, typecheck y tests del frontend, el smoke de Playwright, la comprobación del cliente OpenAPI y el build de la imagen. La imagen se publica en GHCR solo en `main`.
