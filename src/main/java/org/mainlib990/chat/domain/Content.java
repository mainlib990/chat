package org.mainlib990.chat.domain;

import org.mainlib990.core.lib.Validate;

public record Content(String text) {

    private static final long MAX_LENGTH = 1024;

    public static Validate<Content, String> parse(String candidate) {
        if (candidate.length() <= MAX_LENGTH) {
            return Validate.validated(new Content(candidate));
        }
        return Validate.invalidated("내용의 최대 길이가 %d를 초과하였습니다: %d".formatted(MAX_LENGTH, candidate.length()));
    }
}
