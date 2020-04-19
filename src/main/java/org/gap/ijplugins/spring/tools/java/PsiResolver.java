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
