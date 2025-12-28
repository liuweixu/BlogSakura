package org.example.blogsakuraDDD.application.service.impl;

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
import org.example.blogsakuraDDD.domain.picture.entity.Picture;
import org.example.blogsakuraDDD.domain.picture.service.PictureDomainService;
import org.example.blogsakuraDDD.domain.picture.service.impl.PictureDomainServiceImpl;
import org.example.blogsakuraDDD.infrastruct.common.DeleteRequest;
import org.example.blogsakuraDDD.infrastruct.exception.BusinessException;
import org.example.blogsakuraDDD.infrastruct.exception.ErrorCode;
import org.example.blogsakuraDDD.infrastruct.exception.ThrowUtils;
import org.example.blogsakuraDDD.infrastruct.manager.cos.CosManager;
import org.example.blogsakuraDDD.infrastruct.manager.upload.FilePictureUpload;
import org.example.blogsakuraDDD.infrastruct.manager.upload.PictureUploadTemplate;
import org.example.blogsakuraDDD.infrastruct.manager.upload.UrlPictureUpload;
import org.example.blogsakuraDDD.infrastruct.mapper.SpaceMapper;
import org.example.blogsakuraDDD.infrastruct.mapper.PictureMapper;
import org.example.blogsakuraDDD.domain.space.entity.Space;
import org.example.blogsakuraDDD.domain.user.entity.User;
import org.example.blogsakuraDDD.interfaces.assembler.PictureAssembler;
import org.example.blogsakuraDDD.interfaces.vo.picture.PictureVO;
import org.example.blogsakuraDDD.application.service.PictureApplicationService;
import org.example.blogsakura.service.SpaceService;
import org.example.blogsakuraDDD.application.service.UserApplicationService;
import org.example.blogsakuraDDD.interfaces.dto.picture.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

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
public class PictureApplicationServiceImpl extends ServiceImpl<PictureMapper, Picture> implements PictureApplicationService {

    @Resource
    private UserApplicationService userApplicationService;
    @Resource
    private PictureDomainService pictureDomainService;

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
     * 彻底删除图片，需要在回收箱部分删除
     *
     * @param id
     * @return
     */
    @Override
    public boolean deleteDeepPicture(Long id) {
        return pictureDomainService.deleteDeepPicture(id);
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
     * 图像处理的权限逻辑 针对私有空间的特殊操作，比如私有空间的删除和编辑图像（系统管理员不能修改，只能用户自己修改）
     *
     * @param loginUser
     * @param picture
     */
    @Override
    public void checkPictureAuth(User loginUser, Picture picture) {
        pictureDomainService.checkPictureAuth(loginUser, picture);
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

}
