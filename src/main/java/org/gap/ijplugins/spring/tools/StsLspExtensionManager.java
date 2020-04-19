package org.gap.ijplugins.spring.tools;

import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.FileIndexFacade;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;
import org.gap.ijplugins.spring.tools.extensions.StsIconProvider;
import org.gap.ijplugins.spring.tools.extensions.StsLabelProvider;
import org.gap.ijplugins.spring.tools.lang.StsLanguageValidator;
import org.jetbrains.annotations.NotNull;
import org.wso2.lsp4intellij.client.ClientContext;
import org.wso2.lsp4intellij.client.languageserver.ServerOptions;
import org.wso2.lsp4intellij.client.languageserver.requestmanager.DefaultRequestManager;
import org.wso2.lsp4intellij.client.languageserver.requestmanager.RequestManager;
import org.wso2.lsp4intellij.client.languageserver.wrapper.LanguageServerWrapper;
import org.wso2.lsp4intellij.contributors.icon.LSPIconProvider;
import org.wso2.lsp4intellij.contributors.label.LSPLabelProvider;
import org.wso2.lsp4intellij.editor.EditorEventManager;
import org.wso2.lsp4intellij.extensions.LSPExtensionManager;
import org.wso2.lsp4intellij.listeners.EditorMouseListenerImpl;
import org.wso2.lsp4intellij.listeners.EditorMouseMotionListenerImpl;
import org.wso2.lsp4intellij.listeners.LSPCaretListenerImpl;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

import static org.gap.ijplugins.spring.tools.ApplicationUtils.runReadAction;

public class StsLspExtensionManager implements LSPExtensionManager {

    private static final Predicate<? super VirtualFile> SPRING_PREDICATE =
            (f) -> f.getPath().contains("spring-core") || f.getPath().contains("spring-boot");

    private StsLanguageValidator stsLanguageValidator = new StsLanguageValidator();

    @Override
    public <T extends DefaultRequestManager> T getExtendedRequestManagerFor(
            LanguageServerWrapper languageServerWrapper, LanguageServer languageServer,
            LanguageClient languageClient, ServerCapabilities serverCapabilities) {
        return (T) new DefaultRequestManager(languageServerWrapper, languageServer, languageClient,
                serverCapabilities);
    }

    @Override
    public <T extends EditorEventManager> T getExtendedEditorEventManagerFor(Editor editor, DocumentListener documentListener, EditorMouseListenerImpl mouseListener, EditorMouseMotionListenerImpl mouseMotionListener, LSPCaretListenerImpl caretListener, RequestManager requestManager, ServerOptions serverOptions, LanguageServerWrapper wrapper) {
        return (T) new EditorEventManager(editor, documentListener, mouseListener,
                mouseMotionListener, caretListener, requestManager, serverOptions, wrapper);
    }

    @Override
    public Class<? extends LanguageServer> getExtendedServerInterface() {
        return LanguageServer.class;
    }

    @Override
    public LanguageClient getExtendedClientFor(ClientContext clientContext) {
        return new org.gap.ijplugins.spring.tools.StsLanuageClient(clientContext);
    }

    @Override
    public boolean isFileContentSupported(@NotNull PsiFile file) {
        return runReadAction(() ->
                Optional.ofNullable(FileIndexFacade.getInstance(file.getProject()).getModuleForFile(file.getVirtualFile()))
                        .map(this::isSpringModule).orElse(false)) && isSupportedLanguage(file);
    }

    private boolean isSupportedLanguage(PsiFile file) {
        if (file.getLanguage().isKindOf(XMLLanguage.INSTANCE)) {
            return stsLanguageValidator.isXmlSpringBeanFile(file.getVirtualFile(), file.getProject());
        }
        // for all other extension return true for now.
        return true;
    }

    private boolean isSpringModule(Module module) {
        return Arrays.stream(ModuleRootManager.getInstance(module).orderEntries().librariesOnly().classes().getRoots())
                .anyMatch(SPRING_PREDICATE);
    }

    @NotNull
    @Override
    public LSPLabelProvider getLabelProvider() {
        return new StsLabelProvider();
    }

    @NotNull
    @Override
    public LSPIconProvider getIconProvider() {
        return new StsIconProvider();
    }
}
