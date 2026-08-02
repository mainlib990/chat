package org.mainlib990.chat.infra;

import org.mainlib990.chat.app.ChatWriter;
import org.mainlib990.chat.domain.ChatEvent;

public class FakeChatWriter implements ChatWriter {

    private boolean isWrittenNotifiedEvent = false;
    private boolean isWrittenSentEvent = false;

    @Override
    public void write(ChatEvent chatEvent) {
        switch (chatEvent) {
            case ChatEvent.Notified _ -> isWrittenNotifiedEvent = true;
            case ChatEvent.Sent _ -> isWrittenSentEvent = true;
        }
    }

    public boolean isWrittenNotifiedEvent() {
        return isWrittenNotifiedEvent;
    }

    public boolean isWrittenSentEvent() {
        return isWrittenSentEvent;
    }
}
