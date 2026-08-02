package org.mainlib990.chat.domain;

import org.mainlib990.core.lib.Result;

import java.util.function.Function;

public class ChatPolicy {

    public Result<Function<ChatEvent.Id, ChatEvent>> sendMessage(Message message) {
        return switch (message.channel()) {
            case Channel.Closed(var id) -> Result.failed("해당 채널이 닫혀 있습니다: " + id);
            case Channel.Opened(var id) -> sendMessage(id, message);
        };
    }

    private Result<Function<ChatEvent.Id, ChatEvent>> sendMessage(
            Channel.Id channelId,
            Message message
    ) {
        return switch (message.receiver()) {
            case Receiver.Offline _ -> Result.succeeded(eventId -> ChatEvent.notified(eventId, message));
            case Receiver.Online(_, var activity) -> switch (activity) {
                case Receiver.Activity.None _ -> Result.succeeded(eventId -> ChatEvent.notified(eventId, message));
                case Receiver.Activity.Chatting(var chattingChannelId) when chattingChannelId.equals(channelId) ->
                        Result.succeeded(eventId -> ChatEvent.sent(eventId, message));
                case Receiver.Activity.Chatting _ -> Result.succeeded(eventId -> ChatEvent.notified(eventId, message));
            };
        };
    }
}
