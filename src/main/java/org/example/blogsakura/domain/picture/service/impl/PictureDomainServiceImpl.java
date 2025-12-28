package org.example.blogsakura.domain.picture.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.blogsakura.application.service.SpaceApplicationService;
import org.example.blogsakura.domain.picture.entity.Picture;
import org.example.blogsakura.domain.picture.service.PictureDomainService;
import org.example.blogsakura.domain.space.entity.Space;
import org.example.blogsakura.domain.user.entity.User;
import org.example.blogsakura.domain.user.service.UserDomainService;
import org.example.blogsakura.infrastruct.exception.BusinessException;
import org.example.blogsakura.infrastruct.exception.ErrorCode;
import org.example.blogsakura.infrastruct.exception.ThrowUtils;
import org.example.blogsakura.infrastruct.manager.cos.CosManager;
import org.example.blogsakura.infrastruct.manager.upload.FilePictureUpload;
import org.example.blogsakura.infrastruct.manager.upload.PictureUploadTemplate;
import org.example.blogsakura.infrastruct.manager.upload.UrlPictureUpload;
import org.example.blogsakura.infrastruct.mapper.PictureMapper;
import org.example.blogsakura.infrastruct.mapper.SpaceMapper;
import org.example.blogsakura.interfaces.dto.picture.*;
import org.example.blogsakura.interfaces.vo.picture.PictureVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 图片管理 服务层实现。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@Service
@Slf4j
public class PictureDomainServiceImpl extends ServiceImpl<PictureMapper, Picture> implements PictureDomainService {

    @Resource
    private CosManager cosManager;
    @Resource
    private UserDomainService userDomainService;
    @Resource
    private PictureMapper pictureMapper;
    @Resource
    private SpaceApplicationService spaceApplicationService;
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
            Space space = spaceApplicationService.getById(spaceId);
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
            if (!picture.getUserId().equals(loginUser.getId()) && !loginUser.isAdmin()) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        } else {
            Space space = spaceApplicationService.getById(spaceId);
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
    public Boolean editPicture(PictureEditRequest pictureEditRequest, User loginUser) {
        // 在此处将实体类和 DTO 进行转换
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureEditRequest, picture);
        // 注意将 list 转为 string
        picture.setTags(JSONUtil.toJsonStr(pictureEditRequest.getTags()));
        // 设置编辑时间
        picture.setEditTime(LocalDateTime.now());
        // 数据校验
        Picture.validPicture(picture);
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

    /**
     * TODO 私有空间 删除图片
     *
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @Override
    public Boolean deleteFrontendPicture(Picture picture, Long id) {
        // 开启事务
        transactionTemplate.execute(status -> {
            // 操作数据库
            boolean result = this.removeById(id);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
            // 释放额度
            Long spaceId = picture.getSpaceId();
            if (spaceId != null) {
                boolean update = spaceMapper.decrementSpaceMySpace(spaceId, picture.getPicSize(), 1);
                ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR, "额度更新失败");
            }
            this.deleteDeepPicture(id);
            return true;
        });

        // 操作数据库
        boolean result = this.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return result;
    }

}
