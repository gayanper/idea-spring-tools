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

package org.gap.ijplugins.spring.tools.livedata;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import org.eclipse.lsp4j.ExecuteCommandParams;
import org.gap.ijplugins.spring.tools.StsPreloadingActivity;
import org.jetbrains.annotations.NotNull;
import org.wso2.lsp4intellij.IntellijLanguageClient;
import org.wso2.lsp4intellij.client.languageserver.wrapper.LanguageServerWrapper;
import org.wso2.lsp4intellij.utils.FileUtils;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public final class StsApplicationManager {
    private static final Logger LOGGER = Logger.getInstance(StsApplicationManager.class);

    public List<ProcessCommandInfo> listApplication(Project project) {
        return getLanguageServerWrapper(project)
                .flatMap(this::execSyncCommand)
                .orElse(Collections.emptyList());
    }

    @NotNull
    private Optional<LanguageServerWrapper> getLanguageServerWrapper(Project project) {
        return IntellijLanguageClient.getAllServerWrappersFor(FileUtils.projectToUri(project))
                    .stream()
                    .filter(sw -> sw.isActive() && sw.serverDefinition.ext.contains(StsPreloadingActivity.EXT_PTRN_JAVA))
                    .findFirst();
    }

    public boolean canQuery(Project project) {
        return getLanguageServerWrapper(project).isPresent();
    }

    private Optional<List<ProcessCommandInfo>> execSyncCommand(LanguageServerWrapper sw) {
        try {
            return Optional.of(sw.getRequestManager()
                    .executeCommand(new ExecuteCommandParams("sts/livedata/listProcesses",
                            Collections.emptyList())).get())
                    .map(r -> (List<Map<String, String>>)r)
                    .map(r -> r.stream().map(ProcessCommandInfo::new).collect(Collectors.toList()));
        } catch (InterruptedException e) {
            LOGGER.warn(e.getMessage(), e);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return Optional.empty();
    }

    public void sendCommand(Project project, ProcessCommandInfo info) {
        getLanguageServerWrapper(project)
                .map(LanguageServerWrapper::getRequestManager)
                .ifPresent(rm -> rm.executeCommand(new ExecuteCommandParams(info.getAction(),
                        Collections.singletonList(info))));
    }
}
