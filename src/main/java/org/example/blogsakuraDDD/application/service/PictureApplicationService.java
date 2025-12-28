package org.example.blogsakuraDDD.application.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import org.example.blogsakuraDDD.domain.picture.entity.Picture;
import org.example.blogsakuraDDD.domain.user.entity.User;
import org.example.blogsakuraDDD.infrastruct.common.DeleteRequest;
import org.example.blogsakuraDDD.interfaces.vo.picture.PictureVO;
import org.example.blogsakuraDDD.interfaces.dto.picture.PictureEditRequest;
import org.example.blogsakuraDDD.interfaces.dto.picture.PictureQueryRequest;
import org.example.blogsakuraDDD.interfaces.dto.picture.PictureUpdateRequest;
import org.example.blogsakuraDDD.interfaces.dto.picture.PictureUploadRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

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
     * 彻底删除图片，需要在回收箱部分删除
     *
     * @param id
     * @return
     */
    boolean deleteDeepPicture(Long id);

    /**
     * 更新图像
     *
     * @param pictureUpdateRequest
     * @return
     */
    boolean updatePicture(PictureUpdateRequest pictureUpdateRequest, HttpServletRequest request);


    /**
     * 图像处理的权限逻辑
     *
     * @param loginUser
     * @param picture
     */
    void checkPictureAuth(User loginUser, Picture picture);

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
    PictureVO getPictureVOById(@PathVariable Long id, HttpServletRequest request);
}
