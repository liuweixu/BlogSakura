package org.example.blogsakura.controller;

import org.example.blogsakura.aop.Log;
import org.example.blogsakura.pojo.Result;
import org.example.blogsakura.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChannelController {

    @Autowired
    private ChannelService channelService;

    @GetMapping("/backend/channels")
    public Result getChannels() {
        return Result.success(channelService.getChannels());
    }

    @GetMapping("/backend/channel/{id}")
    @Log
    public Result getChannelById(@PathVariable String id) {
        return Result.success(channelService.getChannelById(id));
    }
}
