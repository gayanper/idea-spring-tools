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

import com.intellij.openapi.application.ex.ApplicationUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.util.PsiUtil;
import org.springframework.ide.vscode.commons.protocol.java.TypeDescriptorData;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TypeDescriptorProvider {
    public List<TypeDescriptorData> descriptors(PsiClass[] clazzs) {
        return ApplicationUtil.tryRunReadAction(() -> Arrays.stream(clazzs).map(this::map).collect(Collectors.toList()));
    }

    private TypeDescriptorData map(PsiClass psiClass) {
        TypeDescriptorData data = new TypeDescriptorData();
        data.setAnnotation(psiClass.isAnnotationType());
        data.setClass(!psiClass.isInterface() && !psiClass.isAnnotationType() && psiClass.isEnum());
        data.setEnum(psiClass.isEnum());
        data.setFqName(psiClass.getQualifiedName());
        data.setInterface(psiClass.isInterface());
        data.setSuperClassName(Optional.ofNullable(psiClass.getSuperClass()).map(c -> c.getName()).orElse(null));
        data.setSuperInterfaceNames(Arrays.stream(psiClass.getSupers()).filter(c -> c.isInterface()).map(c -> c.getName()).toArray(i -> new String[i]));
        data.setDeclaringType(Optional.ofNullable(psiClass.getContainingClass()).map(JvmBindings::getBindingKey).orElse(null));
        data.setFlags(PsiUtil.getAccessLevel(psiClass.getModifierList()));
        data.setName(psiClass.getName());
        data.setLabel(psiClass.getName());
        return data;
    }
}
