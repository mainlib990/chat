package org.mainlib990.core.lib;

import java.util.Objects;

public sealed interface Result<V> {

    record Succeeded<V>(V value) implements Result<V> {
    }

    record Failed<T>(String error) implements Result<T> {
    }

    static <V> Succeeded<V> succeeded(V value) {
        return new Succeeded<>(Objects.requireNonNull(value));
    }

    static <V> Succeeded<V> emptySucceeded() {
        return new Succeeded<>(null);
    }

    static <V> Failed<V> failed(String error) {
        return new Failed<>(Objects.requireNonNull(error));
    }

    default V orElseThrow() {
        return switch (this) {
            case Succeeded(var v) -> v;
            case Failed(_) -> throw new IllegalStateException();
        };
    }
}
