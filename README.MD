# LCA Collaboration Server

This repository contains the source code of the LCA Collaboration Server application. It is a servlet web application build with Google Guice and JAX-RS web services, secured with Shiro. This project depends on some of the [olca-modules](https://github.com/GreenDelta/olca-modules) project which is a plain [Maven](http://maven.apache.org/) project that contains the core functionalities (e.g. the model, database access, calculations, and data exchange) of openLCA. 

## Building from source
To compile it from source you need to have the following tools installed:

* [Git](https://git-scm.com/) (optional)
* a [Java Development Kit 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [Maven](http://maven.apache.org/)
* [Node.js](https://nodejs.org/) and [Gulp](http://gulpjs.com/) (for building the HTML5 user interface components)

When you have these tools installed you can build the application from source via the following steps:

#### Install the openLCA core modules
The core modules contain the openLCA application logic that is independent from the collaboration server logic. These modules are plain Maven projects and can be installed via `mvn install`. See the [olca-modules](https://github.com/GreenDelta/olca-modules) repository for a more information.

#### Get the source code of the application
We recommend that to use Git to manage the source code but you can also download the source code as a zip file. Create a development directory (the path should not contain whitespaces):

```bash
mkdir lca-collaboration
cd lca-collaboration
```

and get the source code:

```bash
git clone https://github.com/GreenDelta/lca-collaboration
```

#### Building the HTML pages
To build the HTML pages of the user interface, install the Node.js modules via [npm](https://www.npmjs.com/) (npm is a package manager that comes with your Node.js installation):

```
npm install
```

This will create a folder `lca-collaboration/node_modules` with the dependent modules. After this, you can create the html package via Gulp:

```bash
gulp
```

This will build and the HTML files.

#### Prepare the Eclipse workspace
Download the current Eclipse package for JEE developers (to have everything together you can extract it into your development directory). Create a workspace directory in your development directory (e.g. under the eclipse folder to have a clean structure):

    eclipse
      ...
      workspace
    lca-collaboration
      .git
      ...

To create the relevant project files for eclipse run:

```bash
mvn eclipse:eclipse
```
	
After this, open Eclipse and select the created workspace directory. Import the project into Eclipse via `Import/General/Existing Projects into Workspace`
(select the lca-collaboration directory). You should now see the project in your Eclipse workspace.

#### Build the web application for deployment
To build the web application to be deployed on a web server (e.g. tomcat) run:

```bash
mvn package
```

The file 'lca-collaboration.war' in the target sub directory is the deployable application.

## Installation

#### Requirements
In order to install the application a Java Runtime Environment >= 8 and a servlet container (e.g. tomcat 8) needs to be installed.

As server hardware, we recommend to configure elasticsearch with at least 2GB heap space, the same goes for the servlet container. This is not the minimum requirements, but rather a recommendation for use in production for a moderate amount of simultaneous requests.

#### Elasticsearch
Before deploying the application to the servlet container you will need to set up elasticsearch. For more information on the elasticsearch installation, please take a look at the [official documentation](https://www.elastic.co/guide/en/elasticsearch/reference/current/setup.html).

#### Collaboration Server
After installing and starting elasticsearch you will need to create the Search Index as well as the LCA Collaboration Server database (Derby) and data directories. The easiest way to do this is to use the Installer application. It is a simple Java JAR, run it e.g. with java -jar lca-collaboration-installer.jar

The application will ask for the relevant configuration (These values can be reconfigured by the admin user once the application is running) – Directories (incl. parent directories) are not required to exist before running the installer. It will create the directories itself. If existing, the directories must be empty, otherwise the creation is skipped. 

**Note**: The OS user which is used for running tomcat needs sufficient rights on the directories. If you are e.g. using a non-root user you will need to change the owner of the directories to the tomcat user. (e.g. chown tomcat8:tomcat8 /opt/collab -R on ubuntu with tomcat 8 installed via apt)
Prompted input:
-	A directory to create the internal database in (e.g. /opt/collab/database)
-	A directory where the data sets for each repository will be stored (e.g. /opt/collab/repositories)
-	A directory for “refId” lists for protected libraries (e.g. /opt/collab/libraries)
-	The elasticsearch cluster name (e.g. elasticsearch on a fresh installation)
-	The elasticsearch host address (e.g. localhost if on the same machine)
-	The index name (e.g. lca-collaboration)
-	An email address for the initially created admin user
-	A password for the admin user

These initial settings or additional optional settings can be configured in the admin area. You can access it by clicking the wrench icon in the top left (if you have sufficient rights).

##### Error Reporting
The Collaboration Server uses log4j2 for logging. In addition to write server-side log files, log4j2 can be configured to send error emails to a specified email account. These emails can either be send from the local server directly or via an external email account. You will need to replace the file /WEB-INF/classes/log4j2.xml in the built war-file. The following is an example configuration with an external email account. For more information see the log4j2 

**Note**: If you want to directly report errors  to us, please use error@greendelta.com as 'to' attribute and append your company or institution name to the 'subject' attribute.

**Important**: The username and email address of the user encountering the error, will be added to the email. If you do not want this information to be send to us, please configure the logging to send the emails to your email address first and forward the email - cleared from personal information - to us.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
	<Appenders>
		<Console name="Console" target="SYSTEM_OUT">
			<PatternLayout pattern="%d{yy-MM-dd HH:mm:ss:SSS} %-5p %c:%L - %m%n" />
		</Console>
		<SMTP name="Mail" 
        	from="some-account@your-company.com" 
            to="error@your-company.com" 
            subject="Error on LCA Collaboration Server"
        	smtpPassword="the-password" 
            smtpUsername="some-account@your-company.com" 
            smtpProtocol="smtps"
            smtpPort="465" 
            smtpHost="your-email-host-address" />
</Appenders>
	<Loggers>
		<Root level="info">
			<AppenderRef ref="Console" level="info" />
			<AppenderRef ref="Mail" level="error"/>
		</Root>
	</Loggers>
</Configuration>
```
