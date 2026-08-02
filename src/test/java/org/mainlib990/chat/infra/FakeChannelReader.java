package org.mainlib990.chat.infra;

import org.mainlib990.chat.app.ChannelReader;
import org.mainlib990.chat.domain.Channel;
import org.mainlib990.core.lib.Result;

import java.util.Map;

public class FakeChannelReader implements ChannelReader {

    private final Map<Channel.Id, Channel> channels;

    public FakeChannelReader(Map<Channel.Id, Channel> channels) {
        this.channels = channels;
    }

    @Override
    public Result<Channel> read(Channel.Id channelId) {
        Channel channel = channels.get(channelId);
        if (channel == null) {
            return Result.failed("존재하지 않는 채널입니다: " + channelId);
        }
        return Result.succeeded(channel);
    }
}
