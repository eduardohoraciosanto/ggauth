plugins {
    id("java")
}

group = "com.popoletos"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework:spring-web:6.1.12")
    implementation("com.google.auto.service:auto-service-annotations:1.1.1")

    annotationProcessor("com.google.auto.service:auto-service:1.1.1")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    testImplementation("com.google.testing.compile:compile-testing:0.21.0")
}

tasks.test {
    useJUnitPlatform()
}