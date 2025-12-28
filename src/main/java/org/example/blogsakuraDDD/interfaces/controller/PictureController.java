package org.example.blogsakuraDDD.interfaces.controller;

import cn.hutool.json.JSONUtil;
import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.blogsakuraDDD.domain.picture.entity.Picture;
import org.example.blogsakuraDDD.infrastruct.annotation.AuthCheck;
import org.example.blogsakuraDDD.infrastruct.common.BaseResponse;
import org.example.blogsakuraDDD.infrastruct.common.DeleteRequest;
import org.example.blogsakuraDDD.infrastruct.common.ResultUtils;
import org.example.blogsakuraDDD.domain.user.constant.UserConstant;
import org.example.blogsakuraDDD.infrastruct.exception.BusinessException;
import org.example.blogsakuraDDD.infrastruct.exception.ErrorCode;
import org.example.blogsakuraDDD.infrastruct.exception.ThrowUtils;
import org.example.blogsakuraDDD.domain.user.entity.User;
import org.example.blogsakuraDDD.interfaces.vo.picture.PictureVO;
import org.example.blogsakura.service.SpaceService;
import org.example.blogsakuraDDD.application.service.UserApplicationService;
import org.example.blogsakuraDDD.interfaces.dto.picture.PictureQueryRequest;
import org.example.blogsakuraDDD.interfaces.dto.picture.PictureTagCategory;
import org.example.blogsakuraDDD.interfaces.dto.picture.PictureUpdateRequest;
import org.example.blogsakuraDDD.interfaces.dto.picture.PictureUploadRequest;
import org.springframework.web.bind.annotation.*;
import org.example.blogsakuraDDD.application.service.PictureApplicationService;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * 图片管理 控制层。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
@RestController
@RequestMapping("/backend/picture")
@Slf4j
public class PictureController {

    @Resource
    private PictureApplicationService pictureApplicationService;

    @Resource
    private UserApplicationService userApplicationService;

    /**
     * 保存图片。
     *
     * @param pictureVO 前段传递来的图片信息
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("/")
    public BaseResponse<Boolean> addPicture(@RequestBody PictureVO pictureVO) {
        return ResultUtils.success(pictureApplicationService.addPicture(pictureVO));
    }

    /**
     * TODO 根据删除请求删除图片。
     *
     * @param deleteRequest 删除请求
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("/")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deletePicture(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        return ResultUtils.success(pictureApplicationService.deletePicture(deleteRequest, request));
    }

    /**
     * 根据更新请求更新图片。管理员
     *
     * @param pictureUpdateRequest 更新请求
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("/")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updatePicture(@RequestBody PictureUpdateRequest pictureUpdateRequest,
                                               HttpServletRequest request) {
        ThrowUtils.throwIf(pictureUpdateRequest == null, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(pictureApplicationService.updatePicture(pictureUpdateRequest, request));
    }

    /**
     * 查询所有封装图片。
     *
     * @return 所有数据
     */
    @GetMapping("/list")
    public BaseResponse<List<PictureVO>> getPictureVOList() {
        return ResultUtils.success(pictureApplicationService.getPictureVOList());
    }

    /**
     * 根据主键获取封装图片（管理员）。
     *
     * @param id 图片管理主键
     * @return 图片管理详情
     */
    @GetMapping("/{id}")
    public BaseResponse<PictureVO> getPictureVOById(@PathVariable Long id, HttpServletRequest request) {
        return ResultUtils.success(pictureApplicationService.getPictureVOById(id, request));
    }

    /**
     * 分页查询图片，返回未封装的Picture
     *
     * @param pictureQueryRequest 分页查询请求
     * @return 分页对象
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<Picture>> getPictureListByPage(@RequestBody PictureQueryRequest pictureQueryRequest) {
        long currentPage = pictureQueryRequest.getCurrentPage();
        long pageSize = pictureQueryRequest.getPageSize();
        // 查询数据库
        Page<Picture> picturePage = pictureApplicationService.page(Page.of(currentPage, pageSize),
                pictureApplicationService.getQueryWrapper(pictureQueryRequest));
        return ResultUtils.success(picturePage);
    }

    /**
     * 分页查询图片，返回封装的Picture
     * 以防万一，补充一下spaceId，表示只查询公共图像
     * 此处只表示公共图库，即后端和前端瀑布流图库，用这个方法
     *
     * @param pictureQueryRequest 分页查询请求
     * @return 分页对象
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<PictureVO>> getPictureVOListByPage(@RequestBody PictureQueryRequest pictureQueryRequest,
                                                                HttpServletRequest request) {
        long currentPage = pictureQueryRequest.getCurrentPage();
        long pageSize = pictureQueryRequest.getPageSize();
        pictureQueryRequest.setNullSpaceId(true);
        // 查询数据库
        Page<Picture> picturePage = pictureApplicationService.page(Page.of(currentPage, pageSize),
                pictureApplicationService.getQueryWrapper(pictureQueryRequest));
        return ResultUtils.success(pictureApplicationService.getPictureVOPage(picturePage, request));
    }

    /**
     * 上传图像到COS
     *
     * @param multipartFile
     * @param pictureUploadRequest
     * @param request
     * @return
     */
    @PostMapping("/upload")
    public BaseResponse<PictureVO> getUploadPicture(
            @RequestPart("image") MultipartFile multipartFile,
            PictureUploadRequest pictureUploadRequest,
            HttpServletRequest request
    ) {
        log.info("pictureUploadRequest:{}", JSONUtil.toJsonStr(pictureUploadRequest));
        User loginUser = userApplicationService.sessionLoginUser(request);
        PictureVO pictureVO = pictureApplicationService.uploadPicture(multipartFile, pictureUploadRequest, loginUser);
        return ResultUtils.success(pictureVO);
    }

    /**
     * 上传URL到COS
     *
     * @param pictureUploadRequest
     * @param request
     * @return
     */
    @PostMapping("/upload/url")
    public BaseResponse<PictureVO> getUploadPictureByUrl(
            @RequestBody PictureUploadRequest pictureUploadRequest,
            HttpServletRequest request
    ) {
        User loginUser = userApplicationService.sessionLoginUser(request);
        String fileUrl = pictureUploadRequest.getFileUrl();
        PictureVO pictureVO = pictureApplicationService.uploadPicture(fileUrl, pictureUploadRequest, loginUser);
        return ResultUtils.success(pictureVO);
    }

    /**
     * 在回收箱删除图像，也删除相应cos上的图像
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @DeleteMapping("/deep")
    public BaseResponse<Boolean> deleteDeepPicture(@RequestBody DeleteRequest deleteRequest,
                                                   HttpServletRequest request) {
        ThrowUtils.throwIf(deleteRequest == null, ErrorCode.PARAMS_ERROR);
        Long id = deleteRequest.getId();
        ThrowUtils.throwIf(id == null || id <= 0, ErrorCode.PARAMS_ERROR);
        boolean result = pictureApplicationService.deleteDeepPicture(id);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除图像失败");
        }
        return ResultUtils.success(true);
    }

    @GetMapping("/tag_category")
    public BaseResponse<PictureTagCategory> getPictureListTagCategory() {
        PictureTagCategory pictureTagCategory = new PictureTagCategory();
        List<String> tagList = Arrays.asList("pixiv", "星空", "森林", "天空", "幻想风", "樱花", "高画质", "芙莉莲", "约会大作战", "城市", "田野", "雪");
        List<String> categoryList = Arrays.asList("风景", "二次元", "游戏", "科技");
        pictureTagCategory.setTagList(tagList);
        pictureTagCategory.setCategoryList(categoryList);
        return ResultUtils.success(pictureTagCategory);
    }


}
