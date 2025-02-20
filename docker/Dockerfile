FROM gradle:8.12.1-jdk23 AS build-env
USER root
# -- Add some basics
RUN apt-get update && apt-get install -y vim
# ------ Enable color prompt
ENV TERM=xterm-color
# ------ Make root's prompt red
RUN sed -i 's/01;32m/01;31m/g' /root/.bashrc
# ------ Timezone to HST
ENV TZ=HST10
# ------ User,workspace,shared gradle cache
USER gradle
# The gradle image delcares the GRADLE_USER_HOME dir as a volume
# which means the directory will be empty in subsequent steps.
# Using an alnernate location means we can share it with subseqent
# tasks to speed everything up.
#ENV GRADLE_USER_HOME=/home/gradle/workspace/.gradle/GRADLE_USER_HOME
ENV WORKSPACE=/home/gradle/workspace
RUN mkdir workspace
WORKDIR ${WORKSPACE}
# server
EXPOSE 8080
# jdwp remote debugging
EXPOSE 5005
# The INFO_VERSION environment variable will set the info.version spring parameter
# and the value will display in the actuator/info endpoint.
ENV INFO_VERSION=BUILD-ENVIRONMENT

FROM build-env AS test
# Only copy in what we need to so we don't break the cache unless needed.
COPY --chown=gradle:gradle ./gradle ./.gradle
COPY --chown=gradle:gradle ./src ./src
COPY --chown=gradle:gradle ./build.gradle.kts ./build.gradle.kts
COPY --chown=gradle:gradle ./settings.gradle.kts ./settings.gradle.kts
RUN gradle test

FROM test AS package
RUN gradle bootJar
# Match the artifact with the release container owner so the `COPY --from` will work.
# See https://github.com/UniversityOfHawaii/docker-at-uh/issues/30
USER root
RUN chown root:root ${WORKSPACE}/build/libs/casdemo-1.1.jar

FROM eclipse-temurin:23
WORKDIR /casdemo
COPY --from=package /home/gradle/workspace/build/libs/casdemo-1.1.jar .
# ------ Timezone to HST
ENV TZ=HST10
# The INFO_VERSION environment variable will set the info.version spring parameter
# and the value will display in the actuator/info endpoint.
ARG VERSION
ENV INFO_VERSION=$VERSION
CMD java -jar /casdemo/casdemo-1.1.jar
HEALTHCHECK \
     --interval=10s \
     --timeout=10s \
     --start-period=60s \
     --retries=3 \
     CMD wget \
           -O - \
           --quiet \
           localhost:8080/casdemo/actuator/health \
         | grep '"status":"UP"'
