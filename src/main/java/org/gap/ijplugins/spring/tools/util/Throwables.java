package org.gap.ijplugins.spring.tools.util;

import com.intellij.util.ThrowableConsumer;

import java.util.function.Consumer;

public final class Throwables {
    private Throwables() {
    }

    public static <S, T extends Throwable> Consumer<S> fromThrowable(ThrowableConsumer<S, T> consumer,
                                                                     Consumer<T> errorConsumer) {
        return s -> {
            try {
                consumer.consume(s);
            } catch (Throwable t) {
                errorConsumer.accept((T) t);
            }
        };
    }
}
