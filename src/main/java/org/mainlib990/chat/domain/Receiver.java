package org.mainlib990.chat.domain;

import org.mainlib990.core.lib.Validate;

import java.util.UUID;

public sealed interface Receiver {

    record Offline(Id id) implements Receiver {
    }

    record Online(Id id, Activity activity) implements Receiver {
    }

    sealed interface Activity {

        record None() implements Activity {
        }

        record Chatting(Channel.Id channelId) implements Activity {
        }

        static Activity none() {
            return new None();
        }
    }

    record Id(UUID value) {

        public static Validate<Id, String> parse(String candidate) {
            try {
                UUID value = UUID.fromString(candidate);
                return Validate.validated(new Receiver.Id(value));
            } catch (IllegalArgumentException _) {
                return Validate.invalidated("유효하지 않은 수신자 식별자입니다: " + candidate);
            }
        }
    }
}
