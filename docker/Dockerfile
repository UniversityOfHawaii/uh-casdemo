FROM eclipse-temurin:23 AS base-env
USER ubuntu
ENV TZ=HST10
RUN mkdir /home/ubuntu/workspace
WORKDIR /home/ubuntu/workspace

FROM base-env AS test

USER ubuntu
# Only copy in what we need to so we don't break the cache unless needed.
COPY --chown=ubuntu:ubuntu ./.eslintrc.json ./.eslintrc.json
COPY --chown=ubuntu:ubuntu ./.jshintrc ./.jshintrc

COPY --chown=ubuntu:ubuntu ./gradle ./gradle
COPY --chown=ubuntu:ubuntu ./build.gradle.kts ./build.gradle.kts
COPY --chown=ubuntu:ubuntu ./gradlew ./gradlew
COPY --chown=ubuntu:ubuntu ./settings.gradle.kts ./settings.gradle.kts

COPY --chown=ubuntu:ubuntu ./pom.xml ./pom.xml
COPY --chown=ubuntu:ubuntu ./mvnw ./mvnw
COPY --chown=ubuntu:ubuntu ./.mvn ./.mvn

# Download dependencies before copying in the src so changes to the src won't trigger a redownload.
RUN ./gradlew dependencies --no-daemon
RUN ./mvnw dependency:go-offline && \
    ./mvnw dependency:resolve -Dclassifier=sources -DincludeScope=compile && \
    ./mvnw dependency:resolve -Dclassifier=sources -DincludeScope=test

COPY --chown=ubuntu:ubuntu ./src ./src

RUN ./gradlew --no-daemon test
RUN ./mvnw test -Djs-tests.skip=true


FROM test AS package
RUN  ./gradlew --no-daemon bootJar
# Match the artifact with the release container owner so the `COPY --from` will work.
# See https://github.com/UniversityOfHawaii/docker-at-uh/issues/30
USER root
RUN chown root:root /home/ubuntu/workspace/build/libs/casdemo-1.1.jar

FROM base-env as deploy
COPY --from=package --chown=ubuntu:ubuntu /home/ubuntu/workspace/build/libs/casdemo-1.1.jar /home/ubuntu/workspace/casdemo-1.1.jar
CMD java -jar /home/ubuntu/workspace/casdemo-1.1.jar
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
