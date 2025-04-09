<p><strong>Internal Developer Documentation</strong><br />Overview<br />This document provides a high-level guide to our web platform&rsquo;s application structure, stack, key workflows, and tooling to support onboarding and internal development.</p>
<p><strong>Application Structure</strong><br />├── backend<br />│ ├── src<br />│ └── target<br />├── custom<br />├── docker<br />├── frontend<br />│ ├── custom<br />│ ├── external-libs<br />│ ├── node_modules<br />│ └── src<br />├── node_modules<br />├── search<br />│ ├── src<br />│ └── target<br />├── src<br />│ └── main<br />└── ssr</p>
<p>backend/ &ndash; Main service backend (Spring Boot).</p>
<p>frontend/ &ndash; Node.js app for user-facing frontend using Pug and Bootstrap.</p>
<p>search/ &ndash; Search indexing and querying service (OpenSearch).</p>
<p>ssr/ &ndash; Server-side rendering support.</p>
<p>custom/ &ndash; Shared business logic and utilities.</p>
<p>docker/ &ndash; Container and deployment nyarconfig.</p>
<p>src/ &ndash; Legacy or support code.</p>
<p><strong>Key Technologies</strong><br />Frontend: Node.js, Pug (template engine), Bootstrap, Tailwind CSS, SWR</p>
<p>Backend: Spring Boot, Kotlin, JPA/Hibernate</p>
<p>Search: OpenSearch (Java client)</p>
<p>Database: MySQL</p>
<p>Auth: OAuth2 + JWT</p>
<p>Build Tools: Maven (backend &amp; external modules)</p>
<p>Infrastructure: NGINX, GitHub Actions, AWS</p>
<p>Key Workflows<br /><strong>1. Authentication</strong><br />Users authenticate via OAuth2 (Google, Microsoft)</p>
<p>JWT tokens are issued and validated in the backend</p>
<p>Frontend stores access tokens in HttpOnly cookies</p>
<p><strong>2. Search</strong><br />Search data is ingested by the search module</p>
<p>Uses OpenSearch for full-text and fuzzy queries</p>
<p>Backend communicates with OpenSearch using the Java High-Level REST Client</p>
<p><strong>3. Rendering</strong><br />Public pages are server-side rendered via ssr/</p>
<p>Authenticated areas are rendered client-side</p>
<p>Shared state handled by React Context and SWR</p>
<p><strong>4. Development Flow
<p>Services run independently and communicate via REST</p>
<p><strong>Environment Setup</strong><br />Clone Repo:</p>
<p>git clone https://github.com/USDA-REE-ARS/nal-lca-repo-application.git<br />Clone and Build External Modules:</p>
<p>git clone https://github.com/GreenDelta/olca-modules.git<br />cd olca-modules &amp;&amp; mvn clean install -DskipTests</p>
<p>git clone https://github.com/GreenDelta/search-wrapper.git<br />cd search-wrapper &amp;&amp; mvn clean install -DskipTests</p>
<p><br /><strong>Frontend Build (Optional)</strong><br />To build frontend separately (usually handled as part of Maven build):</p>
<p>npm install<br />gulp<br /># Or with context:<br />node_modules/gulp/bin/gulp.js --contextPath=/lca-collaboration/ --appserver=prod --customDir=custom<br />Tips<br />Use VSCode + IntelliJ for full IDE support across frontend/backend</p>
<p>Ensure Java 17 and Node.js 18+ are installed</p>
<p>Shared environment variables live in .env</p>
