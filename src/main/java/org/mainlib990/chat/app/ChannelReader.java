package org.mainlib990.chat.app;

import org.mainlib990.chat.domain.Channel;
import org.mainlib990.core.lib.Result;

public interface ChannelReader {

    Result<Channel> read(Channel.Id channelId);
}
