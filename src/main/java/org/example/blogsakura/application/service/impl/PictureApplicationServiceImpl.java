package org.example.blogsakura.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.example.blogsakura.application.service.SpaceApplicationService;
import org.example.blogsakura.domain.picture.entity.Picture;
import org.example.blogsakura.domain.picture.service.PictureDomainService;
import org.example.blogsakura.domain.space.entity.Space;
import org.example.blogsakura.infrastruct.common.DeleteRequest;
import org.example.blogsakura.infrastruct.exception.BusinessException;
import org.example.blogsakura.infrastruct.exception.ErrorCode;
import org.example.blogsakura.infrastruct.exception.ThrowUtils;
import org.example.blogsakura.infrastruct.mapper.PictureMapper;
import org.example.blogsakura.domain.user.entity.User;
import org.example.blogsakura.interfaces.assembler.PictureAssembler;
import org.example.blogsakura.interfaces.vo.picture.PictureVO;
import org.example.blogsakura.application.service.PictureApplicationService;
import org.example.blogsakura.application.service.UserApplicationService;
import org.example.blogsakura.interfaces.dto.picture.*;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 图片管理 服务层实现。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@Service
@Slf4j
public class PictureApplicationServiceImpl extends ServiceImpl<PictureMapper, Picture> implements PictureApplicationService {

    @Resource
    private UserApplicationService userApplicationService;
    @Resource
    private PictureDomainService pictureDomainService;
    @Resource
    private SpaceApplicationService spaceApplicationService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 构建查询条件
     *
     * @param pictureQueryRequest
     * @return
     */
    @Override
    public QueryWrapper getQueryWrapper(PictureQueryRequest pictureQueryRequest) {
        return pictureDomainService.getQueryWrapper(pictureQueryRequest);
    }


    /**
     * 上传图片到COS，用户目前默认为管理者admin
     * 新增URL方式
     *
     * @param inputSource
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    @Override
    public PictureVO uploadPicture(Object inputSource, PictureUploadRequest pictureUploadRequest, User loginUser) {
        return pictureDomainService.uploadPicture(inputSource, pictureUploadRequest, loginUser);
    }

    /**
     * 获取图片封装（要装入用户信息）
     *
     * @param picture
     * @param request
     * @return
     */
    @Override
    public PictureVO getPictureVO(Picture picture, HttpServletRequest request) {
        PictureVO pictureVO = PictureVO.objToVo(picture);
        // 关联查询用户信息
        Long userId = picture.getUserId();
        if (userId != null && userId > 0) {
            User user = userApplicationService.getById(userId);
            pictureVO.setUser(userApplicationService.getUserVO(user));
        }
        return pictureVO;
    }

    /**
     * 分页获取图片封装
     *
     * @param picturePage
     * @param request
     * @return
     */
    @Override
    public Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request) {
        List<Picture> pictureList = picturePage.getRecords();
        Page<PictureVO> pictureVOPage = new Page<>(
                picturePage.getPageNumber(), picturePage.getPageSize(), picturePage.getTotalRow());
        // pictureList为空时，直接返回pictureVOPage(也是为空)
        if (CollUtil.isEmpty(pictureList)) {
            return pictureVOPage;
        }
        // 对象列表->封装对象列表
        List<PictureVO> pictureVOList = pictureList.stream().map(PictureVO::objToVo).toList();
        // 1. 关联查询的用户信息
        Set<Long> userIdSet = pictureList.stream().map(Picture::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userApplicationService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        pictureVOList.forEach(pictureVO -> {
            Long userId = pictureVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            pictureVO.setUser(userApplicationService.getUserVO(user));
        });
        pictureVOPage.setRecords(pictureVOList);
        return pictureVOPage;
    }


    /**
     * 更新图像（用在前端的用户空间中的更新图像）
     *
     * @param pictureUpdateRequest
     * @param request
     * @return
     */
    @Override
    public boolean updatePicture(PictureUpdateRequest pictureUpdateRequest, HttpServletRequest request) {
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureUpdateRequest, picture);
        // 处理tags
        picture.setTags(JSONUtil.toJsonStr(pictureUpdateRequest.getTags()));
        // 设置编辑时间或更新时间
        picture.setEditTime(LocalDateTime.now());
        // 校验
        Picture.validPicture(picture);
        // 更新图像前，判断id是否存在
        Long pictureId = pictureUpdateRequest.getId();
        Long spaceId = pictureUpdateRequest.getSpaceId();
        if (pictureId != null) {
            Picture oldPicture = this.getById(pictureId);
            if (oldPicture == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片不存在");
            }
            // TODO: 仅本人或管理员可编辑（也就是说，个人图库前端界面如果需要修改，也需要登录）
            User loginUser = userApplicationService.sessionLoginUser(request);
            if (!oldPicture.getUserId().equals(loginUser.getId()) && !loginUser.isAdmin()) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
            // 校验空间是否一致
            // 没传spaceId，则复用原有图片的
            if (spaceId == null) {
                if (oldPicture.getSpaceId() != null) {
                    picture.setSpaceId(oldPicture.getSpaceId());
                }
            } else {
                if (ObjectUtils.notEqual(oldPicture.getSpaceId(), spaceId)) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间id不一致");
                }
                picture.setSpaceId(spaceId);
            }
        }
        // 操作数据库
        boolean result = this.updateById(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }


    /**
     * 针对私有空间上的用户修改文件
     *
     * @param pictureEditRequest
     * @param loginUser
     */
    @Override
    public boolean editPicture(PictureEditRequest pictureEditRequest, User loginUser) {
        return pictureDomainService.editPicture(pictureEditRequest, loginUser);
    }

    /**
     * 保存图片
     *
     * @param pictureVO
     * @return
     */
    @Override
    public Boolean addPicture(PictureVO pictureVO) {
        ThrowUtils.throwIf(pictureVO == null, ErrorCode.PARAMS_ERROR);
        return pictureDomainService.save(PictureAssembler.toPictureEntity(pictureVO));
    }

    /**
     * 删除图片
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @Override
    public Boolean deletePicture(DeleteRequest deleteRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(deleteRequest == null, ErrorCode.PARAMS_ERROR);
        Long id = deleteRequest.getId();
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        Picture picture = pictureDomainService.getById(id);
        User loginUser = userApplicationService.sessionLoginUser(request);
        ThrowUtils.throwIf(picture == null, ErrorCode.PARAMS_ERROR, "图片不存在");
        // 操作数据库
        boolean result = pictureDomainService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return result;
    }

    /**
     * 查询所有封装图片。
     *
     * @return 所有数据
     */
    @Override
    public List<PictureVO> getPictureVOList() {
        return pictureDomainService.list().stream().map(PictureVO::objToVo).toList();
    }

    /**
     * 根据主键获取封装图片（管理员）。
     *
     * @param id
     * @param request
     * @return
     */
    @Override
    public PictureVO getPictureVOById(Long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        Picture picture = pictureDomainService.getById(id);
        if (picture == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片id不存在");
        }
        return this.getPictureVO(picture, request);
    }

    /**
     * 分页查询图片，返回未封装的Picture
     *
     * @param pictureQueryRequest 分页查询请求
     * @return 分页对象
     */
    @Override
    public Page<Picture> getPictureListByPage(PictureQueryRequest pictureQueryRequest) {
        long currentPage = pictureQueryRequest.getCurrentPage();
        long pageSize = pictureQueryRequest.getPageSize();
        // 查询数据库
        return pictureDomainService.page(Page.of(currentPage, pageSize),
                pictureDomainService.getQueryWrapper(pictureQueryRequest));
    }

    /**
     * 分页查询图片，返回封装的Picture
     * 以防万一，补充一下spaceId，表示只查询公共图像
     * 此处只表示公共图库，即后端和前端瀑布流图库，用这个方法
     *
     * @param pictureQueryRequest 分页查询请求
     * @param request
     * @return 分页对象
     */
    @Override
    public Page<PictureVO> getPictureVOListByPage(PictureQueryRequest pictureQueryRequest, HttpServletRequest request) {
        long currentPage = pictureQueryRequest.getCurrentPage();
        long pageSize = pictureQueryRequest.getPageSize();
        pictureQueryRequest.setNullSpaceId(true); // 只查询公共图像
        // 查询数据库
        Page<Picture> picturePage = pictureDomainService.page(Page.of(currentPage, pageSize),
                pictureDomainService.getQueryWrapper(pictureQueryRequest));
        return this.getPictureVOPage(picturePage, request);
    }

    /**
     * 上传图像到COS
     *
     * @param multipartFile
     * @param pictureUploadRequest
     * @param request
     * @return
     */
    @Override
    public PictureVO getUploadPicture(MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest,
                                      HttpServletRequest request) {
        log.info("pictureUploadRequest:{}", JSONUtil.toJsonStr(pictureUploadRequest));
        User loginUser = userApplicationService.sessionLoginUser(request);
        return pictureDomainService.uploadPicture(multipartFile, pictureUploadRequest, loginUser);
    }

    /**
     * 上传URL图像到COS
     *
     * @param pictureUploadRequest
     * @param request
     * @return
     */
    @Override
    public PictureVO getUploadPictureByUrl(PictureUploadRequest pictureUploadRequest, HttpServletRequest request) {
        User loginUser = userApplicationService.sessionLoginUser(request);
        String fileUrl = pictureUploadRequest.getFileUrl();
        return pictureDomainService.uploadPicture(fileUrl, pictureUploadRequest, loginUser);
    }

    /**
     * 在回收箱删除图像，也删除相应cos上的图像
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @Override
    public Boolean deleteDeepPicture(DeleteRequest deleteRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(deleteRequest == null, ErrorCode.PARAMS_ERROR);
        Long id = deleteRequest.getId();
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        boolean result = pictureDomainService.deleteDeepPicture(id);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除图像失败");
        }
        return result;
    }

    /**
     * 获取标签和类别信息
     *
     * @return
     */
    @Override
    public PictureTagCategory getPictureListTagCategory() {
        PictureTagCategory pictureTagCategory = new PictureTagCategory();
        List<String> tagList = Arrays.asList("pixiv", "星空", "森林", "天空", "幻想风", "樱花", "高画质", "芙莉莲", "约会大作战", "城市", "田野", "雪");
        List<String> categoryList = Arrays.asList("风景", "二次元", "游戏", "科技");
        pictureTagCategory.setTagList(tagList);
        pictureTagCategory.setCategoryList(categoryList);
        return pictureTagCategory;
    }

    /**
     * 前端分页查询图片，返回封装的Picture
     *
     * @param pictureQueryRequest 分页查询请求
     * @param request
     * @return 分页对象
     */
    @Override
    public Page<PictureVO> getFrontendPictureVOListByPage(PictureQueryRequest pictureQueryRequest, HttpServletRequest request) {
        long currentPage = pictureQueryRequest.getCurrentPage();
        long pageSize = pictureQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(pageSize > 20, ErrorCode.PARAMS_ERROR);
        // 空间校验
        Long spaceId = pictureQueryRequest.getSpaceId();
        // 查询公开图库
        if (spaceId == null) {
            pictureQueryRequest.setNullSpaceId(true);
        } else {
            // 查询空间类型
            Integer spaceType = spaceApplicationService.getById(spaceId).getSpaceType();
            // 私有空间
            User loginUser = userApplicationService.sessionLoginUser(request);
            Space space = spaceApplicationService.getById(spaceId);
            pictureQueryRequest.setNullSpaceId(false);
            ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
            if (spaceType == 0 && !loginUser.getId().equals(space.getUserId())) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "没有私有空间权限");
            }
        }
        // 查询数据库
        Page<Picture> picturePage = pictureDomainService.page(Page.of(currentPage, pageSize),
                pictureDomainService.getQueryWrapper(pictureQueryRequest));
        return this.getPictureVOPage(picturePage, request);
    }

    /**
     * 加入cache的前端分页
     *
     * @param pictureQueryRequest
     * @param request
     * @return
     */
    @Override
    public Page<PictureVO> getFrontendPictureVOListByPageWithCache(PictureQueryRequest pictureQueryRequest, HttpServletRequest request) {
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
            return cachePage;
        }
        // 查询数据库
        Page<Picture> picturePage = pictureDomainService.page(Page.of(currentPage, pageSize),
                pictureDomainService.getQueryWrapper(pictureQueryRequest));
        // 获取封装类
        Page<PictureVO> pictureVOPage = this.getPictureVOPage(picturePage, request);

        // 存入Redis缓存
        cacheValue = JSONUtil.toJsonStr(pictureVOPage);
        int cacheExpireTime = 300 + RandomUtil.randomInt(0, 30);
        stringStringValueOperations.set(redisKey, cacheValue, cacheExpireTime, TimeUnit.SECONDS);
        return pictureVOPage;
    }

    /**
     * 前端编辑图片
     * TODO 私有空间上，用户或管理员根据更新请求更新图片。
     *
     * @param pictureEditRequest 更新请求
     * @param request
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @Override
    public Boolean editFrontendPicture(PictureUpdateRequest pictureEditRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(pictureEditRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userApplicationService.sessionLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        return this.updatePicture(pictureEditRequest, request);
    }

    /**
     * TODO 私有空间 删除图片
     *
     * @param deleteRequest 删除请求
     * @param request
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @Override
    public Boolean deleteFrontendPicture(DeleteRequest deleteRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(deleteRequest == null, ErrorCode.PARAMS_ERROR);
        Long id = deleteRequest.getId();
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        Picture picture = pictureDomainService.getById(id);
        User loginUser = userApplicationService.sessionLoginUser(request);
        pictureDomainService.checkPictureAuth(loginUser, picture);
        if (picture == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片id不存在");
        }
        return pictureDomainService.deleteFrontendPicture(picture, id);
    }

    /**
     * TODO 针对私有空间的查询图片
     *
     * @param id      图片管理主键
     * @param request
     * @return 图片管理详情
     */
    @Override
    public PictureVO getFrontendPictureVOById(Long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        log.info("测试id:{}", id);
        Picture picture = pictureDomainService.getById(id);
        if (picture == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片id不存在");
        }
        Long spaceId = picture.getSpaceId();
        if (spaceId != null) {
            User loginUser = userApplicationService.sessionLoginUser(request);
            pictureDomainService.checkPictureAuth(loginUser, picture);
        }
        return this.getPictureVO(picture, request);
    }


}
