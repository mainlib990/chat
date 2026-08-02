package org.mainlib990.chat.infra;

import org.mainlib990.chat.app.UserReader;
import org.mainlib990.chat.domain.Receiver;
import org.mainlib990.chat.domain.SenderId;
import org.mainlib990.core.lib.Result;

import java.util.Map;
import java.util.Set;

public class FakeUserReader implements UserReader {

    private final Set<SenderId> senders;
    private final Map<Receiver.Id, Receiver> receivers;

    public FakeUserReader(Set<SenderId> senders, Map<Receiver.Id, Receiver> receivers) {
        this.senders = senders;
        this.receivers = receivers;
    }

    @Override
    public Result<Void> existsSender(SenderId senderId) {
        if (!senders.contains(senderId)) {
            return Result.failed("존재하지 않는 송신자 입니다: " + senderId);
        }
        return Result.emptySucceeded();
    }

    @Override
    public Result<Receiver> read(Receiver.Id receiverId) {
        Receiver receiver = receivers.get(receiverId);
        if (receiver == null) {
            return Result.failed("존재하지 않는 수신자입니다: " + receiverId);
        }
        return Result.succeeded(receiver);
    }
}
