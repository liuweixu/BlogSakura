package org.example.blogsakura.controller.backend;

import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.example.blogsakura.common.common.BaseResponse;
import org.example.blogsakura.common.common.DeleteRequest;
import org.example.blogsakura.common.common.ResultUtils;
import org.example.blogsakura.common.exception.ErrorCode;
import org.example.blogsakura.common.exception.ThrowUtils;
import org.example.blogsakura.model.dto.channel.ChannelQueryRequest;
import org.example.blogsakura.model.vo.channel.ChannelVO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.example.blogsakura.model.dto.channel.Channel;
import org.example.blogsakura.service.ChannelService;
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
    private ChannelService channelService;

    /**
     * 保存频道表。
     *
     * @param channel 频道表
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("/")
    public BaseResponse<Boolean> addChannel(@RequestBody Channel channel) {
        return ResultUtils.success(channelService.save(channel));
    }

    /**
     * 删除频道表。
     *
     * @param deleteRequest 根据删除请求删除文章
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("/")
    public BaseResponse<Boolean> deleteChannel(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(deleteRequest == null, ErrorCode.PARAMS_ERROR);
        Long id = deleteRequest.getId();
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(channelService.removeById(id));
    }

    /**
     * 根据主键更新频道表。
     *
     * @param channel 频道表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("/")
    public BaseResponse<Boolean> updateChannel(@RequestBody Channel channel) {
        return ResultUtils.success(channelService.updateById(channel));
    }

    /**
     * 查询所有频道表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public BaseResponse<List<ChannelVO>> getChannelVOlist() {
        List<Channel> channelList = channelService.list();
        return ResultUtils.success(channelService.getChannelVOList(channelList));
    }

    /**
     * 根据主键获取频道表。
     *
     * @param id 频道表主键
     * @return 频道表详情
     */
    @GetMapping("/{id}")
    public BaseResponse<ChannelVO> getChannelVOById(@PathVariable Long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        Channel channel = channelService.getById(id);
        return ResultUtils.success(channelService.getChannelVO(channel));
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
        ThrowUtils.throwIf(channelQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long currentPage = channelQueryRequest.getCurrentPage();
        long pageSize = channelQueryRequest.getPageSize();
        Page<Channel> channelPage = channelService.page(Page.of(currentPage, pageSize),
                channelService.getQueryWrapper(channelQueryRequest));
        // 数据脱敏
        Page<ChannelVO> channelVOPage = new Page<>(currentPage, pageSize, channelPage.getTotalRow());
        List<ChannelVO> channelVOList = channelService.getChannelVOList(channelPage.getRecords());
        channelVOPage.setRecords(channelVOList);
        return ResultUtils.success(channelVOPage);
    }

}
