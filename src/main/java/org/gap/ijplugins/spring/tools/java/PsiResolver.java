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

import com.google.common.base.Strings;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMember;
import com.intellij.psi.search.GlobalSearchScope;
import org.eclipse.lsp4j.jsonrpc.messages.Tuple;

import java.util.Arrays;

public class PsiResolver {
    private Project project;

    public PsiResolver(Project project) {
        this.project = project;
    }

    public Tuple.Two<PsiClass, PsiMember> resolvePsiElements(String bindingKey) {
        // Lcom/example/demo/BootBean;.setAge(LString;)V

        String[] bindings = bindingKey.split("\\.");
        PsiClass psiClass = JavaPsiFacade.getInstance(project).findClass(JavaUtils.typeBindingKeyToFqName(bindings[0]),
                GlobalSearchScope.allScope(project));

        PsiMember member = null;
        if(psiClass != null) {
            // try to resolve source psi class if available
            psiClass = (PsiClass) psiClass.getNavigationElement();
            if(bindings.length > 1 && !Strings.isNullOrEmpty(bindings[1])) {
                final String memberBinding = bindings[1];
                if(memberBinding.matches(".*\\(.*\\).*")) {
                    member = Arrays.stream(psiClass.getAllMethods())
                            .map(m -> Tuple.two(JvmBindings.getBindingKey(m), m))
                            .filter(t -> t.getFirst().endsWith(memberBinding))
                            .findFirst().map(t -> t.getSecond()).orElse(null);
                } else {
                    member = Arrays.stream(psiClass.getAllFields())
                            .map(m -> Tuple.two(JvmBindings.getBindingKey(m), m))
                            .filter(t -> memberBinding.equals(t.getFirst()))
                            .findFirst().map(t -> t.getSecond()).orElse(null);
                }
            }
        }
       return Tuple.two(psiClass, member);
    }
}
