package org.mainlib990.core.lib;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public sealed interface Result<V> {

    record Succeeded<V>(V value) implements Result<V> {
    }

    record Failed<T>(String message) implements Result<T> {
    }

    static <V> Succeeded<V> succeeded(V value) {
        return new Succeeded<>(Objects.requireNonNull(value));
    }

    static <V> Succeeded<V> emptySucceeded() {
        return new Succeeded<>(null);
    }

    static <V> Failed<V> failed(String message) {
        return new Failed<>(Objects.requireNonNull(message));
    }

    default V orElseThrow() {
        return switch (this) {
            case Succeeded(var v) -> v;
            case Failed(_) -> throw new IllegalStateException();
        };
    }

    default <R> Result<R> map(Function<V, R> mapper) {
        Objects.requireNonNull(mapper);
        return switch (this) {
            case Succeeded(var v) -> succeeded(mapper.apply(v));
            case Failed(var message) -> failed(message);
        };
    }

    default void ifPresent(Consumer<V> consumer) {
        Objects.requireNonNull(consumer);
        switch (this) {
            case Succeeded(var v) -> consumer.accept(v);
            case Failed(_) -> {
                // do nothing
            }
        }
    }
}
