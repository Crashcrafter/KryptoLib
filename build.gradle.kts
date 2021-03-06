import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.30"
    `maven-publish`
}

group = "dev.crash"
version = "0.1"

repositories {
    mavenCentral()
    mavenLocal()
}

apply(plugin = "maven-publish")

dependencies {
    api("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.0-rc2")
    api("org.bouncycastle:bcprov-jdk15to18:1.69")
    api("org.java-websocket:Java-WebSocket:1.5.2")
    api("org.slf4j:slf4j-api:2.0.0-alpha4")
    api("org.slf4j:slf4j-simple:2.0.0-alpha4")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "16"
}

publishing {
    publications {
        create<MavenPublication>("maven"){
            groupId = "dev.crash"
            artifactId = "kryptoLib"
            version = "0.1"
        }
    }
}

tasks.withType<PublishToMavenLocal> {
    doLast {
        project.file("/build/libs/KryptoLib-$version.jar").copyTo(file("C:\\Users\\${System.getProperties()["user.name"]}\\.m2\\repository\\dev\\crash\\kryptoLib\\0.1\\KryptoLib-$version.jar"), overwrite = true)
    }
}