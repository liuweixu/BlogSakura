package org.example.blogsakura.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.blogsakura.model.dto.picture.*;
import org.example.blogsakura.model.dto.user.User;
import org.example.blogsakura.model.vo.picture.PictureVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图片管理 服务层。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
public interface PictureService extends IService<Picture> {

    /**
     * 图片查询分页请求
     *
     * @param pictureQueryRequest
     * @return
     */
    public QueryWrapper getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    /**
     * 获取图片封装，类似脱敏
     *
     * @param picture
     * @param request
     * @return
     */
    public Picture getPicureVO(Picture picture, HttpServletRequest request);

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
     * 图片校验方法
     *
     * @param picture
     */
    void validPicture(Picture picture);

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
}
