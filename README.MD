lca-collaboration
========
This repository contains the source code of the LCA Collaboration Server application. It is a servlet web application build with Google Guice and JAX-RS web services, secured with Shiro. This project depends on some of the [olca-modules](https://github.com/GreenDelta/olca-modules) project which is a plain [Maven](http://maven.apache.org/) project that contains the core functionalities (e.g. the model, database access, calculations, and data exchange) of openLCA. 

Building from source
--------------------
To compile it from source you need to have the following tools installed:

* [Git](https://git-scm.com/) (optional)
* a [Java Development Kit 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [Maven](http://maven.apache.org/)
* [Node.js](https://nodejs.org/) and [Gulp](http://gulpjs.com/) (for building the HTML5 user interface components)

When you have these tools installed you can build the application from source via the following steps:

####Install the openLCA core modules
The core modules contain the openLCA application logic that is independent from the collaboration server logic. These modules are plain Maven projects and can be installed via `mvn install`. See the [olca-modules](https://github.com/GreenDelta/olca-modules) repository for a more information.

####Get the source code of the application
We recommend that to use Git to manage the source code but you can also download the source code as a zip file. Create a development directory (the path should not contain whitespaces):

    mkdir lca-collaboration
    cd lca-collaboration

and get the source code:

    git clone [repository-url]

####Building the HTML pages
To build the HTML pages of the user interface, install the Node.js modules via [npm](https://www.npmjs.com/) (npm is a package manager that comes with your Node.js installation):

    npm install

This will create a folder `lca-collaboration/node_modules` with the dependent modules. After this, you can create the html package via Gulp:

    gulp

This will build and the HTML files.

####Prepare the Eclipse workspace
Download the current Eclipse package for JEE developers (to have everything together you can extract it into your development directory). Create a workspace directory in your development directory (e.g. under the eclipse folder to have a clean structure):

    eclipse
      ...
      workspace
    lca-collaboration
      .git
      ...

To create the relevant project files for eclipse run:

	mvn eclipse:eclipse
	
After this, open Eclipse and select the created workspace directory. Import the project into Eclipse via `Import/General/Existing Projects into Workspace`
(select the lca-collaboration directory). You should now see the project in your Eclipse workspace.

####Build the web application for deployment
To build the web application to be deployed on a web server (e.g. tomcat) run:

	mvn package

The file 'lca-collaboration.war' in the target sub directory is the deployable application.