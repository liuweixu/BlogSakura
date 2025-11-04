package org.example.blogsakura.service;

import org.example.blogsakura.pojo.Channel;

import java.util.List;

public interface ChannelService {

    public List<Channel> getChannels();

    public Channel getChannelById(String id);
}
