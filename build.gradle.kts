plugins {
    java
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.github.spotbugs") version "6.0.18"
    id("com.avast.gradle.docker-compose") version "0.17.0"
}

buildscript {
    dependencies {
        classpath("com.palantir.javaformat:gradle-palantir-java-format:2.63.0")
    }
    repositories {
        mavenCentral()
    }
}

apply(plugin = "com.palantir.java-format")

group = "com.popoletos"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(22)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("io.micrometer:micrometer-core")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("org.springframework:spring-webmvc")

    implementation("com.bucket4j:bucket4j_jdk17-core:8.14.0")

    implementation(project(":ggauth-annotations-processor"))
    annotationProcessor(project(":ggauth-annotations-processor"))

    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.8")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")

}

dockerCompose {
    isRequiredBy(tasks.named("bootRun"))
    startedServices.set(listOf("prometheus", "grafana"))
    removeOrphans.set(true)
}

spotbugs {
    toolVersion
    toolVersion.set("4.8.6")
    effort.set(com.github.spotbugs.snom.Effort.DEFAULT)
    reportLevel.set(com.github.spotbugs.snom.Confidence.DEFAULT)
    showProgress.set(true)
}

tasks.withType<com.github.spotbugs.snom.SpotBugsTask>().configureEach {
    reports.create("html") {
        required.set(true)
        setStylesheet("fancy-hist.xsl")
    }
}

tasks.withType<Test> {
    jvmArgs("-XX:+EnableDynamicAgentLoading")
    useJUnitPlatform()
}

tasks.register<Exec>("buildContainerFromCompose", fun Exec.() {
    println("-----Building Container Image from docker-compose file-----")
    commandLine("docker", "compose", "build", "app")
})

tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
    val varName = "SERVER_VERSION"
    val defaultValue = "Developer"

    val currentValue = System.getenv(varName)
    if (currentValue.isNullOrEmpty()) {
        println("$varName not set. Using default value: $defaultValue")
        environment(varName, defaultValue)
    } else {
        println("$varName set to: $currentValue")
    }
}