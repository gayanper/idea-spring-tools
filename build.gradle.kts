import org.jetbrains.intellij.tasks.PatchPluginXmlTask
import org.jetbrains.intellij.tasks.PrepareSandboxTask
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    id("org.jetbrains.intellij") version "0.4.16"
    kotlin("jvm") version "1.3.61"
    id("com.jfrog.bintray") version "1.8.4"
    id("net.researchgate.release") version "2.6.0"
}

group = "org.gap.ijplugins.spring.ideaspringtools"
version = "1.0.0-SNAPSHOT"
val snapshotStr: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd.HH.mm.ss.SSS"))

repositories {
    mavenCentral()
    maven ("https://jitpack.io")
    maven ("https://repo.spring.io/libs-snapshot/")
}

val languageServer by configurations.creating

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.github.ballerina-platform:lsp4intellij:0.94.2")
    implementation("org.springframework.ide.vscode:commons-java:1.15.0-SNAPSHOT")
    languageServer("org.springframework.ide.vscode:spring-boot-language-server:1.15.0-SNAPSHOT:exec") {
        isTransitive = false
    }
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = "2018.3"
    pluginName = "idea-spring-tools"
    setPlugins("IntelliLang")
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
    setUntilBuild("203.*")
    setSinceBuild("183.3647.12")
}

tasks.getByName<PrepareSandboxTask>("prepareSandbox").doLast {
    val pluginServerDir = "${intellij.sandboxDirectory}/plugins/${intellij.pluginName}/lib/server"

    mkdir(pluginServerDir)
    copy {
        from(languageServer)
        into(pluginServerDir)
        rename("spring-boot-language-server.*\\.jar", "language-server.jar")
    }
}

bintray {
    user = System.getenv("BINTRAY_USER")
    key = System.getenv("BINTRAY_API_KEY")
    publish = true
    override = true
    pkg(delegateClosureOf<com.jfrog.bintray.gradle.BintrayExtension.PackageConfig> {
        repo = "idea-spring-tools"
        name = intellij.pluginName
        setLicenses("Apache-2.0")
        userOrg = System.getenv("BINTRAY_USER")
        vcsUrl = "https://github.com/gayanper/idea-spring-tools"
        version(delegateClosureOf<com.jfrog.bintray.gradle.BintrayExtension.VersionConfig> {
            name = snapshotStr
            setVersion(snapshotStr)
        })

        filesSpec(delegateClosureOf<com.jfrog.bintray.gradle.tasks.RecordingCopyTask> {
            from("build/distributions")
            into(".")
            rename ("idea-spring-tools.zip","idea-spring-tools-${snapshotStr}.zip" )
        })

    })
}

release {
    failOnUnversionedFiles = false
}