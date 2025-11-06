package org.example.blogsakura2.controller;

import org.example.blogsakura.common.aop.Log;
import org.example.blogsakura2.pojo.Result;
import org.example.blogsakura2.service.ChannelService;
import org.springframework.web.bind.annotation.PathVariable;

//@RestController
public class ChannelController {

    //    @Autowired
    private ChannelService channelService;

    //    @GetMapping("/backend/channels")
    public Result getChannels() {
        return Result.success(channelService.getChannels());
    }

    //    @GetMapping("/backend/channel/{id}")
    @Log
    public Result getChannelById(@PathVariable String id) {
        return Result.success(channelService.getChannelById(id));
    }
}
