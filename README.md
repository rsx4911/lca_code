Internal Developer Documentation
Overview
This document provides a high-level guide to our web platform’s application structure, stack, key workflows, and tooling to support onboarding and internal development.

Application Structure
├── backend
│   ├── src
│   └── target
├── custom
├── docker
├── frontend
│   ├── custom
│   ├── external-libs
│   ├── node_modules
│   └── src
├── node_modules
├── search
│   ├── src
│   └── target
├── src
│   └── main
└── ssr
backend/ – Main service backend (Spring Boot).

frontend/ – Node.js app for user-facing frontend using Pug and Bootstrap.

search/ – Search indexing and querying service (OpenSearch).

ssr/ – Server-side rendering support.

custom/ – Shared business logic and utilities.

docker/ – Container and deployment config.

src/ – Legacy or support code.

Key Technologies
Frontend: Node.js, Pug (template engine), Bootstrap, Tailwind CSS, SWR

Backend: Spring Boot, Kotlin, JPA/Hibernate

Search: OpenSearch (Java client)

Database: MySQL

Auth: OAuth2 + JWT

Build Tools: Maven (backend & external modules), Yarn (frontend)

Infrastructure: NGINX, GitHub Actions, AWS

Key Workflows
1. Authentication
Users authenticate via OAuth2 (Google, Microsoft)

JWT tokens are issued and validated in the backend

Frontend stores access tokens in HttpOnly cookies

2. Search
Search data is ingested by the search module

Uses OpenSearch for full-text and fuzzy queries

Backend communicates with OpenSearch using the Java High-Level REST Client

3. Rendering
Public pages are server-side rendered via ssr/

Authenticated areas are rendered client-side

Shared state handled by React Context and SWR

4. Development Flow
Backend: mvn spring-boot:run

Frontend: yarn dev

Services run independently and communicate via REST

Environment Setup
Clone Repo:

git clone https://github.com/USDA-REE-ARS/nal-lca-repo-application.git
Clone and Build External Modules:

git clone https://github.com/GreenDelta/olca-modules.git
cd olca-modules && mvn clean install -DskipTests

git clone https://github.com/GreenDelta/search-wrapper.git
cd search-wrapper && mvn clean install -DskipTests
Install Dependencies:

cd backend && mvn clean install
cd ../frontend && yarn install
cd ../search && mvn clean install
Run Backend:

cd backend
mvn spring-boot:run
Run Frontend:

cd frontend
yarn dev
Run Search Service (if needed):

cd search
mvn spring-boot:run
Frontend Build (Optional)
To build frontend separately (usually handled as part of Maven build):

npm install
gulp
# Or with context:
node_modules/gulp/bin/gulp.js --contextPath=/lca-collaboration/ --appserver=prod --customDir=custom
Tips
Use VSCode + IntelliJ for full IDE support across frontend/backend

Ensure Java 17 and Node.js 18+ are installed

Shared environment variables live in .env
