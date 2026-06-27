# Environment Setup

<cite>
**Referenced Files in This Document**
- [pom.xml](file://backend/pom.xml)
- [application.yml](file://backend/src/main/resources/application.yml)
- [schema.sql](file://backend/src/main/resources/schema.sql)
- [migration_add_engine_type.sql](file://backend/src/main/resources/migration_add_engine_type.sql)
- [MinioConfig.java](file://backend/src/main/java/com/paiagent/config/MinioConfig.java)
- [MinioService.java](file://backend/src/main/java/com/paiagent/service/MinioService.java)
- [PaiAgentApplication.java](file://backend/src/main/java/com/paiagent/PaiAgentApplication.java)
- [package.json](file://frontend/package.json)
- [vite.config.ts](file://frontend/vite.config.ts)
- [frontend README.md](file://frontend/README.md)
- [backend README.md](file://backend/README.md)
</cite>

## Table of Contents
1. [Introduction](#introduction)
2. [Prerequisites and System Requirements](#prerequisites-and-system-requirements)
3. [Step-by-Step Installation](#step-by-step-installation)
4. [Environment Variables and Configuration](#environment-variables-and-configuration)
5. [Database Initialization](#database-initialization)
6. [MinIO Setup](#minio-setup)
7. [Development Environment Setup](#development-environment-setup)
8. [Production Preparation](#production-preparation)
9. [Validation Steps](#validation-steps)
10. [Troubleshooting Guide](#troubleshooting-guide)
11. [Conclusion](#conclusion)

## Introduction
This guide provides end-to-end environment setup for the PaiAgent platform, covering prerequisites, system requirements, installation, configuration, and operational guidance for both development and production environments. It focuses on Java 21, Node.js, MySQL, and MinIO, and explains how to configure environment variables, initialize the database, and prepare the application for local and production use.

## Prerequisites and System Requirements
- Java 21: Required for building and running the backend service.
- Maven 3.8+: Used to build the backend module.
- Node.js and npm: Required for frontend development and builds.
- MySQL 8.0: Required for persistence.
- MinIO: Required for object storage (files, audio, etc.).
- Git: Recommended for cloning and version control.
- IDE: IntelliJ IDEA recommended for Java backend; VS Code recommended for frontend.

**Section sources**
- [backend README.md:7-11](file://backend/README.md#L7-L11)
- [pom.xml:29-35](file://backend/pom.xml#L29-L35)
- [package.json:1-40](file://frontend/package.json#L1-L40)

## Step-by-Step Installation
1. Install Java 21 and Maven 3.8+.
2. Install Node.js and npm.
3. Install and start MySQL 8.0.
4. Install and start MinIO.
5. Clone the repository and navigate to the project root.
6. Build the backend using Maven.
7. Install frontend dependencies using npm.
8. Start the backend and frontend services.

Notes:
- Backend build and run commands are provided in the backend README.
- Frontend build and run commands are provided in the frontend README.

**Section sources**
- [backend README.md:13-43](file://backend/README.md#L13-L43)
- [frontend README.md:17-37](file://frontend/README.md#L17-L37)

## Environment Variables and Configuration
The backend reads configuration from application.yml and supports environment overrides. Key areas:
- Database connection: JDBC URL, username, password.
- Jackson timezone and date format.
- OpenAI API configuration placeholder for dynamic node configuration.
- MyBatis-Plus mapper locations, type aliases, and logging.
- SpringDoc OpenAPI enablement and paths.
- MinIO endpoint, credentials, bucket, and public URL.

Important placeholders:
- OPENAI_API_KEY can be supplied via environment variable to override the placeholder value in configuration.

Configuration locations:
- Backend YAML configuration: [application.yml:1-55](file://backend/src/main/resources/application.yml#L1-L55)
- Application entrypoint: [PaiAgentApplication.java:1-16](file://backend/src/main/java/com/paiagent/PaiAgentApplication.java#L1-L16)

**Section sources**
- [application.yml:1-55](file://backend/src/main/resources/application.yml#L1-L55)
- [PaiAgentApplication.java:1-16](file://backend/src/main/java/com/paiagent/PaiAgentApplication.java#L1-L16)

## Database Initialization
Follow these steps to initialize the database:
1. Ensure MySQL is installed and running.
2. Initialize the schema by executing the provided SQL script.
3. Optionally apply the engine type migration if upgrading.

Initialization steps:
- Run the schema SQL to create the database and tables.
- Apply the migration script to add the engine_type column if needed.

Scripts:
- Schema: [schema.sql:1-84](file://backend/src/main/resources/schema.sql#L1-L84)
- Migration: [migration_add_engine_type.sql:1-17](file://backend/src/main/resources/migration_add_engine_type.sql#L1-L17)

Notes:
- The backend README provides the command to execute the schema script.
- The default datasource configuration expects a database named paiagent.

**Section sources**
- [backend README.md:15-23](file://backend/README.md#L15-L23)
- [schema.sql:1-84](file://backend/src/main/resources/schema.sql#L1-L84)
- [migration_add_engine_type.sql:1-17](file://backend/src/main/resources/migration_add_engine_type.sql#L1-L17)
- [application.yml:8-11](file://backend/src/main/resources/application.yml#L8-L11)

## MinIO Setup
The backend integrates with MinIO for object storage. Configuration is loaded from application.yml under the minio namespace and exposed via a configuration bean.

Key configuration fields:
- endpoint: MinIO server address.
- accessKey and secretKey: Credentials.
- bucketName: Target bucket.
- publicUrl: Publicly accessible URL prefix.

Behavior:
- The service ensures the bucket exists during uploads.
- Upload methods support streams, URLs, and byte arrays.
- Returns a public URL for uploaded objects.

Configuration and service references:
- MinIO configuration class: [MinioConfig.java:1-28](file://backend/src/main/java/com/paiagent/config/MinioConfig.java#L1-L28)
- MinIO service implementation: [MinioService.java:1-102](file://backend/src/main/java/com/paiagent/service/MinioService.java#L1-L102)
- YAML configuration: [application.yml:49-55](file://backend/src/main/resources/application.yml#L49-L55)

**Section sources**
- [MinioConfig.java:1-28](file://backend/src/main/java/com/paiagent/config/MinioConfig.java#L1-L28)
- [MinioService.java:1-102](file://backend/src/main/java/com/paiagent/service/MinioService.java#L1-L102)
- [application.yml:49-55](file://backend/src/main/resources/application.yml#L49-L55)

## Development Environment Setup
Backend:
- Use Java 21 and Maven to build and run the Spring Boot application.
- The main entrypoint is the application class annotated as Spring Boot startup.
- The backend exposes Swagger UI at a configured path after startup.

Frontend:
- Install dependencies using npm.
- Start the development server; the frontend runs on a separate port.
- The frontend’s API base URL is configured in the frontend code and points to the backend.

IDE recommendations:
- Backend: IntelliJ IDEA with Spring Boot support.
- Frontend: VS Code with React and TypeScript extensions.

References:
- Backend entrypoint: [PaiAgentApplication.java:1-16](file://backend/src/main/java/com/paiagent/PaiAgentApplication.java#L1-L16)
- Frontend scripts and ports: [frontend README.md:17-31](file://frontend/README.md#L17-L31)
- Frontend Vite config: [vite.config.ts:1-8](file://frontend/vite.config.ts#L1-L8)
- Frontend package scripts: [package.json:6-11](file://frontend/package.json#L6-L11)

**Section sources**
- [PaiAgentApplication.java:1-16](file://backend/src/main/java/com/paiagent/PaiAgentApplication.java#L1-L16)
- [frontend README.md:17-31](file://frontend/README.md#L17-L31)
- [vite.config.ts:1-8](file://frontend/vite.config.ts#L1-L8)
- [package.json:6-11](file://frontend/package.json#L6-L11)

## Production Preparation
Security hardening:
- Change default credentials for MySQL and MinIO.
- Set secure passwords and restrict network access to databases and MinIO.
- Configure HTTPS for both backend and MinIO in production deployments.
- Restrict API exposure and enforce authentication.

Performance optimization:
- Tune JVM heap and GC settings for Java 21 based on workload.
- Enable connection pooling and tune MySQL connection pool sizes.
- Configure MinIO for optimal throughput and disk layout.
- Use reverse proxy (e.g., Nginx) to serve frontend static assets and handle TLS termination.

Operational notes:
- The backend uses Spring Boot defaults; consider adding production-specific profiles and externalized configuration.
- Ensure logs are centralized and monitored.

[No sources needed since this section provides general guidance]

## Validation Steps
After completing setup:
- Confirm backend starts and Swagger UI is accessible at the configured path.
- Verify database connectivity using the configured JDBC URL and credentials.
- Test MinIO connectivity and confirm bucket creation/uploader behavior.
- Validate frontend loads and communicates with the backend API.

Reference paths:
- Backend startup and Swagger path: [backend README.md:37-47](file://backend/README.md#L37-L47)
- Frontend dev server and base URL: [frontend README.md:25-31](file://frontend/README.md#L25-L31)

**Section sources**
- [backend README.md:37-47](file://backend/README.md#L37-L47)
- [frontend README.md:25-31](file://frontend/README.md#L25-L31)

## Troubleshooting Guide
Common issues and resolutions:
- Java version mismatch: Ensure Java 21 is installed and used by Maven.
- MySQL connection failures: Verify host, port, database name, username, and password in configuration.
- MinIO connectivity errors: Confirm endpoint, access keys, and bucket name match the running MinIO instance.
- Port conflicts: Adjust backend server.port or frontend dev server port if ports are in use.
- Missing dependencies: Reinstall Node.js/npm packages and rebuild frontend.
- OpenAI API key placeholder: Set OPENAI_API_KEY environment variable to enable LLM nodes.

Helpful references:
- Backend property and dependency versions: [pom.xml:29-35](file://backend/pom.xml#L29-L35)
- Frontend dependencies and scripts: [package.json:12-38](file://frontend/package.json#L12-L38)
- MinIO configuration and service behavior: [MinioConfig.java:10-27](file://backend/src/main/java/com/paiagent/config/MinioConfig.java#L10-L27), [MinioService.java:26-102](file://backend/src/main/java/com/paiagent/service/MinioService.java#L26-L102)

**Section sources**
- [pom.xml:29-35](file://backend/pom.xml#L29-L35)
- [package.json:12-38](file://frontend/package.json#L12-L38)
- [MinioConfig.java:10-27](file://backend/src/main/java/com/paiagent/config/MinioConfig.java#L10-L27)
- [MinioService.java:26-102](file://backend/src/main/java/com/paiagent/service/MinioService.java#L26-L102)

## Conclusion
By following this guide, you will have installed and configured the backend and frontend services, initialized the database, set up MinIO, and prepared both development and production environments. Use the validation steps to confirm everything is working and consult the troubleshooting section for common issues.

[No sources needed since this section summarizes without analyzing specific files]