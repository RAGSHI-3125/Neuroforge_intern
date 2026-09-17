-- ============================================================
-- NeuroForge Enterprise SDLC Platform - Database Schema
-- Engine: MySQL 8.x
-- ============================================================
-- You do NOT have to run this manually - Spring Boot (Hibernate)
-- will auto-create these exact tables on first run because
-- application.yml has "ddl-auto: update".
--
-- This file exists so you can:
--   1) Open it in MySQL Workbench to visualize/reverse-engineer an ER diagram
--   2) Run it manually if you ever set ddl-auto to "validate" or "none"
--   3) Understand the schema at a glance
-- ============================================================

CREATE DATABASE IF NOT EXISTS neuroforge_db;
USE neuroforge_db;

-- ---------------------------------------------------
-- USERS  (single table, role column instead of subclass
--         tables, per the ISA hierarchy in the ER diagram)
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name   VARCHAR(100) NOT NULL,
    email       VARCHAR(150) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,          -- BCrypt hash
    role        VARCHAR(30)  NOT NULL,           -- ADMIN, PROJECT_MANAGER, BUSINESS_ANALYST,
                                                  -- SOFTWARE_ARCHITECT, UI_UX_DESIGNER,
                                                  -- SOFTWARE_DEVELOPER, QA, DEVOPS
    created_at  DATETIME NOT NULL
);

-- ---------------------------------------------------
-- PROJECTS  (MANAGES: Project Manager 1..N Project)
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS projects (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_name  VARCHAR(150) NOT NULL,
    description   VARCHAR(2000),
    start_date    DATE,
    end_date      DATE,
    status        VARCHAR(30) NOT NULL DEFAULT 'PLANNING', -- PLANNING, IN_PROGRESS, ON_HOLD, COMPLETED
    manager_id    BIGINT NOT NULL,
    created_at    DATETIME NOT NULL,
    CONSTRAINT fk_project_manager FOREIGN KEY (manager_id) REFERENCES users(id)
);

-- ---------------------------------------------------
-- REQUIREMENTS  (INCLUDES: Project 1..N Requirement)
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS requirements (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    title             VARCHAR(200) NOT NULL,
    description       VARCHAR(2000),
    technical_stack   VARCHAR(200),
    project_id        BIGINT NOT NULL,
    created_by        BIGINT NOT NULL,
    created_at        DATETIME NOT NULL,
    CONSTRAINT fk_requirement_project FOREIGN KEY (project_id) REFERENCES projects(id),
    CONSTRAINT fk_requirement_user    FOREIGN KEY (created_by) REFERENCES users(id)
);

-- ---------------------------------------------------
-- TASKS  (CONTAINS: Project 1..N Task, WORKS_ON: User N..N Task simplified to assignee)
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS tasks (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_name     VARCHAR(200) NOT NULL,
    description   VARCHAR(2000),
    status        VARCHAR(30) NOT NULL DEFAULT 'TODO', -- TODO, IN_PROGRESS, IN_REVIEW, DONE
    project_id    BIGINT NOT NULL,
    assigned_to   BIGINT,
    due_date      DATE,
    created_at    DATETIME NOT NULL,
    CONSTRAINT fk_task_project  FOREIGN KEY (project_id) REFERENCES projects(id),
    CONSTRAINT fk_task_assignee FOREIGN KEY (assigned_to) REFERENCES users(id)
);

-- ---------------------------------------------------
-- TEST_CASES  (HAS: Task 1..N TestCase, CREATES: QA 1..N TestCase)
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS test_cases (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    title             VARCHAR(200) NOT NULL,
    status            VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- PENDING, PASSED, FAILED
    no_of_test_cases  INT DEFAULT 1,
    task_id           BIGINT NOT NULL,
    created_by        BIGINT NOT NULL,
    created_at        DATETIME NOT NULL,
    CONSTRAINT fk_testcase_task FOREIGN KEY (task_id)    REFERENCES tasks(id),
    CONSTRAINT fk_testcase_user FOREIGN KEY (created_by) REFERENCES users(id)
);

-- ---------------------------------------------------
-- REPORTS  (GENERATES: TestCase 0..N Report, belongs to Project)
-- ---------------------------------------------------
CREATE TABLE IF NOT EXISTS reports (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    report_type    VARCHAR(100) NOT NULL,   -- BUG_REPORT, STATUS_REPORT, RISK_REPORT, ...
    bug_id         BIGINT,
    severity       VARCHAR(20),             -- LOW, MEDIUM, HIGH, CRITICAL
    description    VARCHAR(2000),
    test_case_id   BIGINT,
    project_id     BIGINT NOT NULL,
    generated_by   BIGINT NOT NULL,
    created_at     DATETIME NOT NULL,
    CONSTRAINT fk_report_testcase FOREIGN KEY (test_case_id) REFERENCES test_cases(id),
    CONSTRAINT fk_report_project  FOREIGN KEY (project_id)   REFERENCES projects(id),
    CONSTRAINT fk_report_user     FOREIGN KEY (generated_by) REFERENCES users(id)
);
