package org.gap.ijplugins.spring.tools;

import com.google.common.base.Strings;
import com.intellij.openapi.application.PreloadingActivity;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import org.jetbrains.annotations.NotNull;
import org.wso2.lsp4intellij.IntellijLanguageClient;
import org.wso2.lsp4intellij.requests.Timeouts;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StsPreloadingActivity extends PreloadingActivity {

    private static final Logger LOGGER = Logger.getInstance(StsPreloadingActivity.class);
    private static final String PTRN_JAVA = "java";
    public static final String LANG_ID_JAVA = PTRN_JAVA;
    public static final String LANG_ID_XML = "xml";
    public static final String LANG_ID_PROPERTIES = "spring-boot-properties";
    public static final String LANG_ID_YAML = "spring-boot-properties-yaml";
    private static final String PTRN_APPLICATION_YAML = "application.*\\.yaml";
    private static final String PTRN_APPLICATION_YML = "application.*\\.yml";
    private static final String PTRN_CONTEXT_XML = "xml";
    private static final String PTRN_APPLICATION_PROPERTIES = "application.*\\.properties";

    @Override
    public void preload(@NotNull ProgressIndicator progressIndicator) {
        if (Strings.isNullOrEmpty(System.getProperty(PTRN_JAVA + ".home"))) {
            LOGGER.error("No java home found in system properties");
            return;
        }

        if (Prerequisities.isBelowJava8()) {
            LOGGER.error("Unsupported java version, 1.8 or above is required");
            return;
        }

        IntellijLanguageClient.setTimeout(Timeouts.INIT, 60000);
        IntellijLanguageClient.setTimeout(Timeouts.COMPLETION, 5000);

        // construct extensions patterns for now from system properties if available
        final List<String> extensions = Stream.concat(Arrays.stream(System.getProperty("sts4.boot.extensions", "").split(",")),
                Arrays.stream(new String[]{PTRN_JAVA, PTRN_APPLICATION_YAML, PTRN_APPLICATION_YML, PTRN_CONTEXT_XML,
                        PTRN_APPLICATION_PROPERTIES})).filter(i -> !i.isEmpty()).collect(Collectors.toList());

        IntellijLanguageClient.addServerDefinition(
                StsServiceDefinitionBuilder.forExtensions(extensions.stream().collect(Collectors.joining(",")))
                        .withLanguageMapping(PTRN_JAVA, LANG_ID_JAVA)
                        .withLanguageMapping("yaml", LANG_ID_YAML)
                        .withLanguageMapping("yml", LANG_ID_YAML)
                        .withLanguageMapping("xml", LANG_ID_XML)
                        .withLanguageMapping("properties", LANG_ID_PROPERTIES)
                        .withServerListener()
                        //.enableDebugging()
                        .build());

        StsLspExtensionManager extensionManager = new StsLspExtensionManager();
        extensions.forEach(e -> IntellijLanguageClient.addExtensionManager(e, extensionManager));
    }
}
