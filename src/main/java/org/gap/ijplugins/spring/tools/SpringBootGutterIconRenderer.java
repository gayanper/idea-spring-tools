package org.gap.ijplugins.spring.tools;

import com.intellij.openapi.editor.markup.GutterIconRenderer;
import org.gap.ijplugins.spring.tools.graphics.StsIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author Alex Boyko
 */
public class SpringBootGutterIconRenderer extends GutterIconRenderer {

    public static SpringBootGutterIconRenderer INSTANCE = new SpringBootGutterIconRenderer();

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return StsIcons.getBootIcon();
    }
}
