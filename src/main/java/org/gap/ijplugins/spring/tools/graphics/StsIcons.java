package org.gap.ijplugins.spring.tools.graphics;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.intellij.openapi.util.IconLoader;

import javax.swing.*;

public final class StsIcons {
    private static final Supplier<Icon> bootIcon = Suppliers.memoize(() -> IconLoader.getIcon("/icons/boot-icon.png"));

    private static final Supplier<Icon> beanIcon = Suppliers.memoize(() -> IconLoader.getIcon("/icons/bean-icon.png"));

    private StsIcons() {
    }

    public static Icon getBootIcon() {
        return bootIcon.get();
    }

    public static Icon getBeanIcon() {
        return beanIcon.get();
    }
}
