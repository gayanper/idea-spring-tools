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

package org.gap.ijplugins.spring.tools.graphics;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public final class StsIcons {
    private static final Supplier<Icon> bootIcon = Suppliers.memoize(() -> IconLoader.getIcon("/icons/boot-icon.png",
            StsIcons.class.getClassLoader()));

    private static final Supplier<Icon> beanIcon = Suppliers.memoize(() -> IconLoader.getIcon("/icons/spring-bean.svg",
            StsIcons.class.getClassLoader()));

    private static final Supplier<Icon> requestMappingIcon = Suppliers.memoize(() -> IconLoader.getIcon("/icons/spring-request-mapping.svg",
            StsIcons.class.getClassLoader()));

    private StsIcons() {
    }

    public static Icon getBootIcon() {
        return bootIcon.get();
    }

    public static Icon getBeanIcon() {
        return beanIcon.get();
    }

    public static Icon getRequestMappingIcon() {
        return requestMappingIcon.get();
    }
}
