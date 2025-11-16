package org.example.blogsakura.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.example.blogsakura.common.exception.BusinessException;
import org.example.blogsakura.common.exception.ErrorCode;
import org.example.blogsakura.common.exception.ThrowUtils;
import org.example.blogsakura.manager.cos.CosManager;
import org.example.blogsakura.manager.upload.FilePictureUpload;
import org.example.blogsakura.manager.upload.PictureUploadTemplate;
import org.example.blogsakura.manager.upload.UrlPictureUpload;
import org.example.blogsakura.mapper.SpaceMapper;
import org.example.blogsakura.model.dto.picture.*;
import org.example.blogsakura.mapper.PictureMapper;
import org.example.blogsakura.model.dto.space.Space;
import org.example.blogsakura.model.dto.user.User;
import org.example.blogsakura.model.vo.picture.PictureVO;
import org.example.blogsakura.service.PictureService;
import org.example.blogsakura.service.SpaceService;
import org.example.blogsakura.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 图片管理 服务层实现。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@Service
@Slf4j
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture> implements PictureService {

    @Resource
    private CosManager cosManager;
    @Resource
    private UserService userService;
    @Resource
    private PictureMapper pictureMapper;
    @Resource
    private SpaceService spaceService;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private SpaceMapper spaceMapper;
    @Resource
    private FilePictureUpload filePictureUpload;
    @Resource
    private UrlPictureUpload urlPictureUpload;

    /**
     * 构建查询条件
     *
     * @param pictureQueryRequest
     * @return
     */
    @Override
    public QueryWrapper getQueryWrapper(PictureQueryRequest pictureQueryRequest) {
        QueryWrapper queryWrapper = QueryWrapper.create();
        if (pictureQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = pictureQueryRequest.getId();
        String name = pictureQueryRequest.getName();
        String introduction = pictureQueryRequest.getIntroduction();
        String category = pictureQueryRequest.getCategory();
        List<String> tags = pictureQueryRequest.getTags();
        Long picSize = pictureQueryRequest.getPicSize();
        Integer picWidth = pictureQueryRequest.getPicWidth();
        Integer picHeight = pictureQueryRequest.getPicHeight();
        Double picScale = pictureQueryRequest.getPicScale();
        String picFormat = pictureQueryRequest.getPicFormat();
        String searchText = pictureQueryRequest.getSearchText();
        Long userId = pictureQueryRequest.getUserId();
        String sortField = pictureQueryRequest.getSortField();
        String sortOrder = pictureQueryRequest.getSortOrder();
        Long spaceId = pictureQueryRequest.getSpaceId();
        Boolean nullSpaceId = pictureQueryRequest.getNullSpaceId();
        // 从多字段中搜索
        if (StrUtil.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> {
                qw.like("name", searchText)
                        .like("introduction", searchText);
            });

        }
        queryWrapper.eq("id", id);
        queryWrapper.eq("userId", userId);
        queryWrapper.like("name", name, StringUtils.isNotBlank(name));
        queryWrapper.like("introduction", introduction, StringUtils.isNotBlank(introduction));
        queryWrapper.like("picFormat", picFormat);
        queryWrapper.eq("category", category, StringUtils.isNotBlank(category));
        queryWrapper.eq("picWidth", picWidth);
        queryWrapper.eq("picHeight", picHeight);
        queryWrapper.eq("picSize", picSize);
        queryWrapper.eq("picScale", picScale);
        queryWrapper.eq("spaceId", spaceId, ObjUtil.isNotEmpty(spaceId));
        queryWrapper.isNull("spaceId", nullSpaceId);
        // JSON 数组查询
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // 排序
        queryWrapper.orderBy(sortField, sortOrder.equals("ascend"));
        return queryWrapper;
    }

    /**
     * 获取图片封装，类似脱敏
     *
     * @param picture
     * @param request
     * @return
     */
    @Override
    public Picture getPicureVO(Picture picture, HttpServletRequest request) {
        return null;
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
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NO_AUTH_ERROR);
        ThrowUtils.throwIf(inputSource == null, ErrorCode.PARAMS_ERROR);
        // 新增图片信息或者更新图片信息
        Picture picture = new Picture();
        // 用于判断新增还是更新图片
        Long pictureId = null;
        if (pictureUploadRequest.getId() != null) {
            // 更新图片
            pictureId = pictureUploadRequest.getId();
        }
        // 如果是更新图片，则需要校验图片是否存在
        if (pictureId != null) {
            boolean exist = exists(this.query().eq(Picture::getId, pictureId));
            ThrowUtils.throwIf(!exist, ErrorCode.PARAMS_ERROR);
        }
        // 校验空间是否存在
        Long spaceId = pictureUploadRequest.getSpaceId();
        log.info("空间spaceId:{}", spaceId);
        if (spaceId != null) {
            Space space = spaceService.getById(spaceId);
            ThrowUtils.throwIf(space == null, ErrorCode.PARAMS_ERROR, "空间不存在");
            ThrowUtils.throwIf(!loginUser.getId().equals(space.getUserId()),
                    ErrorCode.NO_AUTH_ERROR, "没有空间权限");
            // 校验额度
            ThrowUtils.throwIf(space.getTotalCount() >= space.getMaxCount(),
                    ErrorCode.OPERATION_ERROR, "空间图像个数不足");
            ThrowUtils.throwIf(space.getTotalSize() >= space.getMaxSize(),
                    ErrorCode.OPERATION_ERROR, "空间大小不足");
            picture.setSpaceId(spaceId);
        }
        // 上传图片得到信息
        String uploadPathPrefix;
        if (spaceId == null) {
            uploadPathPrefix = "/personal_pictures";
        } else {
            uploadPathPrefix = String.format("/space_pictures/%s", spaceId);
        }

//        UploadPictureResult uploadPictureResult = null;
//        if (inputSource instanceof MultipartFile) {
//            uploadPictureResult = cosManager.uploadPicture((MultipartFile) inputSource, uploadPathPrefix);
//        } else {
//            uploadPictureResult = cosManager.uploadPictureByUrl((String) inputSource, uploadPathPrefix);
//        }
        PictureUploadTemplate pictureUploadTemplate = filePictureUpload;
        if (inputSource instanceof String) {
            pictureUploadTemplate = urlPictureUpload;
        }
        UploadPictureResult uploadPictureResult = pictureUploadTemplate.uploadPicture(inputSource, uploadPathPrefix);
        picture.setPicHeight(uploadPictureResult.getPicHeight());
        picture.setPicWidth(uploadPictureResult.getPicWidth());
        picture.setPicFormat(uploadPictureResult.getPicFormat());
        picture.setPicScale(uploadPictureResult.getPicScale());
        picture.setPicSize(uploadPictureResult.getPicSize());
        picture.setUrl(uploadPictureResult.getUrl());
        picture.setName(uploadPictureResult.getPicName());
        picture.setUserId(loginUser.getId());
        // 如果pictureId不为空，表示更新，需要补充id和编辑时间
        if (pictureId != null) {
            picture.setId(pictureId);
            picture.setEditTime(LocalDateTime.now());
            cosManager.deleteCOSPicture(this.getById(pictureId).getUrl());
        }
        Picture finalPicture = picture;
        picture = transactionTemplate.execute(status -> {
            boolean result = this.saveOrUpdate(finalPicture);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "图片上传失败");
            if (spaceId != null) {
                boolean update = spaceMapper.incrementSpaceMySpace(spaceId, finalPicture.getPicSize(), 1);
                ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR, "额度更新失败");
            }
            return finalPicture;
        });
        return PictureVO.objToVo(picture);
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
            User user = userService.getById(userId);
            pictureVO.setUser(userService.getUserVO(user));
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
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        pictureVOList.forEach(pictureVO -> {
            Long userId = pictureVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            pictureVO.setUser(userService.getUserVO(user));
        });
        pictureVOPage.setRecords(pictureVOList);
        return pictureVOPage;
    }

    /**
     * 图片校验方法
     *
     * @param picture
     */
    @Override
    public void validPicture(Picture picture) {
        ThrowUtils.throwIf(picture == null, ErrorCode.PARAMS_ERROR);
        // 从对象中取值
        Long id = picture.getId();
        String url = picture.getUrl();
        String introduction = picture.getIntroduction();
        // 修改数据时，id 不能为空，有参数则校验
        ThrowUtils.throwIf(ObjUtil.isNull(id), ErrorCode.PARAMS_ERROR, "id 不能为空");
        if (StrUtil.isNotBlank(url)) {
            ThrowUtils.throwIf(url.length() > 1024, ErrorCode.PARAMS_ERROR, "url 过长");
        }
        if (StrUtil.isNotBlank(introduction)) {
            ThrowUtils.throwIf(introduction.length() > 800, ErrorCode.PARAMS_ERROR, "简介过长");
        }
    }

    /**
     * 彻底删除图片，需要在回收箱部分删除
     *
     * @param id
     * @return
     */
    @Override
    public boolean deleteDeepPicture(Long id) {
        String url = this.getById(id).getUrl();
        boolean resultDeletePicture = cosManager.deleteCOSPicture(url);
        ThrowUtils.throwIf(!resultDeletePicture, ErrorCode.OPERATION_ERROR);
        return pictureMapper.deleteDeepPicture(id);
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
        this.validPicture(picture);
        // 更新图像前，判断id是否存在
        Long pictureId = pictureUpdateRequest.getId();
        Long spaceId = pictureUpdateRequest.getSpaceId();
        if (pictureId != null) {
            Picture oldPicture = this.getById(pictureId);
            if (oldPicture == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片不存在");
            }
            // TODO: 仅本人或管理员可编辑（也就是说，个人图库前端界面如果需要修改，也需要登录）
            User loginUser = userService.sessionLoginUser(request);
            if (!oldPicture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
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
     * 图像处理的权限逻辑 针对私有空间的特殊操作，比如私有空间的删除和编辑图像（系统管理员不能修改，只能用户自己修改）
     *
     * @param loginUser
     * @param picture
     */
    @Override
    public void checkPictureAuth(User loginUser, Picture picture) {
        Long spaceId = picture.getSpaceId();
        if (spaceId == null) {
            // 公共图库，仅本人或管理员可操作
            if (!picture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        } else {
            Space space = spaceService.getById(spaceId);
            Integer spaceType = space.getSpaceType();
            // 私有空间，仅空间管理员可操作
            if (spaceType == 0 && !picture.getUserId().equals(loginUser.getId())) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }
    }

    /**
     * 针对私有空间上的用户修改文件
     *
     * @param pictureEditRequest
     * @param loginUser
     */
    @Override
    public boolean editPicture(PictureEditRequest pictureEditRequest, User loginUser) {
        // 在此处将实体类和 DTO 进行转换
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureEditRequest, picture);
        // 注意将 list 转为 string
        picture.setTags(JSONUtil.toJsonStr(pictureEditRequest.getTags()));
        // 设置编辑时间
        picture.setEditTime(LocalDateTime.now());
        // 数据校验
        this.validPicture(picture);
        // 判断是否存在
        long id = pictureEditRequest.getId();
        Picture oldPicture = this.getById(id);
        ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR);
        // 校验权限
        checkPictureAuth(loginUser, oldPicture);
        // 操作数据库
        boolean result = this.updateById(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

}
