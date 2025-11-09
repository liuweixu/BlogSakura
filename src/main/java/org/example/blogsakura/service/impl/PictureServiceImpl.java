package org.example.blogsakura.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.example.blogsakura.common.exception.BusinessException;
import org.example.blogsakura.common.exception.ErrorCode;
import org.example.blogsakura.common.exception.ThrowUtils;
import org.example.blogsakura.manager.CosManager;
import org.example.blogsakura.model.dto.picture.Picture;
import org.example.blogsakura.mapper.PictureMapper;
import org.example.blogsakura.model.dto.picture.PictureQueryRequest;
import org.example.blogsakura.model.dto.picture.PictureUploadRequest;
import org.example.blogsakura.model.dto.picture.UploadPictureResult;
import org.example.blogsakura.model.dto.user.User;
import org.example.blogsakura.model.vo.picture.PictureVO;
import org.example.blogsakura.model.vo.user.UserVO;
import org.example.blogsakura.service.PictureService;
import org.example.blogsakura.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalDateTime;
import java.util.Date;
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
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture> implements PictureService {


    private final CosManager cosManager;
    private final UserService userService;
    private final PictureMapper pictureMapper;

    public PictureServiceImpl(CosManager cosManager, UserService userService, PictureMapper pictureMapper) {
        this.cosManager = cosManager;
        this.userService = userService;
        this.pictureMapper = pictureMapper;
    }

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
        queryWrapper.like("name", name);
        queryWrapper.like("introduction", introduction);
        queryWrapper.like("picFormat", picFormat);
        queryWrapper.eq("category", category);
        queryWrapper.eq("picWidth", picWidth);
        queryWrapper.eq("picHeight", picHeight);
        queryWrapper.eq("picSize", picSize);
        queryWrapper.eq("picScale", picScale);
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
     * 上传图片到云图，用户目前默认为管理者admin
     *
     * @param multipartFile
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    @Override
    public PictureVO uploadPicture(MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest, User loginUser) {
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NO_AUTH_ERROR);
        // 用于判断新增还是更新图片
        Long pictureId = null;
        if (pictureUploadRequest.getId() != null) {
            // 更新图片
            pictureId = pictureUploadRequest.getId();
        }
        // 如果是更新图片，则需要校验图片是否存在
        if (pictureId != null) {
            boolean exist = this.query().eq(Picture::getId, pictureId).hasCondition();
            ThrowUtils.throwIf(!exist, ErrorCode.PARAMS_ERROR);
        }
        // 上传图片得到信息
        String uploadPathPrefix = "/personal_pictures";
        UploadPictureResult uploadPictureResult = cosManager.uploadPicture(multipartFile, uploadPathPrefix);
        // 新增图片信息或者更新图片信息
        Picture picture = new Picture();
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
            cosManager.deletePicture(this.getById(pictureId).getUrl());
        }
        boolean result = this.saveOrUpdate(picture);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "图片上传失败");
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
                picturePage.getPageNumber(), picturePage.getPageSize(), picturePage.getTotalRow()
        );
        // pictureList为空时，直接返回pictureVOPage(也是为空)
        if (CollUtil.isEmpty(pictureList)) {
            return pictureVOPage;
        }
        // 对象列表->封装对象列表
        List<PictureVO> pictureVOList = pictureList.stream().map(PictureVO::objToVo).toList();
        // 1. 关联查询的用户信息
        Set<Long> userIdSet = pictureList.stream().map(Picture::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap
                = userService.listByIds(userIdSet).stream().collect(Collectors.groupingBy(User::getId));
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
        boolean resultDeletePicture = cosManager.deletePicture(url);
        ThrowUtils.throwIf(!resultDeletePicture, ErrorCode.OPERATION_ERROR);
        return pictureMapper.deleteDeepPicture(id);
    }
}
