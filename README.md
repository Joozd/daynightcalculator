# DayNightCalculator

DayNightCalculator is a tool to calculate if it is day or night on a specific point on earth at a specific time and date, 
or how much time along a track with start and end positions and times is spent during day and night.

## Underlying calculations

Sun position calculations are done by [Shredzone's SunCalc](https://shredzone.org/maven/commons-suncalc/). See their project for more details about that.

Day/Night calculations along a route expect a uniform speed along the shortest(great circle) route. 

## Dependencies and Requirements

DayNightCalculator targets java 11.
It can also be used on Android, API level 26 (Android 8.0 "Oreo") or higher. (because it uses `java.time` which is supported from that version)

Even though this is a Kotlin project, it uses Java times and dates, for compatibility.

## Installation

Gradle: 

    repositories {
        // other repositories like mavenCentral()
        maven {
            url = uri("https://joozd.nl/nexus/repository/maven-releases/")
        }
    }
    dependencies {
        // DayNightCalculator
        implementation("nl.joozd.daynightcalculator:daynightcalculator:1.0")
    }

## License

DayNightCalculator is open source software. The source code and software may be used under the terms of [Apache License 2.0.](http://www.apache.org/licenses/LICENSE-2.0)
