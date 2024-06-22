plugins {
    `java-library`
}

val versionStr = (System.getenv("VERSION")?: "v1.0.0").removePrefix("v")

group = "com.funniray.minimap"
version = versionStr

repositories {
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

val javaTarget = 8
java {
    sourceCompatibility = JavaVersion.toVersion(javaTarget)
    targetCompatibility = JavaVersion.toVersion(javaTarget)
    if (JavaVersion.current() < JavaVersion.toVersion(javaTarget)) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(javaTarget))
    }
}

dependencies {
    compileOnly("com.velocitypowered:velocity-api:3.1.1")
    compileOnly("io.github.waterfallmc:waterfall-api:1.20-R0.3-SNAPSHOT")
    annotationProcessor("com.velocitypowered:velocity-api:3.1.1")
}

tasks {
    jar {
        archiveBaseName.set("${rootProject.name}-proxy")
        doLast {
            copy {
                from(archiveFile)
                into("${rootProject.projectDir}/build")
            }
        }
    }

}
