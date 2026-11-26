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
If the container was stopped, run `docker start ciaboc-dashboard-db`. The dashboard needs PostgreSQL for sign-in even when Vehicle Movement & Reception reads its report data from Atlas.

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

## Live Vehicle Movement & Reception demo

Stop any running dashboard backend before starting the Atlas demo. In PowerShell, run this from `backend/`, using the path to the credential file you received. The launcher reads `MONGODB_URI` and passes it to Spring Boot as `VMR_ATLAS_URI`.

```powershell
.\start-atlas-demo.ps1 -AtlasCredentialsFile 'C:/path/to/atlas-credentials.env'
```

Sign in to the dashboard and open **Vehicle Movement & Reception**. It fetches aggregate counts through Spring Boot, refreshes every 30 seconds while the report is open, and also has a manual refresh button. Use an Atlas account with read access to the RMS database and allow the demo backend's connection in Atlas.

The backend defaults to the RMS Atlas database `ciaboc_rvm` and reads the `visit` and `vehicle_movement` collections. Override these names with `VMR_ATLAS_DATABASE`, `VMR_ATLAS_VISITS_COLLECTION`, or `VMR_ATLAS_MOVEMENTS_COLLECTION` for a different deployment. The feed uses `visitDate`, `status`, `entryApprovedAt`, `occurredAt`, `direction`, and `decision`. The equivalent snake-case fields are also supported.

Vehicle Movement & Reception shows MongoDB results after a successful fetch. If Atlas is unreachable or `VMR_ATLAS_URI` is missing, the page shows the connection status. The other dashboard modules keep their existing sources.

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
