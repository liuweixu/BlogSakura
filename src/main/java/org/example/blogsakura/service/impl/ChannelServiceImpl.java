package org.example.blogsakura.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.blogsakura.common.exception.ErrorCode;
import org.example.blogsakura.common.exception.ThrowUtils;
import org.example.blogsakura.mapper.ArticleMapper;
import org.example.blogsakura.model.dto.channel.Channel;
import org.example.blogsakura.mapper.ChannelMapper;
import org.example.blogsakura.model.dto.channel.ChannelQueryRequest;
import org.example.blogsakura.model.vo.channel.ChannelVO;
import org.example.blogsakura.service.ChannelService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 频道表 服务层实现。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@Service
public class ChannelServiceImpl extends ServiceImpl<ChannelMapper, Channel> implements ChannelService {

    @Resource
    private ArticleMapper articleMapper;

    /**
     * 获取供给前端用的频道数据
     *
     * @param channel
     * @return
     */
    @Override
    public ChannelVO getChannelVO(Channel channel) {
        ThrowUtils.throwIf(channel == null, ErrorCode.PARAMS_ERROR, "频道不存在");
        ChannelVO channelVO = new ChannelVO();
        BeanUtils.copyProperties(channel, channelVO);
        Long channelVOId = channelVO.getId();
        long countArticlesByChannelId = articleMapper.countArticlesByChannelId(channelVOId);
        channelVO.setArticleNumbers(countArticlesByChannelId);
        return channelVO;
    }

    @Override
    public List<ChannelVO> getChannelVOList(List<Channel> channelList) {
        ThrowUtils.throwIf(channelList == null, ErrorCode.PARAMS_ERROR, "频道列表不存在");
        return channelList.stream().map(this::getChannelVO).collect(Collectors.toList());
    }

    /**
     * 分页查询条件
     *
     * @param channelQueryRequest
     * @return
     */
    @Override
    public QueryWrapper getQueryWrapper(ChannelQueryRequest channelQueryRequest) {
        ThrowUtils.throwIf(channelQueryRequest == null, ErrorCode.PARAMS_ERROR);
        Long id = channelQueryRequest.getId();
        String channel = channelQueryRequest.getChannel();
        LocalDateTime createTime = channelQueryRequest.getCreateTime();
        LocalDateTime updateTime = channelQueryRequest.getUpdateTime();
        String sortField = channelQueryRequest.getSortField();
        String sortOrder = channelQueryRequest.getSortOrder();
        return QueryWrapper.create()
                .eq("id", id)
                .like("channel", channel)
                .orderBy(sortField, "ascend".equals(sortOrder));
    }
}
