import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("plugin.jpa") version "1.4.32"
    id("org.springframework.boot") version "2.6.1"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.6.0"
    kotlin("plugin.spring") version "1.6.0"
}

group = "com.ead"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

repositories {
    mavenCentral()
}

dependencies {
    implementations(
        "org.springframework.boot:spring-boot-starter-web",
        "org.springframework.boot:spring-boot-starter-data-jpa",
        "org.springframework.boot:spring-boot-starter-validation",
        "com.fasterxml.jackson.module:jackson-module-kotlin",
        "org.jetbrains.kotlin:kotlin-reflect",
        "org.jetbrains.kotlin:kotlin-stdlib-jdk8",
        "io.github.microutils:kotlin-logging-jvm:2.1.20",
    )

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks {

    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }

    withType<Test> {
        useJUnitPlatform()
    }
}

fun DependencyHandlerScope.implementations(vararg all: Any) {
    for (impl in all) implementation(impl)
}


