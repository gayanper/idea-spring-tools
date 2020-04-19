package org.gap.ijplugins.spring.tools.java;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.intellij.lang.jvm.annotation.*;
import com.intellij.lang.jvm.types.JvmPrimitiveTypeKind;
import com.intellij.lang.jvm.types.JvmType;
import com.intellij.openapi.application.ex.ApplicationUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.JdkOrderEntry;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.LibraryUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.util.ClassUtil;
import com.intellij.psi.util.PsiUtil;
import org.springframework.ide.vscode.commons.protocol.java.Classpath;
import org.springframework.ide.vscode.commons.protocol.java.JavaTypeData;
import org.springframework.ide.vscode.commons.protocol.java.JavaTypeData.JavaTypeKind;
import org.springframework.ide.vscode.commons.protocol.java.TypeData;
import org.springframework.ide.vscode.commons.protocol.java.TypeData.AnnotationData;
import org.springframework.ide.vscode.commons.protocol.java.TypeData.ClasspathEntryData;
import org.springframework.ide.vscode.commons.protocol.java.TypeData.FieldData;
import org.springframework.ide.vscode.commons.protocol.java.TypeData.MethodData;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TypeProvider {
    private static final Logger log = Logger.getInstance(TypeProvider.class);

    private static Map<Class, Function<JvmAnnotationAttributeValue, Object>> attributeValueMappings = new HashMap<>();
    private static Map<JvmPrimitiveTypeKind, JavaTypeKind> primitiveKindMapping = new HashMap<>();

    static {
        attributeValueMappings.put(JvmAnnotationConstantValue.class,
                v -> ((JvmAnnotationConstantValue) v).getConstantValue());
        attributeValueMappings.put(JvmAnnotationEnumFieldValue.class,
                v -> ((JvmAnnotationEnumFieldValue) v).getFieldName());
        attributeValueMappings.put(JvmAnnotationClassValue.class,
                v -> ((JvmAnnotationClassValue) v).getQualifiedName());
        attributeValueMappings.put(JvmAnnotationArrayValue.class,
                v -> ((JvmAnnotationArrayValue) v).getValues().stream()
                        .map(v1 -> attributeValueMappings.get(v1.getClass()).apply(v1)).toArray());

        primitiveKindMapping.put(JvmPrimitiveTypeKind.BOOLEAN, JavaTypeKind.BOOLEAN);
        primitiveKindMapping.put(JvmPrimitiveTypeKind.BYTE, JavaTypeKind.BYTE);
        primitiveKindMapping.put(JvmPrimitiveTypeKind.CHAR, JavaTypeKind.CHAR);
        primitiveKindMapping.put(JvmPrimitiveTypeKind.DOUBLE, JavaTypeKind.DOUBLE);
        primitiveKindMapping.put(JvmPrimitiveTypeKind.FLOAT, JavaTypeKind.FLOAT);
        primitiveKindMapping.put(JvmPrimitiveTypeKind.INT, JavaTypeKind.INT);
        primitiveKindMapping.put(JvmPrimitiveTypeKind.LONG, JavaTypeKind.LONG);
        primitiveKindMapping.put(JvmPrimitiveTypeKind.SHORT, JavaTypeKind.SHORT);
    }

    private final PsiManager psiManager;
    private final Project project;

    public TypeProvider(Project project) {
        this.project = project;
        this.psiManager = PsiManager.getInstance(project);
    }

    public TypeData typeDataFor(String typeBinding) {
        return ApplicationUtil.tryRunReadAction(() -> processTypeInfo(typeBinding));
    }

    private TypeData processTypeInfo(String typeBinding) {
        PsiClass psiClass = ClassUtil.findPsiClass(psiManager, JavaUtils.typeBindingKeyToFqName(typeBinding));
        if (psiClass == null) {
            return null;
        }
        TypeData data = new TypeData();
        data.setName(psiClass.getName());
        data.setLabel(data.getName());

        if (PsiUtil.isInnerClass(psiClass)) {
            data.setDeclaringType(JvmBindings.getBindingKey(psiClass));
        }
        data.setFlags(PsiUtil.getAccessLevel(psiClass.getModifierList()));

        data.setFqName(psiClass.getQualifiedName());
        data.setClass(PsiUtil.isLocalClass(psiClass));
        data.setAnnotation(psiClass.isAnnotationType());
        data.setInterface(psiClass.isInterface());
        data.setEnum(psiClass.isEnum());
        if (psiClass.getSuperClass() != null) {
            data.setSuperClassName(JvmBindings.getBindingKey(psiClass.getSuperClass()));
        }
        data.setSuperInterfaceNames(Arrays.stream(psiClass.getInterfaces())
                .map(i -> JvmBindings.getBindingKey(i)).toArray(i -> new String[i]));

        data.setBindingKey(typeBinding);
        data.setFields(mapFields(psiClass.getFields()));
        data.setMethods(mapMethods(psiClass.getMethods()));
        data.setAnnotations(mapAnnotations(psiClass.getAnnotations()));
        data.setClasspathEntry(findCPE(psiClass));
        return data;
    }

    private ClasspathEntryData findCPE(PsiClass psiClass) {
        VirtualFile virtualFile = psiClass.getContainingFile().getVirtualFile();
        OrderEntry library = LibraryUtil.findLibraryEntry(virtualFile, psiClass.getProject());
        if (library == null) {
            log.warn("No classpath entry library found for class: " + psiClass.getQualifiedName());
            return null;
        } else {
            return Arrays.stream(library.getFiles(OrderRootType.CLASSES))
                    .findFirst().map(file -> {
                        ClasspathEntryData data = new ClasspathEntryData();
                        data.setModule(library.getPresentableName());
                        data.setCpe(toCPE(library, file));
                        return data;
                    }).orElse(null);
        }
    }

    private Classpath.CPE toCPE(OrderEntry entry, VirtualFile file) {
        Classpath.CPE cpe = CommonUtils.toBinaryCPE(file);
        cpe.setJavaContent(true);
        cpe.setSystem(entry instanceof JdkOrderEntry);
        cpe.setOwn(false);
        return cpe;
    }

    private List<AnnotationData> mapAnnotations(PsiAnnotation[] annotations) {
        return Arrays.stream(annotations).map(a -> {
            AnnotationData data = new AnnotationData();
            data.setFqName(a.getQualifiedName());
            data.setName(ClassUtil.extractClassName(a.getQualifiedName()));
            data.setLabel(data.getName());

            Map<String, Object> values = new HashMap<>();
            for (JvmAnnotationAttribute attribute : a.getAttributes()) {
                Function<JvmAnnotationAttributeValue, Object> mapper =
                        attributeValueMappings.get(attribute.getAttributeValue().getClass());
                if (mapper != null) {
                    values.put(attribute.getAttributeName(), mapper.apply(attribute.getAttributeValue()));
                }
            }
            data.setValuePairs(values);
            return data;
        }).collect(Collectors.toList());
    }

    private List<FieldData> mapFields(PsiField[] fields) {
        return Arrays.stream(fields).map(f -> {
            FieldData data = new FieldData();
            data.setName(f.getName());
            data.setLabel(data.getName());

            data.setDeclaringType(JvmBindings.getBindingKey(f.getContainingClass()));
            data.setFlags(PsiUtil.getAccessLevel(f.getModifierList()));

            data.setBindingKey(JvmBindings.getBindingKey(f));
            data.setType(mapType(f.getType()));
            data.setEnumConstant(f instanceof PsiEnumConstant);
            data.setAnnotations(mapAnnotations(f.getAnnotations()));
            return data;
        }).collect(Collectors.toList());
    }

    private List<MethodData> mapMethods(PsiMethod[] methods) {
        return Arrays.stream(methods).map(m -> {
            MethodData data = new MethodData();
            data.setName(m.getName());
            data.setLabel(data.getName());

            data.setDeclaringType(JvmBindings.getBindingKey(m.getContainingClass()));
            data.setFlags(PsiUtil.getAccessLevel(m.getModifierList()));

            data.setBindingKey(JvmBindings.getBindingKey(m));
            data.setConstructor(m.isConstructor());
            data.setReturnType(mapType(m.getReturnType()));
            data.setParameters(Arrays.stream(m.getParameters()).map(p -> p.getType()).map(this::mapType).collect(Collectors.toList()));
            data.setAnnotations(mapAnnotations(m.getAnnotations()));
            return data;
        }).collect(Collectors.toList());
    }

    private JavaTypeData mapType(JvmType type) {
        JavaTypeData data = new JavaTypeData();
        data.setName(JvmBindings.getGeneralTypeBindingKey(type));
        fillKindAndExtra(type, data);
        return data;
    }

    private void fillKindAndExtra(JvmType type, JavaTypeData data) {
        if (type instanceof PsiArrayType) {
            data.setKind(JavaTypeKind.ARRAY);
            final PsiArrayType arrayType = (PsiArrayType) type;
            data.setExtras(Maps.newLinkedHashMap(ImmutableMap.of("component", JvmBindings.getGeneralTypeBindingKey(arrayType.getComponentType()),
                    "dimensions", String.valueOf(arrayType.getArrayDimensions()))));
        } else if (type instanceof PsiPrimitiveType) {
            data.setKind(primitiveKindMapping.get(((PsiPrimitiveType) type).getKind()));
        } else if (type instanceof PsiTypeParameter) {
            data.setKind(JavaTypeKind.PARAMETERIZED);
            JavaTypeData owner = new JavaTypeData();
            owner.setKind(JavaTypeKind.CLASS);
            owner.setName(JvmBindings.getBindingKey(((PsiTypeParameter) type).getOwner().getContainingClass()));
            data.setExtras(Maps.newLinkedHashMap(ImmutableMap.of("owner", owner)));
        }
    }
}
