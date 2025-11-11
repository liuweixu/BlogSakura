package org.example.blogsakura.controller.frontend;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.example.blogsakura.common.common.BaseResponse;
import org.example.blogsakura.common.common.ResultUtils;
import org.example.blogsakura.common.exception.BusinessException;
import org.example.blogsakura.common.exception.ErrorCode;
import org.example.blogsakura.common.exception.ThrowUtils;
import org.example.blogsakura.model.dto.picture.Picture;
import org.example.blogsakura.model.dto.picture.PictureQueryRequest;
import org.example.blogsakura.model.dto.picture.PictureUpdateRequest;
import org.example.blogsakura.model.dto.user.User;
import org.example.blogsakura.model.vo.picture.PictureVO;
import org.example.blogsakura.service.PictureService;
import org.example.blogsakura.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/picture")
@Slf4j
public class PictureFrontendController {

    @Resource
    private PictureService pictureService;
    @Resource
    private UserService userService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 前端根据主键获取图片。
     *
     * @param id 图片管理主键
     * @return 图片管理详情
     */
    @GetMapping("/{id}")
    public BaseResponse<PictureVO> getFrontendPictureVOById(@PathVariable Long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        Picture picture = pictureService.getById(id);
        if (picture == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片id不存在");
        }
        return ResultUtils.success(pictureService.getPictureVO(picture, request));
    }

    /**
     * 分页查询图片，返回封装的Picture
     *
     * @param pictureQueryRequest 分页查询请求
     * @return 分页对象
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<PictureVO>> getFrontendPictureVOListByPage(@RequestBody PictureQueryRequest pictureQueryRequest,
                                                                        HttpServletRequest request) {
        long currentPage = pictureQueryRequest.getCurrentPage();
        long pageSize = pictureQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Picture> picturePage = pictureService.page(Page.of(currentPage, pageSize),
                pictureService.getQueryWrapper(pictureQueryRequest));
        return ResultUtils.success(pictureService.getPictureVOPage(picturePage, request));
    }

    @PostMapping("/list/page/vo/cache")
    public BaseResponse<Page<PictureVO>> getFrontendPictureVOListByPageWithCache(
            @RequestBody PictureQueryRequest pictureQueryRequest,
            HttpServletRequest request
    ) {
        long currentPage = pictureQueryRequest.getCurrentPage();
        long pageSize = pictureQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR);
        // 构建缓存key
        String queryCondition = JSONUtil.toJsonStr(pictureQueryRequest);
        String hashKey = DigestUtils.md5DigestAsHex(queryCondition.getBytes());
        String redisKey = "personal_picture:getPictureVOByPage:" + hashKey;
        // 从Redis缓存中查询
        ValueOperations<String, String> stringStringValueOperations = stringRedisTemplate.opsForValue();
        String cacheValue = stringStringValueOperations.get(redisKey);
        if (cacheValue != null) {
            // 缓存命中
            log.info("缓存命中");
            Page<PictureVO> cachePage = JSONUtil.toBean(cacheValue, Page.class);
            return ResultUtils.success(cachePage);
        }
        // 查询数据库
        Page<Picture> picturePage = pictureService.page(Page.of(currentPage, pageSize),
                pictureService.getQueryWrapper(pictureQueryRequest));
        // 获取封装类
        Page<PictureVO> pictureVOPage = pictureService.getPictureVOPage(picturePage, request);

        // 存入Redis缓存
        cacheValue = JSONUtil.toJsonStr(pictureVOPage);
        int cacheExpireTime = 300 + RandomUtil.randomInt(0, 30);
        stringStringValueOperations.set(redisKey, cacheValue, cacheExpireTime, TimeUnit.SECONDS);
        return ResultUtils.success(pictureVOPage);
    }

    /**
     * 前端编辑图片
     * 根据更新请求更新图片。
     *
     * @param pictureUpdateRequest 更新请求
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("/")
    public BaseResponse<Boolean> updateFrontendPicture(@RequestBody PictureUpdateRequest pictureUpdateRequest,
                                                       HttpServletRequest request) {
        ThrowUtils.throwIf(pictureUpdateRequest == null, ErrorCode.PARAMS_ERROR);
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureUpdateRequest, picture);
        // 处理tags
        picture.setTags(JSONUtil.toJsonStr(pictureUpdateRequest.getTags()));
        // 设置编辑时间或更新时间
        picture.setEditTime(LocalDateTime.now());
        // 校验
        pictureService.validPicture(picture);
        // 更新图像前，判断id是否存在
        Long id = pictureUpdateRequest.getId();
        Picture oldPicture = pictureService.getById(id);
        if (oldPicture == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片不存在");
        }
        // TODO: 仅本人或管理员可编辑（也就是说，个人图库前端界面如果需要修改，也需要登录）
        User loginUser = userService.sessionLoginUser(request);
        if (!oldPicture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = pictureService.updateById(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }


}
