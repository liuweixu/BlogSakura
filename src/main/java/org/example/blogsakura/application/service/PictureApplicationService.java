package org.example.blogsakura.application.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import org.example.blogsakura.domain.picture.entity.Picture;
import org.example.blogsakura.domain.user.entity.User;
import org.example.blogsakura.infrastruct.common.DeleteRequest;
import org.example.blogsakura.interfaces.dto.picture.*;
import org.example.blogsakura.interfaces.vo.picture.PictureVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 图片管理 服务层。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
public interface PictureApplicationService extends IService<Picture> {

    /**
     * 图片查询分页请求
     *
     * @param pictureQueryRequest
     * @return
     */
    public QueryWrapper getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    /**
     * 上传图片到COS，用户目前默认为管理者admin
     *
     * @param inputSource
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    PictureVO uploadPicture(Object inputSource,
                            PictureUploadRequest pictureUploadRequest,
                            User loginUser);

    /**
     * 获取图片封装（要装入用户信息）
     *
     * @param picture
     * @param request
     * @return
     */
    PictureVO getPictureVO(Picture picture, HttpServletRequest request);

    /**
     * 分页获取图片封装
     *
     * @param picturePage
     * @param request
     * @return
     */
    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);


    /**
     * 更新图像
     *
     * @param pictureUpdateRequest
     * @return
     */
    boolean updatePicture(PictureUpdateRequest pictureUpdateRequest, HttpServletRequest request);


    /**
     * 针对私有空间上的用户修改文件
     *
     * @param pictureEditRequest
     * @param loginUser
     */
    boolean editPicture(PictureEditRequest pictureEditRequest, User loginUser);

    /**
     * 保存图片
     *
     * @param pictureVO
     * @return
     */
    Boolean addPicture(PictureVO pictureVO);

    /**
     * 删除图片
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    Boolean deletePicture(DeleteRequest deleteRequest, HttpServletRequest request);

    /**
     * 查询所有封装图片。
     *
     * @return 所有数据
     */
    List<PictureVO> getPictureVOList();

    /**
     * 根据主键获取封装图片（管理员）。
     *
     * @param id
     * @param request
     * @return
     */
    PictureVO getPictureVOById(Long id, HttpServletRequest request);

    /**
     * 分页查询图片，返回未封装的Picture
     *
     * @param pictureQueryRequest 分页查询请求
     * @return 分页对象
     */
    Page<Picture> getPictureListByPage(PictureQueryRequest pictureQueryRequest);

    /**
     * 分页查询图片，返回封装的Picture
     * 以防万一，补充一下spaceId，表示只查询公共图像
     * 此处只表示公共图库，即后端和前端瀑布流图库，用这个方法
     *
     * @param pictureQueryRequest 分页查询请求
     * @return 分页对象
     */
    Page<PictureVO> getPictureVOListByPage(PictureQueryRequest pictureQueryRequest, HttpServletRequest request);

    /**
     * 上传图像到COS
     *
     * @param multipartFile
     * @param pictureUploadRequest
     * @param request
     * @return
     */
    PictureVO getUploadPicture(MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest,
                               HttpServletRequest request);

    /**
     * 上传URL图像到COS
     *
     * @param pictureUploadRequest
     * @param request
     * @return
     */
    PictureVO getUploadPictureByUrl(PictureUploadRequest pictureUploadRequest, HttpServletRequest request);

    /**
     * 在回收箱删除图像，也删除相应cos上的图像
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    Boolean deleteDeepPicture(DeleteRequest deleteRequest, HttpServletRequest request);

    /**
     * 获取标签和类别信息，此部分尚未完善，后续考虑使用Nacos自动添加等方法
     *
     * @return
     */
    PictureTagCategory getPictureListTagCategory();


    /**
     * 前端分页查询图片，返回封装的Picture
     *
     * @param pictureQueryRequest 分页查询请求
     * @return 分页对象
     */
    Page<PictureVO> getFrontendPictureVOListByPage(PictureQueryRequest pictureQueryRequest,
                                                   HttpServletRequest request);

    /**
     * 加入cache的前端分页
     *
     * @param pictureQueryRequest
     * @param request
     * @return
     */
    Page<PictureVO> getFrontendPictureVOListByPageWithCache(PictureQueryRequest pictureQueryRequest,
                                                            HttpServletRequest request);

    /**
     * 前端编辑图片
     * TODO 私有空间上，用户或管理员根据更新请求更新图片。
     *
     * @param pictureEditRequest 更新请求
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    Boolean editFrontendPicture(PictureUpdateRequest pictureEditRequest,
                                HttpServletRequest request);

    /**
     * TODO 私有空间 删除图片
     *
     * @param deleteRequest 删除请求
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    Boolean deleteFrontendPicture(DeleteRequest deleteRequest, HttpServletRequest request);

    /**
     * TODO 针对私有空间的查询图片
     *
     * @param id 图片管理主键
     * @return 图片管理详情
     */
    PictureVO getFrontendPictureVOById(Long id, HttpServletRequest request);
}
