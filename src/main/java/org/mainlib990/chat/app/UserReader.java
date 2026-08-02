package org.mainlib990.chat.app;

import org.mainlib990.chat.domain.Receiver;
import org.mainlib990.chat.domain.SenderId;
import org.mainlib990.core.lib.Result;

public interface UserReader {

    Result<Void> existsSender(SenderId senderId);

    Result<Receiver> read(Receiver.Id receiverId);
}
