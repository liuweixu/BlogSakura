package org.example.blogsakura.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import org.example.blogsakura.model.dto.article.ArticleQueryRequest;
import org.example.blogsakura.model.dto.channel.Channel;
import org.example.blogsakura.model.dto.channel.ChannelQueryRequest;
import org.example.blogsakura.model.vo.channel.ChannelVO;

import java.util.List;

/**
 * 频道表 服务层。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
public interface ChannelService extends IService<Channel> {

    /**
     * 获取供给前端用的频道数据
     *
     * @param channel
     * @return
     */
    ChannelVO getChannelVO(Channel channel);

    /**
     * 获取供给前端用的频道数据列表
     *
     * @param channelList
     * @return
     */
    List<ChannelVO> getChannelVOList(List<Channel> channelList);

    /**
     * 分页查询条件
     *
     * @param channelQueryRequest
     * @return
     */
    QueryWrapper getQueryWrapper(ChannelQueryRequest channelQueryRequest);
}
