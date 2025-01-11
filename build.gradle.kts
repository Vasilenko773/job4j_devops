plugins {
    checkstyle
    java
    jacoco
    id("org.springframework.boot") version libs.versions.springBoot.get()
    id("io.spring.dependency-management") version "1.1.6"
    id("com.github.spotbugs") version "6.0.26"
}

group = "ru.job4j.devops"
version = "1.0.0"

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.8".toBigDecimal()
            }
        }

        rule {
            isEnabled = false
            element = "CLASS"
            includes = listOf("org.gradle.*")

            limit {
                counter = "LINE"
                value = "TOTALCOUNT"
                maximum = "0.3".toBigDecimal()
            }
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    implementation(libs.springWeb)
    testImplementation(libs.springTest)
    testRuntimeOnly(libs.junitPlatformLauncher)
    testImplementation(libs.junitJupiter)
    testImplementation(libs.assertjCore)
}

checkstyle {
    checkstyle.configFile = rootProject.file("config/checkstyle/checkstyle.xml")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.javadoc {
    destinationDir = rootProject.file("build/docs/javadoc")
}

tasks.register<Zip>("zipJavaDoc") {
    group = "documentation" // Группа, в которой будет отображаться задача
    description = "Packs the generated Javadoc into a zip archive"

    dependsOn("javadoc") // Указываем, что задача зависит от выполнения javadoc

    from(tasks.javadoc.get().destinationDir) // Путь к результатам Javadoc
    archiveFileName.set("javadoc.zip")
    destinationDirectory.set(layout.buildDirectory.dir("archives"))
}

tasks.spotbugsMain {
    reports.create("html") {
        required = true
        outputLocation.set(layout.buildDirectory.file("reports/spotbugs/spotbugs.html"))
    }
}

tasks.register("jarInfo") {
    group = "Build"
    description = "Warning about a fat jar file"
    dependsOn("jar")
    doLast {
        val jarSize = file("build/libs/DevOps-1.0.0.jar").length() / (1024 * 1024)
        if (jarSize > 5) {
            println("WARNING jar exceeds recommended size: ${jarSize} MB")
        }
        println("Current size jar file: ${jarSize} MB")
    }
}

tasks.test {
    finalizedBy(tasks.spotbugsMain)
}
