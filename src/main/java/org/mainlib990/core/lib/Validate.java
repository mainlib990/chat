package org.mainlib990.core.lib;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public sealed interface Validate<V, R> {

    record Validated<V, R>(V v) implements Validate<V, R> {
    }

    record Invalidated<V, R>(R r) implements Validate<V, R> {
    }

    static <V, R> Validate<V, R> validated(V v) {
        return new Validated<>(Objects.requireNonNull(v));
    }

    static <V, R> Validate<V, R> emptyValidated() {
        return new Validated<>(null);
    }

    static <V, R> Validate<V, R> invalidated(R r) {
        return new Invalidated<>(Objects.requireNonNull(r));
    }

    @SafeVarargs
    static <R> Validate<Void, List<R>> allOf(Validate<?, R>... validates) {
        for (Validate<?, R> validate : validates) {
            Objects.requireNonNull(validate);
        }
        return allOf(Arrays.asList(validates));
    }

    private static <R> Validate<Void, List<R>> allOf(List<Validate<?, R>> validates) {
        List<R> rs = validates.stream()
                .<R>mapMulti((validate, consumer) -> {
                    if (validate instanceof Invalidated(var r)) {
                        consumer.accept(r);
                    }
                })
                .toList();

        return rs.isEmpty() ? emptyValidated() : invalidated(rs);
    }

    default V orElseThrow() {
        return switch (this) {
            case Validated(var v) -> v;
            case Invalidated(_) -> throw new IllegalStateException();
        };
    }
}
