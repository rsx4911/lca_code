<b>Please note this is a manual build and deploy process. We hope to retire this process shortly.</b>

The source code is built from GreenDelta, a company in Germany. Their repository link is listed below. They are the creators and owners of the LCA application.

https://github.com/greendelta

We currnetly use LCA Stage to build our code. Please use the code tab above for more detail about the code mix.

Please pull the following code libraries:

https://github.com/GreenDelta/olca-modules
https://github.com/GreenDelta/search-wrapper

Currently these are the only dependencies needed to build the Commons. The code tab above will provide the main repository. This will reflect code pushed from GreenDelta.

The pull the primary code repository: https://github.com/USDA-REE-ARS/nal-lca-repo-application

Here is what I have under my local directory. Please note the reposirtoires I stated above in the below image.

AS WITH ALL NEW CODE PLEASE PULL THE DEPENDECNY UPDATES BEFORE TRYING TO BUILD THE NEW CODE.

Image

Build

We are currently using Apache Maven (https://maven.apache.org/) to build and deploy the Commons.

If building a depedency creates an issue then please include the skipTests flag. These are repsitory tests that were set by GreenDelta at some point.

When building olca-modules, use -DskipTests

mvn clean install -DskipTests

After the dependencies have been built. You can run the primary repository.

There are 2 profiles, Stage and Production. The respective commands are below.

mvn clean package -P appserver-stage
mvn clean package -P appserver-prod

The outcome should be a complied WAR file. This will be found in nal-lca-repo-application/backend/target.
Please rename the file to lca-collaboration.war. This file will be ready to deploy to the web server directory, i.e. /opt/tomcat/webapps.

Please stop tomcat, using systemctl. Systemctl is a containerzied service that runs on Linux. Please do your own research as the guts of Systemctl are little beyond this post.

Stop the web server.
systemctl stop tomcat

Manual move the WAR file.
/opt/tomcat/webapps

Then restart the web server.
systemctl restart tomcat

The code updates should be deployed now. You can check the udpated feature to verify.

So, to summarize:

git pull olca-modules/search-wrapper
mvn clean install -DskipTests olca modules/search-wrapper (you need to be in the project directory with the POM.xml file)
Then run the respective environment profile
mvn clean package -P appserver-(stage or prod)
Deploy the WAR (web archive) to the tomcat web server root
Manage the restart process with tomcat appropriately.
