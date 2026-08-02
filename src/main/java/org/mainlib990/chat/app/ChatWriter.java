package org.mainlib990.chat.app;

import org.mainlib990.chat.domain.ChatEvent;

public interface ChatWriter {

    void write(ChatEvent chatEvent);
}
