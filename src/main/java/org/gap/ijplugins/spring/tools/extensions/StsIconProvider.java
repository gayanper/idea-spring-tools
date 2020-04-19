package org.gap.ijplugins.spring.tools.extensions;

import org.eclipse.lsp4j.SymbolKind;
import org.gap.ijplugins.spring.tools.StsIcons;
import org.wso2.lsp4intellij.contributors.icon.LSPDefaultIconProvider;

import javax.swing.*;

public class StsIconProvider extends LSPDefaultIconProvider {
    @Override
    public Icon getSymbolIcon(SymbolKind kind) {
        if (kind == SymbolKind.Interface) {
            return StsIcons.getBeanIcon();
        }
        return super.getSymbolIcon(kind);
    }
}
