package org.gap.ijplugins.spring.tools.java;

import com.intellij.openapi.compiler.CompilerPaths;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import org.springframework.ide.vscode.commons.protocol.java.Classpath.CPE;

import java.util.Optional;
import java.util.function.Function;

public class CommonUtils {

    public static CPE toBinaryCPE(VirtualFile file) {
        return CPE.binary(file.getPath().replace("!/", ""));
    }

    public static Optional<String> fromFirst(VirtualFile[] files, Function<VirtualFile, String> transformer) {
        if (files.length > 0) {
            return Optional.ofNullable(transformer.apply(files[0]));
        }
        return Optional.empty();
    }

    public static String outputDir(Module module) {
        return CompilerPaths.getModuleOutputPath(module, false);
    }

    public static String testOutputDir(Module module) {
        return CompilerPaths.getModuleOutputPath(module, true);
    }
}
