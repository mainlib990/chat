package org.mainlib990.chat.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mainlib990.chat.domain.*;
import org.mainlib990.chat.infra.FakeChannelReader;
import org.mainlib990.chat.infra.FakeChatWriter;
import org.mainlib990.chat.infra.FakeUserReader;
import org.mainlib990.core.lib.Result;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SendMessageUseCaseTest {

    private static class Config {

        static final String CHANNEL_ID1 = "199bc1fe-1632-41c2-b197-3ef9f961a84f";
        static final String CHANNEL_ID2 = "2ac0a621-4914-46ef-89c5-c678efb7cd42";
        static final String CHANNEL_ID3 = "4e1807ef-d7df-4692-8712-ec99673e6771";
        static final String SENDER_ID = "06d655ff-9426-4c2c-ba9b-44a1a42972ca";
        static final String RECEIVER_ID1 = "205b6346-019e-4989-9398-618ef1c448e7";
        static final String RECEIVER_ID2 = "0434607a-7cf6-41be-b34a-e4a073911818";
        static final String RECEIVER_ID3 = "e577f779-517d-4741-8fd5-44be387cabfc";
        static final String RECEIVER_ID4 = "54857ac4-94e5-42c8-934f-01731ee18a9d";

        static final Channel.Opened opened1 = new Channel.Opened(new Channel.Id(UUID.fromString(CHANNEL_ID1)));
        static final Channel.Opened opened2 = new Channel.Opened(new Channel.Id(UUID.fromString(CHANNEL_ID2)));
        static final Channel.Closed closed = new Channel.Closed(new Channel.Id(UUID.fromString(CHANNEL_ID3)));
        static final Map<Channel.Id, Channel> channels = Map.ofEntries(
                Map.entry(opened1.id(), opened1),
                Map.entry(opened2.id(), opened2),
                Map.entry(closed.id(), closed)
        );

        static final SenderId senderId = new SenderId(UUID.fromString(SENDER_ID));
        static final Set<SenderId> senders = Set.of(senderId);

        static final Receiver.Offline offline = new Receiver.Offline(
                new Receiver.Id(UUID.fromString(RECEIVER_ID1))
        );
        static final Receiver.Online onlineNone = new Receiver.Online(
                new Receiver.Id(UUID.fromString(RECEIVER_ID2)),
                Receiver.Activity.none()
        );
        static final Receiver.Online onlineChatting1 = new Receiver.Online(
                new Receiver.Id(UUID.fromString(RECEIVER_ID3)),
                new Receiver.Activity.Chatting(opened1.id())
        );
        static final Receiver.Online onlineChatting2 = new Receiver.Online(
                new Receiver.Id(UUID.fromString(RECEIVER_ID4)),
                new Receiver.Activity.Chatting(opened2.id())
        );
        static final Map<Receiver.Id, Receiver> receivers = Map.ofEntries(
                Map.entry(offline.id(), offline),
                Map.entry(onlineNone.id(), onlineNone),
                Map.entry(onlineChatting1.id(), onlineChatting1),
                Map.entry(onlineChatting2.id(), onlineChatting2)
        );

        final FakeChannelReader channelReader;
        final FakeUserReader userReader;
        final ChatPolicy chatPolicy = new ChatPolicy();
        final FakeChatWriter chatWriter = new FakeChatWriter();
        final SendMessageUseCase useCase;

        Config() {
            channelReader = new FakeChannelReader(channels);
            userReader = new FakeUserReader(senders, receivers);
            useCase = new SendMessageUseCase(channelReader, userReader, chatPolicy, chatWriter);
        }
    }

    static final String OPENED_CHANNEL = Config.opened1.id().value().toString();
    static final String SENDER = Config.senderId.value().toString();
    static final String RECEIVER_IN_SAME_CHANNEL = Config.onlineChatting1.id().value().toString();
    static final String RECEIVER_IN_DIFFERENT_CHANNEL = Config.onlineChatting2.id().value().toString();
    static final String RECEIVER_IN_NONE_ACTIVITY = Config.onlineNone.id().value().toString();
    static final String RECEIVER_IN_OFFLINE = Config.offline.id().value().toString();
    static final String TEXT = "Fake Message";

    SendMessageUseCase sut;
    FakeChatWriter chatWriter;

    @BeforeEach
    void setUp() {
        var config = new Config();
        sut = config.useCase;
        chatWriter = config.chatWriter;
    }

    @Nested
    class GivenOpenedChannel {

        @Nested
        class WithOnlineChattingReceiver {

            @Test
            void whenSendingMessageInSameChannel_thenSentEventWritten() {
                var command = new SendMessageCommand(OPENED_CHANNEL, SENDER, RECEIVER_IN_SAME_CHANNEL, TEXT);

                Result<ChatEvent> actual = sut.execute(command);

                assertInstanceOf(Result.Succeeded.class, actual);
                assertTrue(chatWriter.isWrittenSentEvent());
            }

            @Test
            void whenSendingMessageInDifferentChannel_thenNotifiedEventWritten() {
                var command = new SendMessageCommand(OPENED_CHANNEL, SENDER, RECEIVER_IN_DIFFERENT_CHANNEL, TEXT);

                Result<ChatEvent> actual = sut.execute(command);

                assertInstanceOf(Result.Succeeded.class, actual);
                assertTrue(chatWriter.isWrittenNotifiedEvent());
            }
        }

        @Test
        void withOnlineNoneReceiver_whenSendingMessage_thenNotifiedEventWritten() {
            var command = new SendMessageCommand(OPENED_CHANNEL, SENDER, RECEIVER_IN_NONE_ACTIVITY, TEXT);

            Result<ChatEvent> actual = sut.execute(command);

            assertInstanceOf(Result.Succeeded.class, actual);
            assertTrue(chatWriter.isWrittenNotifiedEvent());
        }

        @Test
        void withOfflineReceiver_whenSendingMessage_thenNotifiedEventWritten() {
            var command = new SendMessageCommand(OPENED_CHANNEL, SENDER, RECEIVER_IN_OFFLINE, TEXT);

            Result<ChatEvent> actual = sut.execute(command);

            assertInstanceOf(Result.Succeeded.class, actual);
            assertTrue(chatWriter.isWrittenNotifiedEvent());
        }
    }
}