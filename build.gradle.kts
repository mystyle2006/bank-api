plugins {
    id("org.springframework.boot") version "3.2.3"
    kotlin("jvm") version "2.2.20"
    kotlin("plugin.spring") version "1.8.0"

    id("io.spring.dependency-management") version "1.0.15.RELEASE"

    kotlin("plugin.serialization") version "1.8.0"
}

group = "org.inno"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web:3.2.3")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.2.3")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // reflect
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // client
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(17)
}