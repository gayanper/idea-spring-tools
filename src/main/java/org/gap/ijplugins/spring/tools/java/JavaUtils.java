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

import com.google.common.base.Supplier;
import com.intellij.openapi.diagnostic.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

public class JavaUtils {
    private static Logger log = Logger.getInstance(JavaUtils.class);

    public JavaUtils() {
    }

    public static Stream<Path> jreLibs(Supplier<String> javaMinorVersionSupplier, Supplier<String> javaHomeSupplier, Supplier<String> bootClasspathSupplier) {
        String versionString = (String)javaMinorVersionSupplier.get();

        try {
            int version = versionString == null ? 8 : Integer.valueOf(versionString);
            String javaHome;
            if (version > 8) {
                javaHome = (String)javaHomeSupplier.get();
                if (javaHome != null) {
                    Path rtPath = Paths.get(javaHome, "lib", "jrt-fs.jar");
                    if (Files.exists(rtPath, new LinkOption[0])) {
                        return Stream.of(rtPath);
                    }

                    log.error("Cannot find file " + rtPath);
                }
            } else {
                javaHome = (String)bootClasspathSupplier.get();
                if (javaHome != null) {
                    return Arrays.stream(javaHome.split(File.pathSeparator)).map(File::new).filter((f) -> {
                        return f.canRead();
                    }).map((f) -> {
                        return Paths.get(f.toURI());
                    });
                }
            }
        } catch (NumberFormatException var7) {
            log.error("Cannot extract java minor version number.", var7);
        }

        return Stream.empty();
    }

    public static String getJavaRuntimeMinorVersion(String fullVersion) {
        String[] tokenized = fullVersion.split("\\.");
        if (tokenized[0] == "1") {
            if (tokenized.length > 1) {
                return tokenized[1];
            } else {
                log.error("Cannot determine minor version for the Java Runtime Version: " + fullVersion);
                return null;
            }
        } else {
            String version = tokenized[0];
            int idx = version.indexOf(43);
            return idx >= 0 ? version.substring(0, idx) : version;
        }
    }

    public static Path javaHomeFromLibJar(Path libJar) {
        Path root = libJar.getRoot();

        for(Path home = libJar; !root.equals(home.getParent()); home = home.getParent()) {
            Path bin = home.resolve("bin");
            Path lib = home.resolve("lib");
            Path include = home.resolve("include");
            Path man = home.resolve("man");
            Path legal = home.resolve("legal");
            if (Files.isDirectory(bin, new LinkOption[0]) && Files.isDirectory(lib, new LinkOption[0]) && Files.isDirectory(include, new LinkOption[0]) && (Files.isDirectory(man, new LinkOption[0]) || Files.isDirectory(legal, new LinkOption[0]))) {
                return home;
            }
        }

        return null;
    }

    public static Path jreSources(Path libJar) {
        Path home = javaHomeFromLibJar(libJar);
        if (home != null) {
            Path sources = sourceZip(home);
            if (sources == null) {
                sources = sourceZip(home.resolve("lib"));
            }

            return sources;
        } else {
            return null;
        }
    }

    private static Path sourceZip(Path containerFolder) {
        Path sourcesZip = containerFolder.resolve("src.zip");
        return Files.exists(sourcesZip, new LinkOption[0]) ? sourcesZip : null;
    }

    public static String typeBindingKeyToFqName(String bindingKey) {
        return bindingKey == null ? null : bindingKey.substring(1, bindingKey.length() - 1).replace('/', '.');
    }

    public static String typeFqNametoBindingKey(String fqName) {
        StringBuilder sb = new StringBuilder(76);
        sb.append(fqName.replace('.', '/'));
        sb.append(';');
        return sb.toString();
    }
}
