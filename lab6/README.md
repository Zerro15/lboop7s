# Lab6 backend with LB7 frontend

This module now bundles the LB7 static frontend (HTML/JS/CSS) and serves it from the WAR alongside the LB6/LB7 backend APIs.

## Project layout
- `src/main/webapp/` — LB7 frontend (HTML pages, assets, minimal `web.xml`).
- `src/main/java/com/lab7/...` — LB7 backend servlets, services, DAOs, math utilities.
- `src/main/resources/scripts/` — SQL scripts used by the LB7 DAOs.

## Running (single-stack)
```bash
cd lab6
mvn clean package
mvn tomcat7:run
```

Then open http://localhost:8080/lab5/ (adjust context path if your container differs). The frontend files are served from the root; API calls use the `/api/lb7/*` endpoints.

### Docker compose option
If you prefer the existing Docker workflow:
```bash
cd lab6
./start-app.sh
```
This starts PostgreSQL via `docker-compose` and the application at http://localhost:8080/.

## API notes for the frontend
- All frontend requests target `/api/lb7/...` (e.g., `/api/lb7/users`, `/api/lb7/functions`, `/api/lb7/points`, `/api/lb7/operations`).
- Basic Auth is required for protected routes; the frontend stores credentials in `localStorage` and attaches the `Authorization` header automatically.
