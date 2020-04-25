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

import com.google.common.collect.ImmutableMap;
import com.intellij.openapi.diagnostic.Logger;
import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.services.LanguageServer;
import org.jetbrains.annotations.NotNull;
import org.wso2.lsp4intellij.client.languageserver.serverdefinition.ServerListener;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class StsServerListener implements ServerListener {

    private static final Logger LOGGER = Logger.getInstance(StsServerListener.class);

    @Override
    public void initialize(@NotNull LanguageServer server, @NotNull InitializeResult result) {
        final String globs = Stream.concat(
                Arrays.stream(System.getProperty("sts4.boot.scan-folders-globs", "").split(",")),
                Arrays.stream(new String[]{"**/src/main/**", "**/src/test/**"}))
                .filter(i -> !i.isEmpty()).collect(Collectors.joining(","));

        ImmutableMap<String, Object> configJsonObject = ImmutableMap
                .of("boot-java",
                        ImmutableMap.of("support-spring-xml-config", ImmutableMap.of("on", "true",
                            "hyperlinks", "true", "scan-folders", globs,
                            "content-assist", "true")),
                        "scan-java-test-sources", ImmutableMap.of("on", " true"));
        server.getWorkspaceService()
                .didChangeConfiguration(new DidChangeConfigurationParams(configJsonObject));
    }
}
