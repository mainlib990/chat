package org.mainlib990.chat.domain;

import org.mainlib990.core.lib.Validate;

import java.util.UUID;

public sealed interface Channel {

    record Opened(Id id) implements Channel {
    }

    record Closed(Id id) implements Channel {
    }

    record Id(UUID value) {

        public static Validate<Id, String> parse(String candidate) {
            try {
                UUID value = UUID.fromString(candidate);
                return Validate.validated(new Id(value));
            } catch (IllegalArgumentException _) {
                return Validate.invalidated("유효하지 않은 채널 식별자입니다: " + candidate);
            }
        }
    }
}
