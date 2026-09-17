[README.md](https://github.com/user-attachments/files/32319870/README.md)
# NeuroForge Enterprise SDLC Platform — Backend

A Spring Boot REST API backend for an AI-powered SDLC management platform. JWT-based
auth, role-based users (PM, BA, Architect, UI/UX, Developer, QA, DevOps, Admin), and
full CRUD for Projects, Requirements, Tasks, Test Cases, and Reports.

This backend is built **frontend-agnostic**: it's a pure REST API. Whatever you build
later (React, Angular, Vue) will talk to it over HTTP — nothing here needs to change
when you add a frontend.

---

## 1. Tech stack (all free)

| Layer          | Tool                                   |
|----------------|-----------------------------------------|
| Language       | Java 17                                 |
| Framework      | Spring Boot 3.3.4                       |
| Build tool     | Maven                                   |
| Database       | MySQL 8 (local, via MySQL Workbench)    |
| Auth           | Spring Security + JWT (jjwt 0.12.6)     |
| API docs/test  | Swagger UI (springdoc-openapi)          |
| API testing    | Postman (collection included)           |

---

## 2. What you need installed (all free, one-time)

1. **JDK 17** — https://adoptium.net/ (Temurin 17)
2. **Maven** — usually bundled with your IDE. If not: https://maven.apache.org/download.cgi
3. **MySQL Community Server 8.x** — https://dev.mysql.com/downloads/mysql/
4. **MySQL Workbench** — https://dev.mysql.com/downloads/workbench/ (installed automatically with MySQL Community on Windows)
5. **An IDE**: IntelliJ IDEA Community Edition (free) — https://www.jetbrains.com/idea/download/ — or VS Code with the "Extension Pack for Java"
6. **Postman** (Desktop or web) — https://www.postman.com/downloads/

You already have a Postman connector active in this chat, so you can also test
straight from here once the server is running locally — but Postman desktop/web
works the same way with the collection I've included.

---

## 3. Set up the database (MySQL Workbench)

1. Open **MySQL Workbench**, connect to your local MySQL instance (root / whatever
   password you set during install).
2. You do **not** need to manually create tables. `application.yml` has
   `ddl-auto: update`, so Hibernate creates every table automatically the first
   time you run the app.
3. If you just want to *see* the schema visually in Workbench beforehand, open
   `database/neuroforge_schema.sql` (included in this project) and run it — it
   creates the exact same tables Hibernate would. Either path works; don't run
   both, pick one:
   - **Easiest:** do nothing here, just start the Spring Boot app (step 4) and let
     Hibernate create everything, then refresh Workbench's schema list to see it.
   - **Manual:** run `neuroforge_schema.sql` in Workbench first, and change
     `ddl-auto` to `validate` in `application.yml` so Hibernate checks against it
     instead of altering it.
4. Update `src/main/resources/application.yml` if your MySQL username/password
   isn't `root`/`root`:

```yaml
spring:
  datasource:
    username: root
    password: YOUR_MYSQL_PASSWORD
```

---

## 4. Run the backend

### Option A — IntelliJ IDEA (recommended for you as a beginner)

1. Open IntelliJ → **Open** → select the `neuroforge-backend` folder.
2. Let it auto-import the Maven project (first import downloads all dependencies
   from Maven Central — needs internet, one-time).
3. Make sure MySQL is running locally (check MySQL Workbench connects fine).
4. Open `SdlcBackendApplication.java` → click the green ▶ run icon.
5. Console should show `Tomcat started on port(s): 8080` with no errors.

### Option B — Command line (if you have Maven installed)

```bash
cd neuroforge-backend
mvn spring-boot:run
```

### Verifying it's alive

Open `http://localhost:8080/swagger-ui.html` in your browser. You should see the
full interactive API documentation — every endpoint, grouped by controller.

---

## 5. Test it end-to-end (no frontend needed yet)

### Via Swagger UI
1. Go to `http://localhost:8080/swagger-ui.html`
2. Expand **Auth → POST /api/auth/signup**, click "Try it out", use:
   ```json
   {
     "fullName": "Ragshi Kumar",
     "email": "ragshi@neuroforge.com",
     "password": "password123",
     "role": "PROJECT_MANAGER"
   }
   ```
3. Execute — you'll get back a JWT `token` in the response.
4. Click the **Authorize** 🔒 button (top right), paste the token (just the raw
   token string, Swagger adds "Bearer " for you), click Authorize.
5. Now every other endpoint (Projects, Tasks, etc.) will work — try
   **POST /api/projects** using the `managerId` from your signup response.

### Via Postman (included collection)
1. Import `postman/NeuroForge_SDLC_Platform.postman_collection.json` into Postman.
2. Run **Auth → Signup** (or Login if the user already exists) first — a test
   script automatically saves the JWT into a collection variable, so every other
   request in the collection is authenticated automatically.
3. Run any other request (Create Project, Create Task, etc.) in any order.

---

## 6. API overview

All endpoints are prefixed `/api`. Everything except `/api/auth/**` and Swagger
requires header `Authorization: Bearer <token>`.

| Resource      | Endpoints |
|---------------|-----------|
| Auth          | `POST /api/auth/signup`, `POST /api/auth/login` |
| Users         | `GET /api/users`, `GET /api/users/{id}`, `GET /api/users?role=QA` |
| Projects      | `POST /api/projects`, `GET /api/projects`, `GET /api/projects/{id}`, `PUT /api/projects/{id}`, `DELETE /api/projects/{id}` |
| Requirements  | same CRUD pattern, plus `GET /api/requirements?projectId=1` |
| Tasks         | same CRUD pattern, plus `?projectId=` and `?assignedToId=` filters |
| Test Cases    | same CRUD pattern, plus `?taskId=` filter |
| Reports       | same CRUD pattern, plus `?projectId=` filter |

Roles available at signup: `ADMIN`, `PROJECT_MANAGER`, `BUSINESS_ANALYST`,
`SOFTWARE_ARCHITECT`, `UI_UX_DESIGNER`, `SOFTWARE_DEVELOPER`, `QA`, `DEVOPS`.

---

## 7. How this stays frontend-safe

- **CORS** (`config/CorsConfig.java`) already allows any `localhost:*` origin, so
  whatever port your React/Vite/Angular dev server runs on (3000, 5173, 4200...)
  will work immediately — you won't touch this file until you deploy the frontend
  somewhere, at which point you add one line for the deployed URL.
- **Pure REST + JSON**, stateless JWT auth — no server-side sessions, no
  server-rendered views. Any frontend framework can consume this identically.
- **DTOs everywhere** (`dto/` package) — controllers never expose entities
  directly, so you can reshape the frontend without ever worrying about leaking
  internal fields like password hashes.

When you're ready to build the frontend, you'll just call these same endpoints
with `fetch`/`axios`, store the JWT (e.g. in memory or an httpOnly cookie via a
backend proxy — avoid localStorage for production-grade auth), and send it as
`Authorization: Bearer <token>` on every request.

---

## 8. Going to the cloud later (still free)

When you're ready to deploy instead of running locally:

- **Backend hosting:** Render.com free web service tier, or Railway.app free trial
- **MySQL hosting:** Aiven free MySQL plan, Railway MySQL plugin, or Clever Cloud
  free MySQL (256MB)
- Update `application.yml`'s `datasource.url/username/password` to point at the
  cloud DB, add your deployed frontend's URL to `CorsConfig.java`, redeploy.

No code changes needed beyond configuration — that's the whole point of keeping
config externalized in `application.yml`.

---

## 9. Project structure

```
neuroforge-backend/
├── pom.xml
├── database/
│   └── neuroforge_schema.sql        (reference schema for MySQL Workbench)
├── postman/
│   └── NeuroForge_SDLC_Platform.postman_collection.json
└── src/main/
    ├── resources/application.yml
    └── java/com/neuroforge/sdlc/
        ├── SdlcBackendApplication.java
        ├── config/       (Security, CORS, OpenAPI/Swagger config)
        ├── security/     (JWT service, filter, user details service)
        ├── entity/       (User, Project, Requirement, Task, TestCase, Report + enums)
        ├── repository/   (Spring Data JPA interfaces)
        ├── dto/          (Request/Response objects — never expose entities directly)
        ├── service/      (business logic)
        ├── controller/   (REST endpoints)
        └── exception/    (global error handling)
```
