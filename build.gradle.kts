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
if(version.toString().endsWith("SNAPSHOT")) {
    version = version.toString().replace("SNAPSHOT", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd.HH.mm.ss.SSS")))
}

repositories {
    mavenCentral()
//    mavenLocal()
    maven ("https://jitpack.io")
    maven ("https://dl.bintray.com/gayanper/maven")
    maven ("https://repo.spring.io/libs-snapshot/")
}

val languageServer by configurations.creating

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    //implementation("com.github.ballerina-platform:lsp4intellij:0.94.2")
    implementation("com.github.ballerina-platform:lsp4intellij:0.94.1-20200604.18.27.53.863")

    implementation("org.springframework.ide.vscode:commons-java:1.22.0-SNAPSHOT")
    languageServer("org.springframework.ide.vscode:spring-boot-language-server:1.22.0-SNAPSHOT:exec") {
        isTransitive = false
    }
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = "2020.1"
    pluginName = "idea-spring-tools"
    setPlugins("IntelliLang", "java")
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
    setSinceBuild("193.*")
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
            name = version.toString()
        })

        filesSpec(delegateClosureOf<com.jfrog.bintray.gradle.tasks.RecordingCopyTask> {
            from("build/distributions")
            into(".")
        })

    })
}

release {
    failOnUnversionedFiles = false
    failOnSnapshotDependencies = false
    tagTemplate = "$version"
    buildTasks = arrayListOf("buildPlugin")
}

tasks {
    afterReleaseBuild {
        dependsOn("publishPlugin")
    }

    publishPlugin {
        setToken(System.getenv("JB_API_KEY"))
    }
}
