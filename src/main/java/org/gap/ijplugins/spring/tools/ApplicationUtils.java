package org.gap.ijplugins.spring.tools;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Computable;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public final class ApplicationUtils {
    private ApplicationUtils() {
    }

    public static <T> T runReadAction(Computable<T> computable) {
        return ApplicationManager.getApplication().runReadAction(computable);
    }

    public static <T> Future<T> executeOnIntellijPooledThread(Callable<T> callable) {
        return ApplicationManager.getApplication().executeOnPooledThread(callable);
    }
}
