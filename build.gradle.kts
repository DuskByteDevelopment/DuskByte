plugins {
    id("net.fabricmc.fabric-loom") version "1.15.5"
    id("com.github.gmazzo.buildconfig") version "6.0.9"
}

val modId = project.property("mod_id").toString()
val minecraftVersion = project.property("minecraft_version").toString()
val javaVersion = project.property("java_version").toString()
val fabricVersion = project.property("fabric_version").toString()
val fabricLoaderVersion = project.property("fabric_loader_version").toString()

base {
    archivesName.set("${modId}-${minecraftVersion}")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(javaVersion))
    withSourcesJar()
}

sourceSets {
    main {
        java.srcDirs("common/src/main/java")
        resources.srcDirs("common/src/main/resources")
    }
}

buildConfig {
    packageName("com.github.duskbyte")
    useJavaOutput()
    buildConfigField("String", "MOD_ID", "\"${modId}\"")
    buildConfigField("String", "VERSION", "\"${project.version}\"")
}

dependencies {
    minecraft("com.mojang:minecraft:${minecraftVersion}")
    implementation("net.fabricmc:fabric-loader:${fabricLoaderVersion}")
    implementation("net.fabricmc.fabric-api:fabric-api:${fabricVersion}")
    compileOnly(group = "com.google.code.findbugs", name = "jsr305", version = "3.0.2")
}

loom {
    val aw = file("common/src/main/resources/${modId}.accesswidener")
    if (aw.exists()) {
        accessWidenerPath.set(aw)
    }
    runs {
        named("client") {
            client()
            configName = "Fabric Client"
            ideConfigGenerated(true)
            runDir("runs/client")
        }
        named("server") {
            server()
            configName = "Fabric Server"
            ideConfigGenerated(true)
            runDir("runs/server")
        }
    }
}

tasks.named<ProcessResources>("processResources") {
    filesMatching("fabric.mod.json") {
        expand(mutableMapOf(
            "version" to project.version,
            "mod_id" to modId,
            "minecraft_version" to minecraftVersion,
            "fabric_loader_version" to fabricLoaderVersion,
            "java_version" to javaVersion
        ))
    }
    inputs.properties(mutableMapOf(
        "version" to project.version.toString(),
        "mod_id" to modId,
        "minecraft_version" to minecraftVersion,
        "fabric_loader_version" to fabricLoaderVersion,
        "java_version" to javaVersion
    ))
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}
