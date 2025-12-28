package org.example.blogsakuraDDD.interfaces.controller;

import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.example.blogsakuraDDD.infrastruct.common.BaseResponse;
import org.example.blogsakuraDDD.infrastruct.common.DeleteRequest;
import org.example.blogsakuraDDD.infrastruct.common.ResultUtils;
import org.example.blogsakuraDDD.infrastruct.exception.ErrorCode;
import org.example.blogsakuraDDD.infrastruct.exception.ThrowUtils;
import org.example.blogsakuraDDD.interfaces.dto.blog.channel.ChannelQueryRequest;
import org.example.blogsakuraDDD.interfaces.vo.blog.channel.ChannelVO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.example.blogsakuraDDD.domain.blog.channel.entity.Channel;
import org.example.blogsakuraDDD.application.service.ChannelApplicationService;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 频道表 控制层。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@RestController
@RequestMapping("/backend/channel")
public class ChannelController {

    @Resource
    private ChannelApplicationService channelApplicationService;

    /**
     * 保存频道表。
     *
     * @param channel 频道表
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("/")
    public BaseResponse<Boolean> addChannel(@RequestBody Channel channel) {
        return ResultUtils.success(channelApplicationService.addChannel(channel));
    }

    /**
     * 删除频道表。
     *
     * @param deleteRequest 根据删除请求删除文章
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("/")
    public BaseResponse<Boolean> deleteChannel(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        return ResultUtils.success(channelApplicationService.deleteChannel(deleteRequest, request));
    }

    /**
     * 根据主键更新频道表。
     *
     * @param channel 频道表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("/")
    public BaseResponse<Boolean> updateChannel(@RequestBody Channel channel) {
        return ResultUtils.success(channelApplicationService.updateChannel(channel));
    }

    /**
     * 查询所有频道表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public BaseResponse<List<ChannelVO>> getChannelVOlist() {
        return ResultUtils.success(channelApplicationService.getChannelVOlist());
    }

    /**
     * 根据主键获取频道表。
     *
     * @param id 频道表主键
     * @return 频道表详情
     */
    @GetMapping("/{id}")
    public BaseResponse<ChannelVO> getChannelVOById(@PathVariable Long id) {
        return ResultUtils.success(channelApplicationService.getChannelVOById(id));
    }

    /**
     * 分页查询频道表。
     * <p>
     * channelQueryRequest 分页请求
     *
     * @return 分页对象
     */
    @PostMapping("list/page/vo")
    public BaseResponse<Page<ChannelVO>> getChannelVOListByPage(@RequestBody ChannelQueryRequest channelQueryRequest) {
        return ResultUtils.success(channelApplicationService.getChannelVOListByPage(channelQueryRequest));
    }

}
