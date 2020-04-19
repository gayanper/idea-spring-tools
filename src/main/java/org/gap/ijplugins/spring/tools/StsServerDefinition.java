package org.gap.ijplugins.spring.tools;

import org.wso2.lsp4intellij.client.languageserver.serverdefinition.RawCommandServerDefinition;

import java.util.Map;

public class StsServerDefinition extends RawCommandServerDefinition {

    public StsServerDefinition(String ext, Map<String, String> languageIds, String[] command) {
        super(ext, languageIds, command);
    }


}
