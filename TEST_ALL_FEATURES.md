# Test Every Feature — Copy-Paste Checklist

Do these in order in Swagger (`http://localhost:8080/swagger-ui.html`). Each
JSON block is ready to paste directly into "Try it out."

---

## 1. Signup (creates users with different roles)

Do this 3 times with different bodies to get 3 test users:

**PM user** → `POST /api/auth/signup`
```json
{ "fullName": "PM Test", "email": "pm@neuroforge.com", "password": "password123", "role": "PROJECT_MANAGER" }
```

**BA user**
```json
{ "fullName": "BA Test", "email": "ba@neuroforge.com", "password": "password123", "role": "BUSINESS_ANALYST" }
```

**QA user**
```json
{ "fullName": "QA Test", "email": "qa@neuroforge.com", "password": "password123", "role": "QA" }
```

Note the `userId` returned each time — you'll need these ids below (probably 1, 2, 3).

## 2. Login
`POST /api/auth/login`
```json
{ "email": "pm@neuroforge.com", "password": "password123" }
```
Copy the token → click **Authorize** 🔒 → paste it → Authorize. You are now
acting as the PM user for every request below.

## 3. View users
`GET /api/users` — see all 3 you just created
`GET /api/users?role=QA` — see only the QA one

## 4. Projects (PM token required)
`POST /api/projects`
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
`GET /api/projects` — see it in the list
`PUT /api/projects/1` — change `status` to `"IN_PROGRESS"`, resend

## 5. Requirements (switch Authorize to BA token now)
Login as `ba@neuroforge.com` → Authorize with that new token.

`POST /api/requirements`
```json
{
  "title": "User authentication module",
  "description": "Signup/login with JWT",
  "technicalStack": "Spring Boot, MySQL",
  "projectId": 1,
  "createdById": 2
}
```
`GET /api/requirements?projectId=1`

## 6. Tasks (switch back to PM token)
Login as `pm@neuroforge.com` → Authorize with that token.

`POST /api/tasks`
```json
{
  "taskName": "Build JWT auth filter",
  "description": "Implement Spring Security JWT filter",
  "status": "TODO",
  "projectId": 1,
  "assignedToId": 1,
  "dueDate": "2026-02-15"
}
```
`GET /api/tasks?projectId=1`

## 7. Test Cases (switch to QA token)
Login as `qa@neuroforge.com` → Authorize with that token.

`POST /api/testcases`
```json
{
  "title": "Login endpoint returns valid JWT",
  "status": "PENDING",
  "noOfTestCases": 3,
  "taskId": 1,
  "createdById": 3
}
```
`GET /api/testcases?taskId=1`

## 8. Reports (switch back to PM token)
Login as `pm@neuroforge.com` again → Authorize.

`POST /api/reports`
```json
{
  "reportType": "BUG_REPORT",
  "bugId": 101,
  "severity": "HIGH",
  "description": "Token expiry not enforced correctly",
  "testCaseId": 1,
  "projectId": 1,
  "generatedById": 1
}
```
`GET /api/reports?projectId=1`

## 9. Prove restrictions work
While still on the PM token, try `POST /api/testcases` — should fail with
403 (only QA/ADMIN can create test cases). That failure is expected and
proves the security is real.

---

# Now see it all in the actual database

## Option A — MySQL Workbench (visual)
1. Open MySQL Workbench → connect to your local instance
2. Left sidebar → **Schemas** → expand `neuroforge_db` → expand **Tables**
3. You'll see: `users`, `projects`, `requirements`, `tasks`, `test_cases`, `reports`
4. Right-click any table → **Select Rows - Limit 1000** → a spreadsheet-style
   grid opens showing every row you just created through the API

## Option B — SQL queries (more convincing for a demo — shows you understand SQL, not just clicking)
Open a new SQL tab in Workbench (the little script icon), paste and run each:

```sql
USE neuroforge_db;

SELECT id, full_name, email, role FROM users;

SELECT id, project_name, status, manager_id FROM projects;

SELECT id, title, technical_stack, project_id, created_by FROM requirements;

SELECT id, task_name, status, project_id, assigned_to FROM tasks;

SELECT id, title, status, task_id, created_by FROM test_cases;

SELECT id, report_type, severity, project_id, generated_by FROM reports;
```

Run each with the ▶ (lightning bolt) button. You'll see the exact rows your
API calls created — real, persisted data, not something faked in memory.

## Bonus — prove passwords are actually hashed
```sql
SELECT email, password FROM users;
```
The `password` column will show something like
`$2a$10$N9qo8uLOickgx2ZMRZoMy...` — that's BCrypt output, not the plaintext
`password123` you typed. Good talking point if anyone asks about security.
