# Frontend Configuration

<cite>
**Referenced Files in This Document**
- [package.json](file://frontend/package.json)
- [vite.config.ts](file://frontend/vite.config.ts)
- [tsconfig.json](file://frontend/tsconfig.json)
- [tsconfig.app.json](file://frontend/tsconfig.app.json)
- [tsconfig.node.json](file://frontend/tsconfig.node.json)
- [tailwind.config.js](file://frontend/tailwind.config.js)
- [postcss.config.js](file://frontend/postcss.config.js)
- [eslint.config.js](file://frontend/eslint.config.js)
- [src/utils/request.ts](file://frontend/src/utils/request.ts)
- [src/main.tsx](file://frontend/src/main.tsx)
- [src/index.css](file://frontend/src/index.css)
- [src/App.tsx](file://frontend/src/App.tsx)
- [src/store/authStore.ts](file://frontend/src/store/authStore.ts)
</cite>

## Table of Contents
1. [Introduction](#introduction)
2. [Project Structure](#project-structure)
3. [Core Components](#core-components)
4. [Architecture Overview](#architecture-overview)
5. [Detailed Component Analysis](#detailed-component-analysis)
6. [Dependency Analysis](#dependency-analysis)
7. [Performance Considerations](#performance-considerations)
8. [Troubleshooting Guide](#troubleshooting-guide)
9. [Conclusion](#conclusion)

## Introduction
This document provides comprehensive frontend configuration documentation for the project's Vite-powered React application. It covers build configuration, TypeScript settings, Tailwind CSS setup, environment variables, proxy configurations for API requests, build optimization settings, development server configuration, package dependencies and scripts, linting rules, and deployment considerations. Guidance is included for customizing the build process and troubleshooting common frontend build issues.

## Project Structure
The frontend is organized under the frontend directory with the following key configuration and source files:
- Build and tooling: Vite configuration, TypeScript configurations, ESLint configuration, PostCSS/Tailwind configuration
- Application entry points: React root and application shell
- Networking: Axios-based HTTP client with interceptors
- State management: Zustand store for authentication
- Styling: Tailwind CSS integration via PostCSS

```mermaid
graph TB
subgraph "Build and Tooling"
VITE["vite.config.ts"]
TS_ROOT["tsconfig.json"]
TS_APP["tsconfig.app.json"]
TS_NODE["tsconfig.node.json"]
ESLINT["eslint.config.js"]
POSTCSS["postcss.config.js"]
TAILWIND["tailwind.config.js"]
end
subgraph "Application"
MAIN["src/main.tsx"]
APP["src/App.tsx"]
INDEX_CSS["src/index.css"]
REQUEST["src/utils/request.ts"]
AUTH_STORE["src/store/authStore.ts"]
end
VITE --> MAIN
TS_ROOT --> TS_APP
TS_ROOT --> TS_NODE
POSTCSS --> TAILWIND
INDEX_CSS --> TAILWIND
REQUEST --> APP
AUTH_STORE --> APP
```

**Diagram sources**
- [vite.config.ts:1-8](file://frontend/vite.config.ts#L1-L8)
- [tsconfig.json:1-8](file://frontend/tsconfig.json#L1-L8)
- [tsconfig.app.json:1-27](file://frontend/tsconfig.app.json#L1-L27)
- [tsconfig.node.json:1-25](file://frontend/tsconfig.node.json#L1-L25)
- [eslint.config.js:1-29](file://frontend/eslint.config.js#L1-L29)
- [postcss.config.js:1-6](file://frontend/postcss.config.js#L1-L6)
- [tailwind.config.js:1-13](file://frontend/tailwind.config.js#L1-L13)
- [src/main.tsx:1-11](file://frontend/src/main.tsx#L1-L11)
- [src/App.tsx:1-24](file://frontend/src/App.tsx#L1-L24)
- [src/index.css:1-8](file://frontend/src/index.css#L1-L8)
- [src/utils/request.ts:1-49](file://frontend/src/utils/request.ts#L1-L49)
- [src/store/authStore.ts:1-31](file://frontend/src/store/authStore.ts#L1-L31)

**Section sources**
- [package.json:1-40](file://frontend/package.json#L1-L40)
- [vite.config.ts:1-8](file://frontend/vite.config.ts#L1-L8)
- [tsconfig.json:1-8](file://frontend/tsconfig.json#L1-L8)
- [tsconfig.app.json:1-27](file://frontend/tsconfig.app.json#L1-L27)
- [tsconfig.node.json:1-25](file://frontend/tsconfig.node.json#L1-L25)
- [eslint.config.js:1-29](file://frontend/eslint.config.js#L1-L29)
- [postcss.config.js:1-6](file://frontend/postcss.config.js#L1-L6)
- [tailwind.config.js:1-13](file://frontend/tailwind.config.js#L1-L13)
- [src/main.tsx:1-11](file://frontend/src/main.tsx#L1-L11)
- [src/App.tsx:1-24](file://frontend/src/App.tsx#L1-L24)
- [src/index.css:1-8](file://frontend/src/index.css#L1-L8)
- [src/utils/request.ts:1-49](file://frontend/src/utils/request.ts#L1-L49)
- [src/store/authStore.ts:1-31](file://frontend/src/store/authStore.ts#L1-L31)

## Core Components
- Vite configuration: Minimal React plugin setup for JSX transform and fast refresh
- TypeScript configuration: Split tsconfig.app.json for application code and tsconfig.node.json for tooling/Vite config
- Tailwind CSS: Content scanning paths and PostCSS pipeline integration
- ESLint: TypeScript-aware linting with React Hooks and React Refresh plugins
- HTTP client: Axios instance with base URL, timeout, interceptors, and auth token injection
- Routing and UI: React Router DOM with Ant Design provider and locale configuration

**Section sources**
- [vite.config.ts:1-8](file://frontend/vite.config.ts#L1-L8)
- [tsconfig.app.json:1-27](file://frontend/tsconfig.app.json#L1-L27)
- [tsconfig.node.json:1-25](file://frontend/tsconfig.node.json#L1-L25)
- [tailwind.config.js:1-13](file://frontend/tailwind.config.js#L1-L13)
- [postcss.config.js:1-6](file://frontend/postcss.config.js#L1-L6)
- [eslint.config.js:1-29](file://frontend/eslint.config.js#L1-L29)
- [src/utils/request.ts:1-49](file://frontend/src/utils/request.ts#L1-L49)
- [src/App.tsx:1-24](file://frontend/src/App.tsx#L1-L24)

## Architecture Overview
The frontend architecture integrates Vite for development and build, TypeScript for type safety, Tailwind CSS for styling, and React for UI composition. Axios handles HTTP communication with centralized interceptors for authentication and error handling. Zustand manages lightweight application state, and Ant Design provides UI components with locale support.

```mermaid
graph TB
CLIENT["Browser"]
VITE_DEV["Vite Dev Server"]
VITE_BUILD["Vite Build"]
REACT["React Runtime"]
AXIOS["Axios Instance<br/>Interceptors"]
AUTH["Zustand Auth Store"]
ROUTER["React Router DOM"]
TAILWIND["Tailwind CSS"]
CLIENT --> VITE_DEV
CLIENT --> VITE_BUILD
VITE_DEV --> REACT
VITE_BUILD --> REACT
REACT --> ROUTER
REACT --> AUTH
REACT --> AXIOS
AXIOS --> TAILWIND
```

**Diagram sources**
- [vite.config.ts:1-8](file://frontend/vite.config.ts#L1-L8)
- [src/main.tsx:1-11](file://frontend/src/main.tsx#L1-L11)
- [src/App.tsx:1-24](file://frontend/src/App.tsx#L1-L24)
- [src/utils/request.ts:1-49](file://frontend/src/utils/request.ts#L1-L49)
- [src/store/authStore.ts:1-31](file://frontend/src/store/authStore.ts#L1-L31)
- [tailwind.config.js:1-13](file://frontend/tailwind.config.js#L1-L13)

## Detailed Component Analysis

### Vite Configuration
- Purpose: Configure the development server and build pipeline with React plugin
- Plugins: React plugin for JSX transform and fast refresh
- Extensibility: Add aliases, environment variables, and build optimization flags as needed

```mermaid
flowchart TD
Start(["Load vite.config.ts"]) --> LoadPlugins["Load React Plugin"]
LoadPlugins --> ExportConfig["Export Vite Config"]
ExportConfig --> DevServer["Development Server"]
ExportConfig --> Build["Build Pipeline"]
```

**Diagram sources**
- [vite.config.ts:1-8](file://frontend/vite.config.ts#L1-L8)

**Section sources**
- [vite.config.ts:1-8](file://frontend/vite.config.ts#L1-L8)

### TypeScript Configuration
- Root tsconfig.json: References app and node configurations
- tsconfig.app.json: Targets modern browsers, bundler module resolution, strictness, and JSX transform
- tsconfig.node.json: Targets Node tooling, bundler module resolution, and strictness

```mermaid
flowchart TD
Root["tsconfig.json"] --> App["tsconfig.app.json"]
Root --> NodeCfg["tsconfig.node.json"]
App --> Compiler["Compiler Options<br/>Target, Module, JSX, Strict"]
NodeCfg --> Tooling["Tooling Options<br/>Target, Module, Strict"]
```

**Diagram sources**
- [tsconfig.json:1-8](file://frontend/tsconfig.json#L1-L8)
- [tsconfig.app.json:1-27](file://frontend/tsconfig.app.json#L1-L27)
- [tsconfig.node.json:1-25](file://frontend/tsconfig.node.json#L1-L25)

**Section sources**
- [tsconfig.json:1-8](file://frontend/tsconfig.json#L1-L8)
- [tsconfig.app.json:1-27](file://frontend/tsconfig.app.json#L1-L27)
- [tsconfig.node.json:1-25](file://frontend/tsconfig.node.json#L1-L25)

### Tailwind CSS Setup
- Content paths: Scans index.html and all TypeScript/JavaScript files under src
- Theme extension: Empty extension point for future customization
- PostCSS pipeline: Uses @tailwindcss/postcss plugin
- Global styles: Imports Tailwind directives in index.css

```mermaid
flowchart TD
TailwindCfg["tailwind.config.js"] --> ContentScan["Content Paths Scan"]
PostCSS["postcss.config.js"] --> Plugin["@tailwindcss/postcss"]
IndexCSS["src/index.css"] --> ImportTw["Import Tailwind Directives"]
ContentScan --> Build["Build/Dev Pipeline"]
Plugin --> Build
ImportTw --> Build
```

**Diagram sources**
- [tailwind.config.js:1-13](file://frontend/tailwind.config.js#L1-L13)
- [postcss.config.js:1-6](file://frontend/postcss.config.js#L1-L6)
- [src/index.css:1-8](file://frontend/src/index.css#L1-L8)

**Section sources**
- [tailwind.config.js:1-13](file://frontend/tailwind.config.js#L1-L13)
- [postcss.config.js:1-6](file://frontend/postcss.config.js#L1-L6)
- [src/index.css:1-8](file://frontend/src/index.css#L1-L8)

### ESLint Configuration
- Extends: Recommended rules for JavaScript and TypeScript
- Plugins: React Hooks, React Refresh
- Globals: Browser globals enabled
- Rules: Enforces hooks rules and restricts export components for refresh

```mermaid
flowchart TD
ESLintCfg["eslint.config.js"] --> Extends["Extend Recommended Rules"]
ESLintCfg --> Plugins["Plugins: React Hooks, React Refresh"]
ESLintCfg --> Globals["Enable Browser Globals"]
ESLintCfg --> Rules["Hooks and Refresh Rules"]
```

**Diagram sources**
- [eslint.config.js:1-29](file://frontend/eslint.config.js#L1-L29)

**Section sources**
- [eslint.config.js:1-29](file://frontend/eslint.config.js#L1-L29)

### HTTP Client and Proxy Configuration
- Axios instance: Base URL configured to a local backend endpoint
- Interceptors:
  - Request: Injects Authorization header from localStorage token
  - Response: Handles 401 by clearing token and redirecting to login
- Proxy considerations: No Vite proxy is configured; adjust baseURL for different environments

```mermaid
sequenceDiagram
participant C as "Component"
participant R as "Axios Instance"
participant S as "Backend Server"
C->>R : "HTTP Request"
R->>R : "Add Authorization Header (if present)"
R->>S : "Forward Request"
S-->>R : "Response"
R->>R : "Strip Response Data"
alt "401 Unauthorized"
R->>R : "Clear Token"
R->>C : "Reject with Error"
else "Success"
R-->>C : "Return Data"
end
```

**Diagram sources**
- [src/utils/request.ts:1-49](file://frontend/src/utils/request.ts#L1-L49)

**Section sources**
- [src/utils/request.ts:1-49](file://frontend/src/utils/request.ts#L1-L49)

### Application Entry Points and Routing
- Entry point: Creates React root and renders the App component
- App shell: Configures Ant Design locale, React Router routes, and protected/public routes
- Authentication store: Manages token, username, and authentication state with localStorage persistence

```mermaid
sequenceDiagram
participant Browser as "Browser"
participant Main as "main.tsx"
participant App as "App.tsx"
participant Router as "React Router DOM"
participant Store as "authStore.ts"
Browser->>Main : "Load"
Main->>App : "Render"
App->>Router : "Define Routes"
Router->>Store : "Check Authentication"
alt "Authenticated"
Router-->>Browser : "Show Editor/Login"
else "Not Authenticated"
Router-->>Browser : "Redirect to Login"
end
```

**Diagram sources**
- [src/main.tsx:1-11](file://frontend/src/main.tsx#L1-L11)
- [src/App.tsx:1-24](file://frontend/src/App.tsx#L1-L24)
- [src/store/authStore.ts:1-31](file://frontend/src/store/authStore.ts#L1-L31)

**Section sources**
- [src/main.tsx:1-11](file://frontend/src/main.tsx#L1-L11)
- [src/App.tsx:1-24](file://frontend/src/App.tsx#L1-L24)
- [src/store/authStore.ts:1-31](file://frontend/src/store/authStore.ts#L1-L31)

### Package Dependencies and Scripts
- Scripts:
  - dev: Start Vite development server
  - build: Run TypeScript project references then Vite build
  - lint: Run ESLint
  - preview: Preview built bundle locally
- Dependencies: React, React DOM, React Router DOM, Ant Design, Axios, Zustand, and React Flow
- Dev Dependencies: Vite, React plugin, TypeScript, Tailwind CSS, PostCSS, ESLint, and related plugins

**Section sources**
- [package.json:1-40](file://frontend/package.json#L1-L40)

## Dependency Analysis
The frontend configuration exhibits low coupling and high cohesion:
- Vite depends on the React plugin for JSX transform
- TypeScript configurations are decoupled via references
- Tailwind relies on PostCSS and content scanning
- Axios encapsulates HTTP concerns independently of routing/state
- Zustand provides isolated state management

```mermaid
graph LR
VITE["vite.config.ts"] --> REACT_PLUGIN["@vitejs/plugin-react"]
TS_ROOT["tsconfig.json"] --> TS_APP["tsconfig.app.json"]
TS_ROOT --> TS_NODE["tsconfig.node.json"]
POSTCSS["postcss.config.js"] --> TAILWIND["tailwind.config.js"]
REQUEST["src/utils/request.ts"] -.->|independent| ROUTER["src/App.tsx"]
AUTH["src/store/authStore.ts"] -.->|consumes| ROUTER
```

**Diagram sources**
- [vite.config.ts:1-8](file://frontend/vite.config.ts#L1-L8)
- [tsconfig.json:1-8](file://frontend/tsconfig.json#L1-L8)
- [tsconfig.app.json:1-27](file://frontend/tsconfig.app.json#L1-L27)
- [tsconfig.node.json:1-25](file://frontend/tsconfig.node.json#L1-L25)
- [postcss.config.js:1-6](file://frontend/postcss.config.js#L1-L6)
- [tailwind.config.js:1-13](file://frontend/tailwind.config.js#L1-L13)
- [src/utils/request.ts:1-49](file://frontend/src/utils/request.ts#L1-L49)
- [src/App.tsx:1-24](file://frontend/src/App.tsx#L1-L24)
- [src/store/authStore.ts:1-31](file://frontend/src/store/authStore.ts#L1-L31)

**Section sources**
- [vite.config.ts:1-8](file://frontend/vite.config.ts#L1-L8)
- [tsconfig.json:1-8](file://frontend/tsconfig.json#L1-L8)
- [tsconfig.app.json:1-27](file://frontend/tsconfig.app.json#L1-L27)
- [tsconfig.node.json:1-25](file://frontend/tsconfig.node.json#L1-L25)
- [postcss.config.js:1-6](file://frontend/postcss.config.js#L1-L6)
- [tailwind.config.js:1-13](file://frontend/tailwind.config.js#L1-L13)
- [src/utils/request.ts:1-49](file://frontend/src/utils/request.ts#L1-L49)
- [src/App.tsx:1-24](file://frontend/src/App.tsx#L1-L24)
- [src/store/authStore.ts:1-31](file://frontend/src/store/authStore.ts#L1-L31)

## Performance Considerations
- Build optimization: Leverage Vite’s native tree-shaking and on-demand compilation. Consider adding external libraries to optimize chunking if bundle size grows.
- Asset optimization: Enable compression in production builds and configure asset hashing if needed.
- Browser compatibility: Target modern browsers aligned with React and TypeScript settings; avoid polyfills unless legacy support is required.
- Development server: Use Vite’s fast refresh and minimal dev server overhead; disable unnecessary plugins in development.

## Troubleshooting Guide
Common issues and resolutions:
- Axios baseURL mismatch: Update the base URL in the HTTP client to match the backend endpoint for the current environment.
- 401 errors during development: Verify token storage and interceptor logic; ensure tokens are persisted and refreshed appropriately.
- Tailwind classes not applied: Confirm content paths in Tailwind config include all relevant files and rebuild after changes.
- TypeScript errors in Vite: Ensure bundler module resolution and JSX settings align with Vite; verify tsconfig references are intact.
- ESLint errors: Address hook and refresh rules; confirm TypeScript files are included in linting scope.

**Section sources**
- [src/utils/request.ts:1-49](file://frontend/src/utils/request.ts#L1-L49)
- [tailwind.config.js:1-13](file://frontend/tailwind.config.js#L1-L13)
- [tsconfig.app.json:1-27](file://frontend/tsconfig.app.json#L1-L27)
- [eslint.config.js:1-29](file://frontend/eslint.config.js#L1-L29)

## Conclusion
The frontend configuration establishes a modern, type-safe, and efficient development environment using Vite, TypeScript, Tailwind CSS, and React. The setup supports rapid iteration with robust linting and a clean HTTP client with interceptors. By understanding the configuration files and their relationships, teams can customize the build process, integrate environment-specific settings, and troubleshoot issues effectively.