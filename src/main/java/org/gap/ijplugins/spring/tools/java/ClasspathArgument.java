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
