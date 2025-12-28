package org.example.blogsakuraDDD.application.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.example.blogsakuraDDD.domain.blog.channel.service.ChannelDomainService;
import org.example.blogsakuraDDD.infrastruct.common.DeleteRequest;
import org.example.blogsakuraDDD.infrastruct.exception.ErrorCode;
import org.example.blogsakuraDDD.infrastruct.exception.ThrowUtils;
import org.example.blogsakuraDDD.domain.blog.channel.entity.Channel;
import org.example.blogsakuraDDD.infrastruct.mapper.ChannelMapper;
import org.example.blogsakuraDDD.interfaces.dto.blog.channel.ChannelQueryRequest;
import org.example.blogsakuraDDD.interfaces.vo.blog.channel.ChannelVO;
import org.example.blogsakuraDDD.application.service.ChannelApplicationService;
import org.springframework.stereotype.Service;


import java.util.List;

/**
 * 频道表 服务层实现。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@Service
public class ChannelApplicationServiceImpl extends ServiceImpl<ChannelMapper, Channel> implements ChannelApplicationService {


    @Resource
    private ChannelDomainService channelDomainService;

    /**
     * 分页查询条件
     *
     * @param channelQueryRequest
     * @return
     */
    @Override
    public QueryWrapper getQueryWrapper(ChannelQueryRequest channelQueryRequest) {
        return channelDomainService.getQueryWrapper(channelQueryRequest);
    }

    /**
     * 添加频道
     *
     * @param channel
     * @return
     */
    @Override
    public Boolean addChannel(Channel channel) {
        ThrowUtils.throwIf(channel == null, ErrorCode.PARAMS_ERROR, "请求为空");
        return channelDomainService.save(channel);
    }

    /**
     * 删除频道
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @Override
    public Boolean deleteChannel(DeleteRequest deleteRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(deleteRequest == null, ErrorCode.PARAMS_ERROR);
        Long id = deleteRequest.getId();
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        return channelDomainService.removeById(id);
    }

    /**
     * 更新频道
     *
     * @param channel
     * @return
     */
    @Override
    public Boolean updateChannel(Channel channel) {
        ThrowUtils.throwIf(channel == null, ErrorCode.PARAMS_ERROR);
        return channelDomainService.updateById(channel);
    }

    /**
     * 查询所有频道列表
     *
     * @return
     */
    @Override
    public List<ChannelVO> getChannelVOlist() {
        List<Channel> channelList = channelDomainService.list();
        return channelDomainService.getChannelVOList(channelList);
    }

    /**
     * 根据主键获取频道
     *
     * @param id
     * @return
     */
    @Override
    public ChannelVO getChannelVOById(Long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        Channel channel = channelDomainService.getById(id);
        return channelDomainService.getChannelVO(channel);
    }

    /**
     * 分页查询频道
     *
     * @param channelQueryRequest
     * @return
     */
    @Override
    public Page<ChannelVO> getChannelVOListByPage(ChannelQueryRequest channelQueryRequest) {
        ThrowUtils.throwIf(channelQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long currentPage = channelQueryRequest.getCurrentPage();
        long pageSize = channelQueryRequest.getPageSize();
        Page<Channel> channelPage = channelDomainService.page(Page.of(currentPage, pageSize),
                channelDomainService.getQueryWrapper(channelQueryRequest));
        // 数据脱敏
        Page<ChannelVO> channelVOPage = new Page<>(currentPage, pageSize, channelPage.getTotalRow());
        List<ChannelVO> channelVOList = channelDomainService.getChannelVOList(channelPage.getRecords());
        channelVOPage.setRecords(channelVOList);
        return channelVOPage;
    }
}
