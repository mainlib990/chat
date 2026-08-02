package org.mainlib990.chat.app;

public record SendMessageCommand(
        String channelId,
        String senderId,
        String receiverId,
        String text
) {
}
