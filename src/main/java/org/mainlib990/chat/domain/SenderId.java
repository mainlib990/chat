package org.mainlib990.chat.domain;

import org.mainlib990.core.lib.Validate;

import java.util.UUID;

public record SenderId(UUID value) {

    public static Validate<SenderId, String> parse(String candidate) {
        try {
            UUID value = UUID.fromString(candidate);
            return Validate.validated(new SenderId(value));
        } catch (IllegalArgumentException _) {
            return Validate.invalidated("유효하지 않은 송신자 식별자입니다: " + candidate);
        }
    }
}
