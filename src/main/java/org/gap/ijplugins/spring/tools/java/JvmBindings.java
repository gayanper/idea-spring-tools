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

import com.intellij.lang.jvm.types.JvmType;
import com.intellij.psi.*;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class JvmBindings {
    private JvmBindings() {
    }

    private static String getBindingKey(PsiPrimitiveType primitive) {
        if (primitive == PsiPrimitiveType.BYTE) {
            return "B";
        } else if (primitive == PsiPrimitiveType.CHAR) {
            return "C";
        } else if (primitive == PsiPrimitiveType.DOUBLE) {
            return "D";
        } else if (primitive == PsiPrimitiveType.FLOAT) {
            return "F";
        } else if (primitive == PsiPrimitiveType.INT) {
            return "I";
        } else if (primitive == PsiPrimitiveType.LONG) {
            return "J";
        } else if (primitive == PsiPrimitiveType.VOID) {
            return "V";
        } else {
            return primitive == PsiPrimitiveType.SHORT ? "S" : "Z";
        }
    }

    public static String getBindingKey(PsiClass clazz) {
        StringBuilder sb = new StringBuilder();
        sb.append('L');
        sb.append(clazz.getQualifiedName().replace('.', '/'));
        sb.append(';');
        return sb.toString();
    }

    public static String getBindingKey(PsiField field) {
        StringBuilder sb = new StringBuilder();
        sb.append(getBindingKey(field.getContainingClass()));
        sb.append('.');
        sb.append(field.getName());
        sb.append(')');
        sb.append(getGeneralTypeBindingKey(field.getType()));
        return sb.toString();
    }

    public static String getBindingKey(PsiMethod method) {
        StringBuilder sb = new StringBuilder();
        sb.append(getBindingKey(method.getContainingClass()));
        sb.append('.');
        sb.append(method.getName());
        sb.append('(');
        sb.append(Arrays.stream(method.getParameterList().getParameters()).map(p -> getGeneralTypeBindingKey(p.getType())).collect(Collectors.joining()));
        sb.append(')');
        sb.append(getGeneralTypeBindingKey(method.getReturnType()));
        return sb.toString();
    }


    public static String getGeneralTypeBindingKey(JvmType type) {
        if (type instanceof PsiArrayType) {
            return getBindingKey((PsiArrayType) type);
        } else if (type instanceof PsiPrimitiveType) {
            return getBindingKey((PsiPrimitiveType) type);
        } else if (type instanceof PsiClassType) {
            return getBindingKey((PsiClassType) type);
        } else if (type instanceof PsiWildcardType) {
            return getBindingKey((PsiWildcardType) type);
        } else {
            return "";
        }
//
//
//
//        switch(type) {
//            case PARAMETERIZED_TYPE:
//                return getBindingKey(type.asParameterizedType());
//            case PRIMITIVE:
//                return getBindingKey(type.asPrimitiveType());
//            case TYPE_VARIABLE:
//                return getBindingKey(type.asTypeVariable());
//            case UNRESOLVED_TYPE_VARIABLE:
//                return getBindingKey(type.asUnresolvedTypeVariable());
//            default:
//                return "";
//        }
    }

    private static String getBindingKey(PsiWildcardType type) {
        if (type.getExtendsBound() != null) {
            return "+" + getGeneralTypeBindingKey(type.getExtendsBound());
        } else {
            return type.getSuperBound() != null ? "-" + getGeneralTypeBindingKey(type.getSuperBound()) : "*";
        }
    }

    private static String getBindingKey(PsiArrayType type) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < type.getArrayDimensions(); ++i) {
            sb.append('[');
        }
        sb.append(getGeneralTypeBindingKey(type.getComponentType()));
        return sb.toString();
    }

    private static String getBindingKey(PsiClassType type) {
        StringBuilder sb = new StringBuilder();
        sb.append('L');
        sb.append(type.getClassName().replace('.', '/'));
        sb.append(';');
        return sb.toString();
    }


}
