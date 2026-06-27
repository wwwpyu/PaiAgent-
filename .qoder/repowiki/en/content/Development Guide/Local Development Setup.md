# Local Development Setup

<cite>
**Referenced Files in This Document**
- [pom.xml](file://backend/pom.xml)
- [application.yml](file://backend/src/main/resources/application.yml)
- [schema.sql](file://backend/src/main/resources/schema.sql)
- [migration_add_engine_type.sql](file://backend/src/main/resources/migration_add_engine_type.sql)
- [PaiAgentApplication.java](file://backend/src/main/java/com/paiagent/PaiAgentApplication.java)
- [package.json](file://frontend/package.json)
- [vite.config.ts](file://frontend/vite.config.ts)
- [tsconfig.json](file://frontend/tsconfig.json)
- [tailwind.config.js](file://frontend/tailwind.config.js)
- [eslint.config.js](file://frontend/eslint.config.js)
- [.gitignore (backend)](file://backend/.gitignore)
- [.gitignore (frontend)](file://frontend/.gitignore)
- [README.md (root)](file://README.md)
- [README.md (backend)](file://backend/README.md)
</cite>

## Table of Contents
1. [Introduction](#introduction)
2. [Prerequisites](#prerequisites)
3. [Environment Variables](#environment-variables)
4. [Database Setup](#database-setup)
5. [Backend Setup](#backend-setup)
6. [Frontend Setup](#frontend-setup)
7. [IDE Configuration](#ide-configuration)
8. [Development Server Startup](#development-server-startup)
9. [Verification Steps](#verification-steps)
10. [Troubleshooting Guide](#troubleshooting-guide)
11. [Conclusion](#conclusion)

## Introduction
This guide provides a complete, step-by-step local development environment setup for the PaiAgent project. It covers prerequisites, environment configuration, database initialization, backend and frontend setup, IDE recommendations, debugging, and verification steps to ensure a smooth development experience.

## Prerequisites
Ensure your system meets the following requirements before proceeding:
- Java 21+ (OpenJDK or Oracle JDK recommended)
- Node.js 18+ (with npm)
- MySQL 8.0+ (database server)
- Maven 3.8+ (Java project build tool)

These requirements are derived from the project's configuration and documentation.

**Section sources**
- [README.md (root): Environment requirements:286-296](file://README.md#L286-L296)
- [pom.xml: Java version property](file://backend/pom.xml#L30)
- [backend/README.md: Quick start prerequisites:1-12](file://backend/README.md#L1-L12)

## Environment Variables
Configure the following environment variables for development:
- OPENAI_API_KEY: Set this to your OpenAI-compatible API key. The backend configuration supports dynamic override via environment variables.

Notes:
- The backend configuration expects an environment variable named OPENAI_API_KEY for the OpenAI client.
- Ensure the environment variable is set in your shell profile or IDE run configuration.

**Section sources**
- [application.yml: OpenAI API key placeholder and environment override](file://backend/src/main/resources/application.yml#L18)

## Database Setup
Follow these steps to prepare the MySQL database:

1. Create the database
- Connect to MySQL as root and create the database:
  - Command: mysql -u root -p
  - SQL: CREATE DATABASE paiagent DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

2. Initialize schema
- Import the schema SQL script:
  - Command: mysql -u root -p paiagent < backend/src/main/resources/schema.sql
- Alternatively, execute the SQL statements manually from schema.sql.

3. Configure connection details
- Edit backend/src/main/resources/application.yml to update:
  - spring.datasource.url
  - spring.datasource.username
  - spring.datasource.password

4. Optional: Add engine type column
- If upgrading or enabling LangGraph4j support, apply the migration:
  - Command: mysql -u root -p paiagent < backend/src/main/resources/migration_add_engine_type.sql

Database schema highlights:
- workflow: Stores workflow definitions and metadata.
- node_definition: Defines supported node types and their JSON schemas.
- execution_record: Tracks execution history and results.

**Section sources**
- [schema.sql: Database creation and table definitions:1-84](file://backend/src/main/resources/schema.sql#L1-L84)
- [migration_add_engine_type.sql: Engine type column migration:1-17](file://backend/src/main/resources/migration_add_engine_type.sql#L1-L17)
- [application.yml: Database connection settings:7-11](file://backend/src/main/resources/application.yml#L7-L11)
- [README.md (root): Database setup instructions:306-335](file://README.md#L306-L335)

## Backend Setup
The backend is a Spring Boot 3.4.1 application built with Java 21 and Maven.

Steps:
1. Navigate to the backend directory
- cd backend

2. Build and run the application
- Option A: Use Maven wrapper
  - Command: ./mvnw spring-boot:run
- Option B: Use IDE
  - Run the main class com.paiagent.PaiAgentApplication

3. Verify backend startup
- On successful startup, you should see a message indicating the application started.
- Access the API documentation at http://localhost:8080/swagger-ui.html

Backend configuration highlights:
- Port: 8080
- Datasource: MySQL configured via application.yml
- OpenAPI/Swagger: Enabled for API documentation
- MinIO: Optional object storage configuration included

**Section sources**
- [PaiAgentApplication.java: Main application entry point:1-16](file://backend/src/main/java/com/paiagent/PaiAgentApplication.java#L1-L16)
- [application.yml: Server and datasource configuration:1-55](file://backend/src/main/resources/application.yml#L1-L55)
- [pom.xml: Dependencies and plugin configuration:60-131](file://backend/pom.xml#L60-L131)
- [backend/README.md: Backend quick start:13-48](file://backend/README.md#L13-L48)

## Frontend Setup
The frontend is a React 18 + TypeScript application using Vite 6.0.1.

Steps:
1. Navigate to the frontend directory
- cd frontend

2. Install dependencies
- Command: npm install

3. Start the development server
- Command: npm run dev
- The frontend will be available at http://localhost:5173

Frontend configuration highlights:
- Vite configuration with React plugin
- TypeScript configuration with app/node references
- Tailwind CSS for styling
- ESLint configuration for code quality

**Section sources**
- [package.json: Scripts and dependencies:1-40](file://frontend/package.json#L1-L40)
- [vite.config.ts: Vite configuration:1-8](file://frontend/vite.config.ts#L1-L8)
- [tsconfig.json: TypeScript project references:1-8](file://frontend/tsconfig.json#L1-L8)
- [tailwind.config.js: Tailwind setup:1-13](file://frontend/tailwind.config.js#L1-L13)
- [eslint.config.js: ESLint configuration:1-29](file://frontend/eslint.config.js#L1-L29)

## IDE Configuration
Recommended IDEs and configurations:
- IntelliJ IDEA
  - Enable annotation processing for Lombok
  - Set up Spring Boot run configuration pointing to the main class
  - Configure environment variables (e.g., OPENAI_API_KEY) in the run configuration
- Visual Studio Code
  - Extensions: Prettier, ESLint, Tailwind CSS IntelliSense
  - Configure tasks for backend and frontend
  - Set environment variables in launch.json for debugging

General recommendations:
- Backend: Ensure Java 21 SDK is selected and Maven wrapper is enabled.
- Frontend: Ensure Node.js 18+ is selected and npm is configured.

**Section sources**
- [.gitignore (backend): IDE-specific exclusions:16-30](file://backend/.gitignore#L16-L30)
- [.gitignore (frontend): IDE-specific exclusions:15-24](file://frontend/.gitignore#L15-L24)

## Development Server Startup
Start both backend and frontend servers concurrently:

Backend:
- Terminal 1: cd backend && ./mvnw spring-boot:run
- Access: http://localhost:8080

Frontend:
- Terminal 2: cd frontend && npm install && npm run dev
- Access: http://localhost:5173

Optional: API documentation
- Swagger UI: http://localhost:8080/swagger-ui.html

**Section sources**
- [README.md (root): Combined startup instructions:336-372](file://README.md#L336-L372)
- [backend/README.md: Backend access URL:45-47](file://backend/README.md#L45-L47)

## Verification Steps
Confirm successful setup with these checks:

Backend verification:
- Application logs show successful startup
- Swagger UI loads at http://localhost:8080/swagger-ui.html
- Database connectivity verified by successful table queries

Frontend verification:
- Browser opens http://localhost:5173
- No build errors in the terminal
- React DevTools shows components rendering

End-to-end verification:
- Default credentials: admin / 123
- Login to the frontend and navigate to workflow editor
- Create a simple workflow and trigger execution to observe logs

**Section sources**
- [README.md (root): Default login credentials:373-376](file://README.md#L373-L376)
- [backend/README.md: Default account:49-52](file://backend/README.md#L49-L52)

## Troubleshooting Guide
Common issues and resolutions:

Database connectivity:
- Symptom: Application fails to connect to MySQL
  - Check spring.datasource configuration in application.yml
  - Verify MySQL is running and accepts connections
  - Confirm database exists and schema is imported

Port conflicts:
- Symptom: Ports 8080 or 5173 already in use
  - Change server.port in application.yml (backend)
  - Adjust Vite port in vite.config.ts (frontend) or use --port flag

Environment variables:
- Symptom: OpenAI-related operations fail
  - Ensure OPENAI_API_KEY is exported in your shell or set in IDE run configuration

Build failures:
- Symptom: Maven or npm build errors
  - Clear local caches: mvn clean or npm cache clean --force
  - Reinstall dependencies: npm install after cleaning

IDE-specific issues:
- Symptom: Annotation processing errors in IntelliJ
  - Enable annotation processing in compiler settings
  - Invalidate caches and restart

**Section sources**
- [application.yml: Database and API configuration:7-55](file://backend/src/main/resources/application.yml#L7-L55)
- [README.md (root): Environment requirements and setup:286-372](file://README.md#L286-L372)

## Conclusion
You now have a fully configured local development environment for PaiAgent. With Java 21, Node.js 18+, MySQL 8.0+, and Maven 3.8+ installed, the database initialized, and both backend and frontend servers running, you can develop, debug, and iterate efficiently. Use the verification steps to confirm everything works as expected, and refer to the troubleshooting guide for common issues.