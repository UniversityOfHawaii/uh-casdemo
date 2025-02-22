FROM eclipse-temurin:23 AS base-env
USER root
# libglib2.0-0 libnss3 libxcb1 are needed for chromedriver tests
RUN --mount=type=cache,target=/var/cache/apt,sharing=locked \
    --mount=type=cache,target=/var/lib/apt,sharing=locked \
    apt-get update && apt-get --no-install-recommends install -y libglib2.0-0 libnss3 libxcb1
USER ubuntu
ENV TZ=HST10
RUN mkdir /home/ubuntu/workspace
WORKDIR /home/ubuntu/workspace

FROM base-env AS test

# The gradle image delcares the GRADLE_USER_HOME dir as a volume
# which makes mount caching it give permission erros. So we just use
# a different location that isn't a volume.
ENV GRADLE_USER_HOME=/home/ubuntu/gradle-user-home
USER root
RUN mkdir /home/ubuntu/gradle-user-home && chown ubuntu:ubuntu /home/ubuntu/gradle-user-home
USER ubuntu
RUN ls -l /home/ubuntu

# Only copy in what we need to so we don't break the cache unless needed.
COPY --chown=ubuntu:ubuntu ./src ./src
COPY --chown=ubuntu:ubuntu ./.eslintrc.json ./.eslintrc.json
COPY --chown=ubuntu:ubuntu ./.jshintrc ./.jshintrc

COPY --chown=ubuntu:ubuntu ./gradle ./gradle
COPY --chown=ubuntu:ubuntu ./build.gradle.kts ./build.gradle.kts
COPY --chown=ubuntu:ubuntu ./gradlew ./gradlew
COPY --chown=ubuntu:ubuntu ./settings.gradle.kts ./settings.gradle.kts
RUN --mount=type=cache,target=/home/ubuntu/gradle-user-home,uid=1000,gid=1000 ./gradlew --no-daemon test

COPY --chown=ubuntu:ubuntu ./pom.xml ./pom.xml
COPY --chown=ubuntu:ubuntu ./mvnw ./mvnw
COPY --chown=ubuntu:ubuntu ./.mvn ./.mvn
RUN --mount=type=cache,target=/home/ubuntu/.m2,uid=1000,gid=1000 ./mvnw clean test -Djs-tests.skip=true

FROM test AS package
RUN --mount=type=cache,target=/home/ubuntu/gradle-user-home,uid=1000,gid=1000 ./gradlew --no-daemon bootJar

FROM base-env as deploy
COPY --from=package /home/ubuntu/workspace/build/libs/casdemo-1.1.jar /home/ubuntu/workspace/casdemo-1.1.jar
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
