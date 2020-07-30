/*
 *  Copyright (c) 2020 Gayan Perera
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 * Contributors:
 *     Gayan Perera <gayanper@gmail.com> - initial API and implementation
 */

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

            if (Prerequisities.isJava8()) {
                Path toolsJar = javaHomePath.resolve(Paths.get("lib", "tools.jar"));
                if (Files.exists(toolsJar)) {
                    classPathBuilder.append(File.pathSeparator).append(toolsJar);
                } else {
                    toolsJar = javaHomePath.resolve(Paths.get("..", "lib", "tools.jar"));
                    classPathBuilder.append(File.pathSeparator).append(toolsJar);
                }
            }
            final String javaExePath = javaHomePath.resolve(Paths.get("bin", javaExecutable))
                    .toString();

            final ImmutableList.Builder<String> commandBuilder = ImmutableList.builder();
            commandBuilder.add(javaExePath);
            if(classPathBuilder.length() > 0) {
                commandBuilder.add("-classpath").add(classPathBuilder.toString());
            }

            if (debug) {
                commandBuilder.add("-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=1044");
            }
            //commandBuilder.add("-Dlanguageserver.boot.enable-jandex-index=true");
            //commandBuilder.add("-Dsts.lsp.client=vscode");
            commandBuilder.add("-jar").add(new File(root, "lib/server/language-server.jar").getPath());

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
