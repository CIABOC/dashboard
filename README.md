# CIABOC Management Dashboard

Separate Next.js frontend and Spring Boot backend for the supplied CIABOC management dashboard wireframe.

## Frontend

The Next.js application is located in `frontend/`.

```powershell
cd frontend
npm install
copy .env.example .env.local
npm run dev
```

Open `http://localhost:3000`. Set `NEXT_PUBLIC_API_BASE_URL=http://localhost:8080` in `.env.local` when overriding the default.

```powershell
npm run build
npm start
```

Convenience scripts are also available from the repository root:

```powershell
npm run dev
npm run build
npm run lint
npm start
```

## PostgreSQL

The dedicated CIABOC PostgreSQL container is available on host port `55432`:

```powershell
docker ps
docker exec ciaboc-dashboard-db psql -U ciaboc -d ciaboc_dashboard -c "SELECT 1;"
```

The container uses database `ciaboc_dashboard` and role `ciaboc`.

## Backend

```powershell
cd backend
mvn spring-boot:run
```

Or set overrides for the shell:

```powershell
$env:DB_URL="jdbc:postgresql://127.0.0.1:55432/ciaboc_dashboard"
$env:DB_USERNAME="ciaboc"
$env:DB_PASSWORD="ciaboc"
mvn spring-boot:run
```

Spring resolves environment variables for server, database and CORS settings. The default development database URL is `jdbc:postgresql://localhost:55432/ciaboc_dashboard`.

Run tests:

```powershell
cd backend
mvn test
```

## Health and APIs

- `http://localhost:8080/actuator/health`
- `http://localhost:8080/api/v1/health`
- `http://localhost:8080/api/v1/kpis`
- `http://localhost:8080/api/v1/executive/overview`

## Temporary Local Login

Until CIABOC SSO is configured, the login page also accepts environment-controlled local credentials:

- Email: `admin@ciaboc.local`
- Password: `ChangeMe123!`

Configure or change these before use:

```powershell
$env:LOCAL_AUTH_ENABLED="true"
$env:LOCAL_AUTH_EMAIL="admin@ciaboc.local"
$env:LOCAL_AUTH_PASSWORD="ChangeMe123!"
```

Set `LOCAL_AUTH_ENABLED=false` to disable temporary login. This fallback must not be enabled in production.

## Project Documentation

Phase analysis and architecture reference documents are in [docs/](docs/).
