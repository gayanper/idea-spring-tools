package org.gap.ijplugins.spring.tools;

import com.google.common.collect.ImmutableList;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.PluginId;
import org.wso2.lsp4intellij.client.languageserver.serverdefinition.RawCommandServerDefinition;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class StsServiceDefinitionBuilder {

    private static final Logger LOGGER = Logger.getInstance(StsServiceDefinitionBuilder.class);
    public static final String LAUNCHER = "org.springframework.boot.loader.JarLauncher";

    private String extensions;
    private String langId;
    private boolean serverListenerEnabled = false;
    private Map<String, String> langIds = new HashMap<>();
    private boolean debug = false;

    private StsServiceDefinitionBuilder(String extensions) {
        this.extensions = extensions;
    }

    public static StsServiceDefinitionBuilder forExtensions(String extensions) {
        return new StsServiceDefinitionBuilder(extensions);
    }

    public StsServiceDefinitionBuilder withLanguageMapping(String extension, String languageId) {
        langIds.put(extension, languageId);
        return this;
    }

    public StsServiceDefinitionBuilder withServerListener() {
        this.serverListenerEnabled = true;
        return this;
    }

    public StsServiceDefinitionBuilder enableDebugging() {
        this.debug = true;
        return this;
    }

    public RawCommandServerDefinition build() {
        final String javaExecutable = isWindows() ? "java.exe" : "java";
        final String javaHome = System.getProperty("java.home");

        try {
            final StringBuilder classPathBuilder = new StringBuilder();
            final File root = Arrays.stream(PluginManagerCore.getPlugins())
                    .filter(d -> PluginId.getId("org.gap.ijplugins.spring.idea-spring-tools").equals(d.getPluginId()))
                    .findFirst()
                    .map(d -> d.getPath())
                    .orElseThrow(() -> new IllegalStateException("PluginDescriptor for org.gap.ijplugins.spring.idea-spring-tools not found."));
            final Path javaHomePath = Paths.get(javaHome);

            classPathBuilder
                    .append(new File(root, "lib/server/language-server.jar").getPath());
            if (Prerequisities.isJava8()) {
                Path toolsJar = javaHomePath.resolve(Paths.get("lib", "tools.jar"));
                if (Files.exists(toolsJar)) {
                    classPathBuilder.append(File.pathSeparator).append(toolsJar);
                } else {
                    toolsJar = javaHomePath.resolve(Paths.get("..", "lib", "tools.jar"));
                    classPathBuilder.append(File.pathSeparator).append(toolsJar);
                }
            }
            //classPathBuilder.append("\'");
            final String javaExePath = javaHomePath.resolve(Paths.get("bin", javaExecutable))
                    .toString();

            final ImmutableList.Builder<String> commandBuilder = ImmutableList.builder();
            commandBuilder.add(javaExePath);
            commandBuilder.add("-classpath").add(classPathBuilder.toString());

            if (debug) {
                commandBuilder.add("-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=1044");
            }
            //commandBuilder.add("-Dlanguageserver.boot.enable-jandex-index=true");
            commandBuilder.add("-Dsts.lsp.client=vscode");
            commandBuilder.add(LAUNCHER);

            if (serverListenerEnabled) {
                return new StsListenableServerDefinition(extensions,
                        langIds, commandBuilder.build().toArray(new String[0]));
            } else {
                return new StsServerDefinition(extensions,
                        langIds, commandBuilder.build().toArray(new String[0]));
            }

        } catch (Exception e) {
            LOGGER.error(e);
            return null;
        }
    }

    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().startsWith("win");
    }
}
