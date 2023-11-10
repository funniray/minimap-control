plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

val javaTarget = 8 // Sponge targets a minimum of Java 17
java {
    sourceCompatibility = JavaVersion.toVersion(javaTarget)
    targetCompatibility = JavaVersion.toVersion(javaTarget)
    if (JavaVersion.current() < JavaVersion.toVersion(javaTarget)) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(javaTarget))
    }
}

dependencies {
    compileOnly("com.google.guava:guava:21.0")
    compileOnly("com.google.code.gson:gson:2.8.0")
    compileOnly("org.spongepowered:configurate-core:4.1.2")
    compileOnly("net.kyori:adventure-api:4.10.0")
    compileOnly("net.kyori:adventure-text-minimessage:4.10.0")

    implementation("dev.dewy:nbt:1.5.1")
}
