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
