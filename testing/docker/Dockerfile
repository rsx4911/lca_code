# Build the server WAR file
FROM maven:3.9.9-eclipse-temurin-21 AS builder

ARG VERSION

RUN apt-get update && \
    apt-get install -y nodejs npm zip unzip

WORKDIR /app

COPY ./ /app

# Install search-wrapper from GitHub source
# TODO This is a temporary workaround as the search-wrapper artifact is not 
#   available in Maven Central (to be deleted when released)
SHELL ["/bin/bash", "-o", "pipefail", "-c"]
RUN SW_VERSION=$(grep -A 1 '<artifactId>search-wrapper-os</artifactId>' search/pom.xml \
      | grep '<version>' \
      | awk -F '[<>]' '{print $3}') && \
    git clone --depth 1 --branch "$SW_VERSION" https://github.com/GreenDelta/search-wrapper.git search-wrapper && \
    mvn install -f search-wrapper/pom.xml

# Install olca-modules at cs-<version>-release tag
# TODO This is a temporary workaround as the olca-modules artifact is not
#   always published first on Maven Central (to be deleted when released)
RUN git clone --depth 1 --branch cs-"$VERSION"-release https://github.com/GreenDelta/olca-modules.git olca-modules && \
    mvn install -f olca-modules/pom.xml -DskipTests=true

RUN mvn clean compile

WORKDIR /app/frontend
RUN npm install && \
    npx gulp dev

WORKDIR /app
RUN mvn package

WORKDIR /
RUN mkdir -p /ROOT

RUN if ls /app/backend/target/*.war 1> /dev/null 2>&1; then \
        unzip /app/backend/target/*.war -d ROOT; \
    else \
        echo "No .war file found" && exit 1; \
    fi

# Create the runner image
FROM tomcat:10-jdk21 AS runner

# Set up the /opt/collab directories with limited permissions
RUN addgroup collab && \
    adduser --ingroup collab --disabled-login --shell /bin/sh collab && \
    mkdir -p /opt/collab/git /opt/collab/lib && \
    chown -R collab:collab /opt/collab && \
    chmod -R 750 /opt/collab

WORKDIR $CATALINA_HOME

RUN rm -rf webapps/*  && \
    mkdir -p conf/Catalina/localhost/ROOT work/Catalina/localhost/ROOT && \
    chown -R collab:collab webapps conf work && \
    chmod -R 750 conf work && \
    umask 027

# Copy the server files
COPY --from=builder ROOT webapps/ROOT
COPY ./docker/application.properties webapps/ROOT/WEB-INF/classes/application.properties
RUN chown -R collab:collab webapps/ROOT && \
    chmod -R 750 webapps/ROOT

USER collab

ENTRYPOINT ["catalina.sh", "run"]
