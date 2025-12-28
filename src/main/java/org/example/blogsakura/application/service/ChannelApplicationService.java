package org.example.blogsakura.application.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import org.example.blogsakura.domain.blog.channel.entity.Channel;
import org.example.blogsakura.infrastruct.common.DeleteRequest;
import org.example.blogsakura.interfaces.dto.blog.channel.ChannelQueryRequest;
import org.example.blogsakura.interfaces.vo.blog.channel.ChannelVO;

import java.util.List;

/**
 * 频道表 服务层。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
public interface ChannelApplicationService extends IService<Channel> {

    /**
     * 分页查询条件
     *
     * @param channelQueryRequest
     * @return
     */
    QueryWrapper getQueryWrapper(ChannelQueryRequest channelQueryRequest);

    /**
     * 添加频道
     *
     * @param channel
     * @return
     */
    Boolean addChannel(Channel channel);

    /**
     * 删除频道
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    Boolean deleteChannel(DeleteRequest deleteRequest, HttpServletRequest request);

    /**
     * 更新频道
     *
     * @param channel
     * @return
     */
    Boolean updateChannel(Channel channel);

    /**
     * 查询所有频道列表
     *
     * @return
     */
    List<ChannelVO> getChannelVOlist();

    /**
     * 根据主键获取频道
     *
     * @param id
     * @return
     */
    ChannelVO getChannelVOById(Long id);

    /**
     * 分页查询频道
     *
     * @param channelQueryRequest
     * @return
     */
    Page<ChannelVO> getChannelVOListByPage(ChannelQueryRequest channelQueryRequest);

}
