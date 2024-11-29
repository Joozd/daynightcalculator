plugins {
    kotlin("jvm") version "2.0.21"
    id("java-library")
    id("org.jetbrains.dokka") version "1.9.20"
    id("maven-publish")
}

val group = "nl.joozd.daynightcalculator"
val version = "1.0"

val sourceJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

tasks.register<Jar>("dokkaHtmlJar") {
    dependsOn(tasks.dokkaHtml)
    from(tasks.dokkaHtml.flatMap { it.outputDirectory })
    archiveClassifier.set("html-docs")
}

tasks.register<Jar>("dokkaJavadocJar") {
    dependsOn(tasks.dokkaJavadoc)
    from(tasks.dokkaJavadoc.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
}

// publish to my nexus repo
publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            groupId = group
            artifactId = "daynightcalculator"
            version = version

            artifact(sourceJar.get()) // Attach the source JAR

            // Attach the Dokka HTML documentation JAR
            artifact(tasks.named("dokkaHtmlJar").get())

            // Attach the Dokka Javadoc JAR
            artifact(tasks.named("dokkaJavadocJar").get())


            pom {
                licenses {
                    license {
                        name.set("Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
                        distribution.set("repo")
                    }
                }
            }
        }
    }
    repositories {
        maven {
            url = uri("https://joozd.nl/nexus/repository/maven-releases/")
            credentials {
                username = (findProperty("nexusUsername") ?: System.getenv("NEXUS_USERNAME") ?: "").toString()
                password = (findProperty("nexusPassword") ?: System.getenv("NEXUS_PASSWORD") ?: "").toString()
            }
        }
    }
}

// generate Documentation
tasks.dokkaHtml {
    outputDirectory.set(layout.buildDirectory.dir("docs"))

    dokkaSourceSets {
        named("main") {
            // Set module and package options as needed
            moduleName.set("DayNightCalculator")
            includes.from("Module.md")
            reportUndocumented.set(true) // Warn if something is not documented
            jdkVersion.set(11) // Target JDK version
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.shredzone.commons:commons-suncalc:3.11")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(11)
}