package org.gap.ijplugins.spring.tools;

import org.wso2.lsp4intellij.client.languageserver.serverdefinition.RawCommandServerDefinition;
import org.wso2.lsp4intellij.client.languageserver.serverdefinition.ServerListener;

import java.util.Map;

public class StsListenableServerDefinition extends RawCommandServerDefinition {

    public StsListenableServerDefinition(String ext, Map<String, String> languageIds, String[] command) {
        super(ext, languageIds, command);
    }

    @Override
    public ServerListener getServerListener() {
        return new StsServerListener();
    }
}
