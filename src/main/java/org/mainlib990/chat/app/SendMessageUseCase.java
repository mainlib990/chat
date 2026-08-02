package org.mainlib990.chat.app;

import org.mainlib990.chat.domain.*;
import org.mainlib990.core.lib.Result;
import org.mainlib990.core.lib.Validate;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SendMessageUseCase {

    private final ChannelReader channelReader;
    private final UserReader userReader;
    private final ChatPolicy chatPolicy;
    private final ChatWriter chatWriter;

    public SendMessageUseCase(
            ChannelReader channelReader,
            UserReader userReader,
            ChatPolicy chatPolicy,
            ChatWriter chatWriter
    ) {
        this.channelReader = channelReader;
        this.userReader = userReader;
        this.chatPolicy = chatPolicy;
        this.chatWriter = chatWriter;
    }

    public Result<ChatEvent> execute(SendMessageCommand command) {
        return execute(command.channelId(), command.senderId(), command.receiverId(), command.text());
    }

    private Result<ChatEvent> execute(
            String channelIdCandidate,
            String senderIdCandidate,
            String receiverIdCandidate,
            String textCandidate
    ) {
        Validate<Channel.Id, String> channelId = Channel.Id.parse(channelIdCandidate);
        Validate<SenderId, String> senderId = SenderId.parse(senderIdCandidate);
        Validate<Receiver.Id, String> receiverId = Receiver.Id.parse(receiverIdCandidate);
        Validate<Content, String> content = Content.parse(textCandidate);

        Validate<Void, List<String>> validates = Validate.allOf(channelId, senderId, receiverId, content);
        if (validates instanceof Validate.Invalidated(var r)) {
            return r.stream()
                    .collect(Collectors.collectingAndThen(
                            Collectors.joining(System.lineSeparator()),
                            Result::failed
                    ));
        }
        return execute(channelId.orElseThrow(), senderId.orElseThrow(), receiverId.orElseThrow(), content.orElseThrow());
    }

    private Result<ChatEvent> execute(
            Channel.Id channelId,
            SenderId senderId,
            Receiver.Id receiverId,
            Content content
    ) {
        Result<Channel> channel = channelReader.read(channelId);
        if (channel instanceof Result.Failed(var message)) {
            return Result.failed(message);
        }
        Result<Void> sender = userReader.existsSender(senderId);
        if (sender instanceof Result.Failed(var message)) {
            return Result.failed(message);
        }
        Result<Receiver> receiver = userReader.read(receiverId);
        if (receiver instanceof Result.Failed(var message)) {
            return Result.failed(message);
        }

        var messageId = new Message.Id(UUID.randomUUID());
        Instant createdAt = Instant.now();
        var message = new Message(
                messageId,
                channel.getValue(),
                createdAt,
                senderId,
                receiver.getValue(),
                content
        );
        Result<Function<ChatEvent.Id, ChatEvent>> eventFactory = chatPolicy.sendMessage(message);

        Result<ChatEvent> chatEvent = eventFactory.map(
                factory -> factory.apply(new ChatEvent.Id(UUID.randomUUID()))
        );
        chatEvent.ifPresent(chatWriter::write);
        return chatEvent;
    }
}
