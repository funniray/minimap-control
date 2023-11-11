plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
}

val versionStr = (System.getenv("VERSION")?: "v1.0.0").removePrefix("v")

group = "com.funniray.minimap"
version = versionStr

repositories {
    mavenCentral()
    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    maven {
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
}

dependencies {
    // Main Dependencies
    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
    implementation(project(":common"))

    // Common Dependencies
    implementation("org.spongepowered:configurate-core:4.1.2")
    implementation("org.spongepowered:configurate-yaml:4.1.2")
    implementation("net.kyori:adventure-api:4.10.0")
    implementation("net.kyori:adventure-platform-bukkit:4.3.1")
    implementation("net.kyori:adventure-text-minimessage:4.10.0")
}

val javaTarget = 8
java {
    sourceCompatibility = JavaVersion.toVersion(javaTarget)
    targetCompatibility = JavaVersion.toVersion(javaTarget)
    if (JavaVersion.current() < JavaVersion.toVersion(javaTarget)) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(javaTarget))
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    shadowJar {
        relocate("dev.dewy.nbt", "com.funniray.minimap.nbt")
        relocate("org.spongepowered.configurate", "com.funniray.minimap.configurate")
        relocate("net.kyori", "com.funniray.minimap.kyori")
        relocate("io.leangen.geantyref", "com.funniray.minimap.geantyref")
        exclude("com/google/gson/**")
        exclude("org/apache/commons/**")
        exclude("org/yaml/snakeyaml/**")
        archiveBaseName.set("${rootProject.name}-spigot")
        archiveClassifier.set("")
        doLast {
            copy {
                from(archiveFile)
                into("${rootProject.projectDir}/build")
            }
        }
    }
}

bukkit {
    name = "MinimapControl"
    main = "com.funniray.minimap.spigot.SpigotMinimap"
    authors = listOf("funniray")
    description = "Control minimap settings from server-side software"

    apiVersion = "1.13"
}
