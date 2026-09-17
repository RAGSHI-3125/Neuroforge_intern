# NeuroForge Backend — Live Demo Script

Use this as your speaking notes. Say the **[SAY]** lines out loud, do the **[DO]** actions in Swagger.

---

## Part 1 — Architecture overview (2 min, no screen needed yet)

**[SAY]**
"This is a Spring Boot REST API backend. It follows a standard layered
architecture used in real production systems:

- **Controller layer** — receives HTTP requests, doesn't contain business logic
- **Service layer** — the actual business rules (e.g. 'a project needs a valid manager')
- **Repository layer** — talks to the database, powered by Spring Data JPA
- **Entity layer** — Java classes that map 1:1 to database tables
- **DTO layer** — Request/Response objects so we never expose raw database
  entities (like password hashes) directly to the outside world

Every request flows: **Controller → Service → Repository → Database**, and the
response flows back the same way, converted into a DTO before it leaves."

**[SAY]**
"Security is handled by JWT — JSON Web Tokens. Instead of the server
remembering who's logged in (a 'session'), the client holds a signed token
and sends it with every request. This is called **stateless authentication**
— it's what almost every modern REST API uses, because it scales better and
works identically whether the client is a web app, mobile app, or another
service."

---

## Part 2 — Live demo: the full CRUD + auth flow

Open `http://localhost:8080/swagger-ui.html` on screen.

### Step 1: Signup (Create a user)
**[SAY]** "First, authentication. Signup takes a name, email, password, and
role — we support 8 roles matching real SDLC job functions: Project Manager,
Business Analyst, Architect, Developer, QA, DevOps, UI/UX Designer, Admin."

**[DO]** Auth → POST /api/auth/signup → Try it out → Execute with:
```json
{
  "fullName": "Ragshi Vishwavj",
  "email": "ragshi@neuroforge.com",
  "password": "password123",
  "role": "PROJECT_MANAGER"
}
```

**[SAY]** "The response gives us a JWT token — this is our proof of identity
for every future request. Behind the scenes, the password was never stored
as-is — it's hashed with BCrypt, a one-way encryption algorithm. Even I, as
the developer, can't see anyone's actual password in the database."

### Step 2: Authorize
**[DO]** Copy the token → click Authorize 🔒 → paste → Authorize → Close.

**[SAY]** "Now every request I make includes this token automatically. This
is exactly what a frontend app would do — store the token after login, then
attach it to every API call."

### Step 3: Create (POST) — Project
**[DO]** Projects → POST /api/projects → Try it out:
```json
{
  "projectName": "NeuroForge MVP",
  "description": "AI-powered SDLC platform",
  "startDate": "2026-01-01",
  "endDate": "2026-06-30",
  "status": "PLANNING",
  "managerId": 1
}
```
**[SAY]** "This is the **C** in CRUD — Create. Notice the response includes
an auto-generated `id` and `createdAt` timestamp — the database assigned
those, we didn't."

### Step 4: Read (GET) — all projects, and by ID
**[DO]** GET /api/projects → Execute (shows the list)
**[DO]** GET /api/projects/{id} → enter `1` → Execute (shows just that one)

**[SAY]** "This is **R** — Read. One endpoint for listing everything, one for
fetching a specific record — standard REST convention."

### Step 5: Update (PUT) — Project
**[DO]** PUT /api/projects/{id} → id=1, change `status` to `"IN_PROGRESS"` → Execute

**[SAY]** "**U** — Update. We send the full object back with changes; the
server overwrites the existing record."

### Step 6: Create the rest of the chain
**[SAY]** "Now let's prove the relationships in our ER diagram actually work
— a Project has Requirements and Tasks, a Task has TestCases, and a TestCase
can generate a Report."

**[DO]** POST /api/requirements (projectId: 1, createdById: 1)
**[DO]** POST /api/tasks (projectId: 1, assignedToId: 1)
**[DO]** POST /api/testcases (taskId: 1, createdById: 1)
**[DO]** POST /api/reports (projectId: 1, generatedById: 1, testCaseId: 1)

**[SAY]** "Each of these is linked by a foreign key back to the project or
task it belongs to — exactly matching the MANAGES / INCLUDES / CONTAINS /
HAS / CREATES / GENERATES relationships from our ER diagram."

### Step 7: Delete (DELETE)
**[DO]** DELETE /api/reports/{id} → Execute → note the 204 No Content response

**[SAY]** "**D** — Delete. A 204 means 'success, nothing to return.' That
completes full CRUD across every entity."

### Step 8: Prove role-based access control (this is the part that shows real understanding)

**[SAY]** "Our UML diagram assigns specific actions to specific roles — only
a Project Manager creates a Project, only a Business Analyst creates a
Requirement, only QA creates a Test Case. Let's prove that's actually
enforced, not just decoration."

**[DO]** Signup a second user with role `QA`:
```json
{
  "fullName": "Test QA User",
  "email": "qa@neuroforge.com",
  "password": "password123",
  "role": "QA"
}
```
**[DO]** Authorize with this QA user's new token (replace the old one).
**[DO]** Try POST /api/projects with this QA token → Execute.

**[SAY]** "Watch — this fails with a 403 Forbidden, 'Your role does not have
permission to perform this action.' The QA account is fully authenticated —
it has a valid token — but Spring Security's `@PreAuthorize` check on the
Project controller rejects it because the role doesn't match. This is
enforced on the server, not just hidden in a frontend menu — even someone
calling the API directly with tools like Postman can't bypass it."

**[DO]** Now try POST /api/testcases with the same QA token → Execute → succeeds (201).

**[SAY]** "Same user, different endpoint — succeeds, because QA *is*
authorized to create test cases. That's role-based access control working
exactly as designed in our diagram."

### Step 9: Prove security actually blocks unauthorized access
**[DO]** Click Authorize → Logout (removes the token) → try GET /api/projects again

**[SAY]** "Without a valid token, the request is rejected — this is Spring
Security's filter chain doing its job. This is what stops random people from
hitting our API without logging in first."

---

## Part 3 — Anticipated questions & answers

**Q: Why MySQL and not something else?**
A: Free, industry-standard relational database, matches the ER diagram
structure exactly (tables + foreign keys), and integrates natively with
Spring Data JPA.

**Q: Why JWT instead of sessions?**
A: Stateless — the server doesn't need to remember anything about who's
logged in, which means this API can scale horizontally and works the same
for a web frontend, mobile app, or another backend service calling it.

**Q: What happens when the frontend is built?**
A: Nothing here changes. CORS is already configured to accept requests from
any localhost frontend during development, and DTOs mean the frontend only
ever sees clean, purpose-built JSON — never raw database internals.

**Q: How do you know it's secure?**
A: Passwords are BCrypt-hashed (never stored in plain text), all business
endpoints require a valid signed JWT, and Spring Security's filter chain
runs before any controller code executes.
