package org.example.blogsakura.controller.frontend;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.example.blogsakura.common.annotation.AuthCheck;
import org.example.blogsakura.common.common.BaseResponse;
import org.example.blogsakura.common.common.DeleteRequest;
import org.example.blogsakura.common.common.ResultUtils;
import org.example.blogsakura.common.constants.SpaceUserPermissionConstant;
import org.example.blogsakura.common.constants.UserConstant;
import org.example.blogsakura.common.exception.BusinessException;
import org.example.blogsakura.common.exception.ErrorCode;
import org.example.blogsakura.common.exception.ThrowUtils;
import org.example.blogsakura.mapper.SpaceMapper;
import org.example.blogsakura.model.dto.picture.Picture;
import org.example.blogsakura.model.dto.picture.PictureEditRequest;
import org.example.blogsakura.model.dto.picture.PictureQueryRequest;
import org.example.blogsakura.model.dto.picture.PictureUpdateRequest;
import org.example.blogsakura.model.dto.space.Space;
import org.example.blogsakura.model.dto.user.User;
import org.example.blogsakura.model.vo.picture.PictureVO;
import org.example.blogsakura.service.PictureService;
import org.example.blogsakura.service.SpaceService;
import org.example.blogsakura.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.transaction.support.TransactionTemplate;
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
    @Resource
    private SpaceService spaceService;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private SpaceMapper spaceMapper;

    /**
     * 分页查询图片，返回封装的Picture
     * 前端中，需要查询的接口有：1. 图库（瀑布流展示，属于公共图像） 2. 私有空间，涉及到用户自己私有图像和公开图像
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
        // 空间校验
        Long spaceId = pictureQueryRequest.getSpaceId();
        // 查询空间类型
        Integer spaceType = spaceService.getById(spaceId).getSpaceType();
        // 查询公开图库
        if (spaceId == null) {
            pictureQueryRequest.setNullSpaceId(true);
        } else {
            // 私有空间
            User loginUser = userService.sessionLoginUser(request);
            Space space = spaceService.getById(spaceId);
            log.info("space.getUserId():{}", space.getUserId());
            log.info("loginUser.getId():{}", loginUser.getId());
            pictureQueryRequest.setNullSpaceId(false);
            ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
            if (spaceType == 0 && !loginUser.getId().equals(space.getUserId())) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "没有私有空间权限");
            }
        }
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
     * TODO 私有空间上，用户或管理员根据更新请求更新图片。
     *
     * @param pictureEditRequest 更新请求
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("/")
    public BaseResponse<Boolean> editFrontendPicture(@RequestBody PictureUpdateRequest pictureEditRequest,
                                                     HttpServletRequest request) {
        ThrowUtils.throwIf(pictureEditRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.sessionLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        boolean result = pictureService.updatePicture(pictureEditRequest, request);
        return ResultUtils.success(result);
    }

    /**
     * TODO 私有空间 删除图片
     *
     * @param deleteRequest 删除请求
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("/")
    public BaseResponse<Boolean> deleteFrontendPicture(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(deleteRequest == null, ErrorCode.PARAMS_ERROR);
        Long id = deleteRequest.getId();
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        Picture picture = pictureService.getById(id);
        User loginUser = userService.sessionLoginUser(request);
        pictureService.checkPictureAuth(loginUser, picture);
        if (picture == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片id不存在");
        }
        // 开启事务
        transactionTemplate.execute(status -> {
            // 操作数据库
            boolean result = pictureService.removeById(id);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
            // 释放额度
            Long spaceId = picture.getSpaceId();
            if (spaceId != null) {
                boolean update = spaceMapper.decrementSpaceMySpace(spaceId, picture.getPicSize(), 1);
                ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR, "额度更新失败");
            }
            pictureService.deleteDeepPicture(id);
            return true;
        });

        // 操作数据库
        boolean result = pictureService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(result);
    }

    /**
     * TODO 针对私有空间的查询图片
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
        Long spaceId = picture.getSpaceId();
        if (spaceId != null) {
            User loginUser = userService.sessionLoginUser(request);
            pictureService.checkPictureAuth(loginUser, picture);
        }
        return ResultUtils.success(pictureService.getPictureVO(picture, request));
    }
}
