<!DOCTYPE html>
<html lang="en">


<body>

  <h1>Internal Developer Documentation</h1>

  <h2>Overview</h2>
  <p>This document provides a high-level guide to our web platform’s application structure, stack, key workflows, and tooling to support onboarding and internal development.</p>

  <h2>Application Structure</h2>
  <pre><code>├── backend
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
└── ssr</code></pre>

  <ul>
    <li><strong>backend/</strong> – Main service backend (Spring Boot).</li>
    <li><strong>frontend/</strong> – Node.js app for user-facing frontend using Pug and Bootstrap.</li>
    <li><strong>search/</strong> – Search indexing and querying service (OpenSearch).</li>
    <li><strong>ssr/</strong> – Server-side rendering support.</li>
    <li><strong>custom/</strong> – Shared business logic and utilities.</li>
    <li><strong>docker/</strong> – Container and deployment config.</li>
    <li><strong>src/</strong> – Legacy or support code.</li>
  </ul>

  <h2>Key Technologies</h2>
  <table>
    <tr><th>Layer</th><th>Stack</th></tr>
    <tr><td>Frontend</td><td>Node.js, Pug (template engine), Bootstrap, Tailwind CSS, SWR</td></tr>
    <tr><td>Backend</td><td>Spring Boot (Kotlin), JPA/Hibernate</td></tr>
    <tr><td>Search</td><td>OpenSearch (Java client)</td></tr>
    <tr><td>Database</td><td>MySQL</td></tr>
    <tr><td>Auth</td><td>OAuth2 + JWT</td></tr>
    <tr><td>Build</td><td>Maven (backend & modules), Yarn (frontend)</td></tr>
    <tr><td>Infra</td><td>NGINX, GitHub Actions, AWS</td></tr>
  </table>

  <h2>Key Workflows</h2>

  <h3>1. Authentication</h3>
  <ul>
    <li>Users authenticate via OAuth2 (Google, Microsoft)</li>
    <li>JWT tokens are issued and validated in the backend</li>
    <li>Frontend stores access tokens in HttpOnly cookies</li>
  </ul>

  <h3>2. Search</h3>
  <ul>
    <li>Search data is ingested by the <code>search</code> module</li>
    <li>Uses OpenSearch for full-text and fuzzy queries</li>
    <li>Backend communicates with OpenSearch using the Java High-Level REST Client</li>
  </ul>

  <h3>3. Rendering</h3>
  <ul>
    <li>Public pages are server-side rendered via <code>ssr/</code></li>
    <li>Authenticated areas are rendered client-side</li>
    <li>Shared state handled by React Context and SWR</li>
  </ul>

  <h3>4. Development Flow</h3>
  <ul>
    <li>Backend: <code>mvn spring-boot:run</code></li>
    <li>Frontend: <code>yarn dev</code></li>
    <li>Services run independently and communicate via REST</li>
  </ul>

  <h2>Environment Setup</h2>

  <h3>1. Clone Repo</h3>
  <pre><code>git clone https://github.com/USDA-REE-ARS/nal-lca-repo-application.git</code></pre>

  <h3>2. Clone and Build External Modules</h3>
  <pre><code>git clone https://github.com/GreenDelta/olca-modules.git
cd olca-modules && mvn clean install -DskipTests

git clone https://github.com/GreenDelta/search-wrapper.git
cd search-wrapper && mvn clean install -DskipTests</code></pre>

  <h3>3. Install Dependencies</h3>
  <pre><code>cd backend && mvn clean install
cd ../frontend && yarn install
cd ../search && mvn clean install</code></pre>

  <h3>4. Run Backend</h3>
  <pre><code>cd backend
mvn spring-boot:run</code></pre>

  <h3>5. Run Frontend</h3>
  <pre><code>cd frontend
yarn dev</code></pre>

  <h3>6. Run Search Service (if needed)</h3>
  <pre><code>cd search
mvn spring-boot:run</code></pre>

  <h2>Frontend Build (Optional)</h2>
  <p>To build frontend separately (usually handled as part of Maven build):</p>
  <pre><code>npm install
gulp
# Or with context:
node_modules/gulp/bin/gulp.js --contextPath=/lca-collaboration/ --appserver=prod --customDir=custom</code></pre>

  <h2>Tips</h2>
  <ul>
    <li>Use VSCode + IntelliJ for full IDE support across frontend/backend</li>
    <li>Ensure Java 17 and Node.js 18+ are installed</li>
    <li>Shared environment variables live in <code>.env</code></li>
  </ul>

  <h2>Contribution Guidelines</h2>
  <ul>
    <li>PRs must be reviewed by at least one team member</li>
    <li>Use conventional commits</li>
    <li>Run tests and linters before pushing</li>
  </ul>

  <h2>Contact</h2>
  <ul>
    <li>Slack: <code>#dev-internal</code></li>
    <li>Docs: <a href="https://your-docs-url">Confluence Page</a></li>
  </ul>

</body>
</html>
