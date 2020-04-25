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

import org.springframework.ide.vscode.commons.protocol.java.Classpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClasspathArgument {
    private String projectURI;
    private String projectName;
    private boolean deleted = false;
    private Classpath classpath;

    public ClasspathArgument(String projectName) {
        this.projectName = projectName;
    }

    public static ClasspathArgument argument(String projectName) {
        return new ClasspathArgument(projectName);
    }

    public ClasspathArgument projectUri(String projectURI) {
        this.projectURI = projectURI;
        return this;
    }

    public ClasspathArgument deleted(boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    public ClasspathArgument classpath(Classpath classpath) {
        this.classpath = classpath;
        return this;
    }

    public List<Object> arguments() {
        List<Object> arguments = new ArrayList<>();
        arguments.add(projectURI);
        arguments.add(projectName);
        arguments.add(deleted);
        arguments.add(classpath);
        return Collections.unmodifiableList(arguments);
    }
}
