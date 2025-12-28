package org.example.blogsakuraDDD.domain.blog.channel.service.impl;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.blogsakuraDDD.application.service.ChannelApplicationService;
import org.example.blogsakuraDDD.domain.blog.article.entity.Article;
import org.example.blogsakuraDDD.domain.blog.article.service.ArticleDomainService;
import org.example.blogsakuraDDD.domain.blog.channel.entity.Channel;
import org.example.blogsakuraDDD.domain.blog.channel.service.ChannelDomainService;
import org.example.blogsakuraDDD.infrastruct.constants.RabbitMQConstants;
import org.example.blogsakuraDDD.infrastruct.exception.ErrorCode;
import org.example.blogsakuraDDD.infrastruct.exception.ThrowUtils;
import org.example.blogsakuraDDD.infrastruct.manager.BloomFilter.ArticleBloomFilter;
import org.example.blogsakuraDDD.infrastruct.manager.cos.CosManager;
import org.example.blogsakuraDDD.infrastruct.mapper.ArticleMapper;
import org.example.blogsakuraDDD.infrastruct.mapper.ChannelMapper;
import org.example.blogsakuraDDD.interfaces.assembler.ArticleAssembler;
import org.example.blogsakuraDDD.interfaces.dto.blog.channel.ChannelQueryRequest;
import org.example.blogsakuraDDD.interfaces.vo.blog.article.ArticleVO;
import org.example.blogsakuraDDD.interfaces.vo.blog.channel.ChannelVO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
public class ChannelDomainServiceImpl extends ServiceImpl<ChannelMapper, Channel> implements ChannelDomainService {

    @Resource
    private ChannelMapper channelMapper;

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
        long countArticlesByChannelId = channelMapper.countArticlesByChannelId(channelVOId);
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
