package org.mainlib990.chat.domain;

import java.time.Instant;
import java.util.UUID;

public record Message(
        Id id,
        Channel channel,
        Instant createdAt,
        SenderId senderId,
        Receiver receiver,
        Content content
) {

    public record Id(UUID value) {
    }
}
