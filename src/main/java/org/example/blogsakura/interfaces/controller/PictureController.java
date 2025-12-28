package org.example.blogsakura.interfaces.controller;

import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.blogsakura.domain.picture.entity.Picture;
import org.example.blogsakura.infrastruct.annotation.AuthCheck;
import org.example.blogsakura.infrastruct.common.BaseResponse;
import org.example.blogsakura.infrastruct.common.DeleteRequest;
import org.example.blogsakura.infrastruct.common.ResultUtils;
import org.example.blogsakura.domain.user.constant.UserConstant;
import org.example.blogsakura.infrastruct.exception.ErrorCode;
import org.example.blogsakura.infrastruct.exception.ThrowUtils;
import org.example.blogsakura.interfaces.vo.picture.PictureVO;
import org.example.blogsakura.interfaces.dto.picture.PictureQueryRequest;
import org.example.blogsakura.interfaces.dto.picture.PictureTagCategory;
import org.example.blogsakura.interfaces.dto.picture.PictureUpdateRequest;
import org.example.blogsakura.interfaces.dto.picture.PictureUploadRequest;
import org.springframework.web.bind.annotation.*;
import org.example.blogsakura.application.service.PictureApplicationService;
import org.springframework.web.multipart.MultipartFile;


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
        return ResultUtils.success(pictureApplicationService.getPictureListByPage(pictureQueryRequest));
    }

    /**
     * 分页查询图片，返回封装的Picture
     * 此处只表示公共图库，即后端和前端瀑布流图库，用这个方法
     *
     * @param pictureQueryRequest 分页查询请求
     * @return 分页对象
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<PictureVO>> getPictureVOListByPage(@RequestBody PictureQueryRequest pictureQueryRequest,
                                                                HttpServletRequest request) {
        return ResultUtils.success(pictureApplicationService.getPictureVOListByPage(pictureQueryRequest, request));
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
        return ResultUtils.success(pictureApplicationService.getUploadPicture(multipartFile, pictureUploadRequest, request));
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
        return ResultUtils.success(pictureApplicationService.getUploadPictureByUrl(pictureUploadRequest, request));
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
        return ResultUtils.success(pictureApplicationService.deleteDeepPicture(deleteRequest, request));
    }

    @GetMapping("/tag_category")
    public BaseResponse<PictureTagCategory> getPictureListTagCategory() {
        return ResultUtils.success(pictureApplicationService.getPictureListTagCategory());
    }
}
