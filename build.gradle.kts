plugins {
    id("org.springframework.boot") version "3.4.2"
    java
    jacoco
    `maven-publish`
    war
}

apply(plugin = "io.spring.dependency-management")

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.security:spring-security-cas")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-devtools")
    implementation("org.springframework:spring-jdbc")
    implementation("org.springframework:spring-orm")
    implementation("org.springframework:spring-tx")
    implementation("com.h2database:h2")
    implementation("org.apache.commons:commons-dbcp2")
    implementation("org.springframework.security:spring-security-cas")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
    implementation("org.springframework:spring-test")
    implementation("org.springframework.security:spring-security-test")
    implementation("org.webjars:webjars-locator-core")
    implementation("org.webjars:bootstrap:5.2.3")
    implementation("org.webjars.npm:popperjs__core:2.11.8")
    implementation("org.webjars:jquery:3.4.1")
    implementation("org.webjars:angularjs:1.5.8")
    implementation("org.webjars.bower:angular-ui-grid:4.0.6")
    implementation("org.webjars:font-awesome:4.7.0")
    implementation("org.webjars:html5shiv:3.7.3")
    implementation("org.webjars:respond:1.4.2")
    implementation("org.webjars.bower:angular-mocks:1.5.8")
    testImplementation("org.webjars:jasmine:2.2.0")
}

group = "edu.hawaii.its"
version = "1.1"
description = "casdemo"
java.sourceCompatibility = JavaVersion.VERSION_23

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.jar {
    enabled = true
    // Remove 'plain' postfix from file name.
    archiveClassifier.set("")
}

tasks.war {
    enabled = true
    // Remove 'plain' postfix from file name.
    archiveClassifier.set("")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    classDirectories.setFrom(
        sourceSets.main.get().output.asFileTree.matching {
            exclude("**/configuration/SpringBootWebApplication.class")
        }
    )
}

var profiles = "localhost" // Default profile, override as needed.
tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
    args("--spring.profiles.active=$profiles")
}

tasks.register("hints") {
    doLast {
        printHints()
    }
}
tasks.register("info") {
    doLast {
        printHints()
    }
}
tasks.help {
    doLast {
        printHints()
    }
}

/**
 * Print some commonly used commands as hints.
 */
fun printHints() {
    println("\nHere are some common app tasks for this project:")

    // First, build the shared libraries:
    println("  $ ./gradlew publishToMavenLocal")

    // To run a specific test method:
    println("  $ ./gradlew test --tests StringsTest.trunctate")

    // To run an app the test profile:
    println("  $ ./gradlew bootRun --args='--spring.profiles.active=test'")

    // Build the war.
    println("  $ ./gradlew war")

    // To run Jacoco test coverage reports:
    println("  $ ./gradlew jacocoTestReport")
}
