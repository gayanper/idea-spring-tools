import org.jetbrains.intellij.tasks.PatchPluginXmlTask
import org.jetbrains.intellij.tasks.PrepareSandboxTask
import org.jetbrains.intellij.tasks.RunIdeTask
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.io.ByteArrayOutputStream

plugins {
    id("org.jetbrains.intellij") version "1.9.0"
    kotlin("jvm") version "1.7.10"
    id("net.researchgate.release") version "2.8.1"
}

group = "org.gap.ijplugins.spring.ideaspringtools"

if(version.toString().endsWith("SNAPSHOT")) {
    version = version.toString().replace("SNAPSHOT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd.HH.mm.ss.SSS")))
}

repositories {
    mavenCentral()
    maven ("https://jitpack.io")
    maven ("https://repo.spring.io/artifactory/libs-snapshot-local/")
}

val languageServer by configurations.creating

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    //implementation("com.github.ballerina-platform:lsp4intellij:0.94.2")
    implementation(project(":lsp4intellij"))

    implementation("org.springframework.ide.vscode:commons-java:1.22.0-SNAPSHOT")
    languageServer("org.springframework.ide.vscode:spring-boot-language-server:1.22.0-SNAPSHOT:exec") {
        isTransitive = false
    }
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version.set("223-EAP-SNAPSHOT")
    type.set("IC")
    pluginName.set("idea-spring-tools")
    plugins.set(listOf("java"))
    updateSinceUntilBuild.set(true)
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
tasks.getByName<PatchPluginXmlTask>("patchPluginXml") {
    untilBuild.set("243.*")
    sinceBuild.set("221.*")
}

tasks.getByName<PrepareSandboxTask>("prepareSandbox").doLast {
    val pluginServerDir = "${intellij.sandboxDir.get()}/plugins/${intellij.pluginName.get()}/lib/server"

    mkdir(pluginServerDir)
    copy {
        from(languageServer)
        into(pluginServerDir)
        rename("spring-boot-language-server.*\\.jar", "language-server.jar")
    }
}

tasks {
    buildPlugin {
        doLast() {
            val content = """
                <?xml version="1.0" encoding="UTF-8"?>
                <plugins>
                    <plugin id="org.gap.ijplugins.spring.idea-spring-tools" url="https://dl.bintray.com/gayanper/idea-spring-tools/${intellij.pluginName}-${version}.zip"
                        version="${version}">
                        <idea-version since-build="222.*" until-build="243.*" />
                    </plugin>
                </plugins>                
                
            """.trimIndent()
            file("build/distributions/updatePlugins.xml").writeText(content)
        }
    }

    publishPlugin {
        token.set(System.getenv("JB_API_KEY"))
    }

    runIde {
        setJvmArgs(listOf("-Dsts4.jvmargs=-Xmx512m -Xms512m"))
        jbrVersion.set("17.0.3b469.37")
    }
}


release {
    failOnUnversionedFiles = false
    failOnSnapshotDependencies = false
    tagTemplate = "$version"
    buildTasks = arrayListOf("buildPlugin")
}
