package org.example.blogsakura.interfaces.controller;

import com.mybatisflex.core.paginate.Page;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.blogsakura.infrastruct.common.BaseResponse;
import org.example.blogsakura.infrastruct.common.DeleteRequest;
import org.example.blogsakura.infrastruct.common.ResultUtils;
import org.example.blogsakura.interfaces.dto.picture.PictureQueryRequest;
import org.example.blogsakura.interfaces.dto.picture.PictureUpdateRequest;
import org.example.blogsakura.interfaces.vo.picture.PictureVO;
import org.example.blogsakura.application.service.PictureApplicationService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/picture")
@Slf4j
public class PictureFrontendController {

    @Resource
    private PictureApplicationService pictureApplicationService;

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
        return ResultUtils.success(pictureApplicationService.getFrontendPictureVOListByPage(pictureQueryRequest, request));
    }

    /**
     * 加入cache的前端分页
     *
     * @param pictureQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo/cache")
    public BaseResponse<Page<PictureVO>> getFrontendPictureVOListByPageWithCache(
            @RequestBody PictureQueryRequest pictureQueryRequest,
            HttpServletRequest request
    ) {
        return ResultUtils.success(pictureApplicationService.getFrontendPictureVOListByPageWithCache(
                pictureQueryRequest, request
        ));
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
        return ResultUtils.success(pictureApplicationService.editFrontendPicture(pictureEditRequest, request));
    }

    /**
     * TODO 私有空间 删除图片
     *
     * @param deleteRequest 删除请求
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("/")
    public BaseResponse<Boolean> deleteFrontendPicture(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        return ResultUtils.success(
                pictureApplicationService.deleteFrontendPicture(deleteRequest, request)
        );
    }

    /**
     * TODO 针对私有空间的查询图片
     *
     * @param id 图片管理主键
     * @return 图片管理详情
     */
    @GetMapping("/{id}")
    public BaseResponse<PictureVO> getFrontendPictureVOById(@PathVariable Long id, HttpServletRequest request) {

        return ResultUtils.success(pictureApplicationService.getFrontendPictureVOById(id, request));
    }
}
