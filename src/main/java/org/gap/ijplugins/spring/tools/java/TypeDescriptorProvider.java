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
