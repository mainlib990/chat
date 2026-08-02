package org.mainlib990.chat.domain;

import java.util.UUID;

public sealed interface ChatEvent {

    record Notified(Id id, Message message) implements ChatEvent {
    }

    record Sent(Id id, Message message) implements ChatEvent {
    }

    static ChatEvent notified(Id id, Message message) {
        return new Notified(id, message);
    }

    static ChatEvent sent(Id id, Message message) {
        return new Sent(id, message);
    }

    record Id(UUID value) {
    }
}
