# LCA Collaboration Server

This repository contains the source code of the LCA Collaboration Server application. It is a servlet web application build with Spring Boot. This project depends on some of the [olca-modules](https://github.com/GreenDelta/olca-modules) project which is a plain [Maven](http://maven.apache.org/) project that contains the core functionalities (e.g. the model, database access, calculations, and data exchange) of openLCA. 

## Building from source
To compile it from source you need to have the following tools installed:

* [Git](https://git-scm.com/) (optional)
* a [Java Development Kit 17](https://openjdk.org/)
* [Maven](http://maven.apache.org/)
* [Node.js](https://nodejs.org/) and [Gulp](http://gulpjs.com/) (for building the HTML5 user interface components)

When you have these tools installed you can build the application from source via the following steps:

#### Install the openLCA core modules and search-wrapper modules
In addition the following modules are required and not distributed via maven central repository, therefore you need to download and build them manually:
These modules are plain Maven projects and can be installed via `mvn install`. See the respective repositories for a more information.
* [olca-modules](https://github.com/GreenDelta/olca-modules.git)
* [search-wrapper](https://github.com/GreenDelta/search-wrapper.git)
* [search-wrapper-os](https://github.com/GreenDelta/search-wrapper-os.git)
* [search-wrapper-os-rest](https://github.com/GreenDelta/search-wrapper-os-rest.git)

#### Get the source code of the application
We recommend that to use Git to manage the source code but you can also download the source code as a zip file. Create a development directory (the path should not contain whitespaces):

```bash
git clone https://git.greendelta.com/collaboration/collaboration-server
```

#### Building the HTML pages
To build the HTML pages of the user interface, install the Node.js modules via [npm](https://www.npmjs.com/) (npm is a package manager that comes with your Node.js installation):

```
npm install
```

This will create a folder `collaboration-server/node_modules` with the dependent modules. After this, you can create the html package via Gulp:

```bash
npx gulp build
```

This will build the HTML files.

#### Prepare the Eclipse workspace
Download the current Eclipse package for JEE developers (to have everything together you can extract it into your development directory). Create a workspace directory in your development directory (e.g. under the eclipse folder to have a clean structure):

    eclipse
      ...
      workspace
    collaboration-server
      .git
      ...

To create the relevant project files for eclipse run:

```bash
mvn eclipse:eclipse
```
	
After this, open Eclipse and select the created workspace directory. Import the project into Eclipse via `Import/General/Existing Projects into Workspace`
(select the collaboration-server directory). You should now see the project in your Eclipse workspace.

#### Configuring OAuth 2.0 providers
The LCA Collaboration Server includes support for OAuth 2.0 providers via Spring. The application scans for providers configured via the Spring application.properties and will add buttons - labeled "Continue with [client-name]" - in the login page, to redirect to the authentication provider automatically. The client-name will be read from the spring.security.oauth2.client.registration.[registration-id].client-name parameter. Note that for GitHub, Google and Facebook, the client-name - and all other required properties - are already provided by Spring's default configuration. If a user does not exist in the collaboration server, it will be added automatically, using the email and name from the provider, if the preferred_username attribute is not available, a username will be generated from the name and/or the email address. If no email address is specified, the login fails.

Below you can find some example configurations:


Github:

```
spring.security.oauth2.client.registration.github.client-id=your-github-client-id
spring.security.oauth2.client.registration.github.client-secret=your-github-client-secret
```

Google:

```
spring.security.oauth2.client.registration.google.client-id=your-google-client-id
spring.security.oauth2.client.registration.google.client-secret=your-google-client-secret
```

Facebook:

```
spring.security.oauth2.client.registration.facebook.client-id=your-facebook-client-id
spring.security.oauth2.client.registration.facebook.client-secret=your-facebook-client-secret
```

Keycloak:

```
spring.security.oauth2.client.registration.keycloak.client-id=your-keycloak-client-app-id
spring.security.oauth2.client.registration.keycloak.client-name=Keycloak
spring.security.oauth2.client.registration.keycloak.authorization-grant-type=authorization_code
spring.security.oauth2.client.registration.keycloak.scope=openid
spring.security.oauth2.client.provider.keycloak.user-name-attribute=preferred_username
spring.security.oauth2.client.provider.keycloak.issuer-uri=http://[keycloak-host-or-ip]:[keycloak-port]/realms/[KeyCloakRealmName]
# Example: http://localhost:8081/realms/SpringBootKeycloakRealm
```

#### Build the web application for deployment
To build the web application to be deployed on a web server (e.g. tomcat) run:

```bash
mvn clean compile
npx gulp build
mvn package
```

The file 'lca-collaboration-server-{version}_{date}.war' in the target sub directory is the deployable application.

## Installation

Please refer to the official installation instructions available on the [openLCA website](https://www.openlca.org/lca-collaboration-server-2-0-installation-guide/)