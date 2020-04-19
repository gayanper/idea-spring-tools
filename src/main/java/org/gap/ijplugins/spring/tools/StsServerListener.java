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
