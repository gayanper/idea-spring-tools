package org.gap.ijplugins.spring.tools.extensions;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import org.eclipse.lsp4j.SymbolInformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.wso2.lsp4intellij.contributors.label.LSPDefaultLabelProvider;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

public class StsLabelProvider extends LSPDefaultLabelProvider {
    private static final Logger LOGGER = Logger.getInstance(StsLabelProvider.class);

    @Nullable
    @Override
    public String symbolLocationFor(@NotNull SymbolInformation symbolInformation, @NotNull Project project) {
        VirtualFile fileByIoFile;
        try {
            fileByIoFile = LocalFileSystem.getInstance().
                    findFileByIoFile(new File((new URL(symbolInformation.getLocation().getUri())).getFile()));
        } catch (MalformedURLException e) {
            fileByIoFile = null;
            LOGGER.error(e);
        }

        return Optional.ofNullable(fileByIoFile).map(f -> {
            final VirtualFile sourceRootForFile = ProjectFileIndex.getInstance(project).getSourceRootForFile(f);
            return VfsUtilCore.findRelativePath(sourceRootForFile, f, File.separatorChar);
        }).map(s -> String.format("(%s)", s)).orElse("");
    }
}
