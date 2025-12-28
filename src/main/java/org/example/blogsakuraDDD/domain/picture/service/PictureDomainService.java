package org.example.blogsakuraDDD.domain.picture.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import org.example.blogsakuraDDD.domain.picture.entity.Picture;
import org.example.blogsakuraDDD.domain.user.entity.User;
import org.example.blogsakuraDDD.interfaces.dto.picture.PictureEditRequest;
import org.example.blogsakuraDDD.interfaces.dto.picture.PictureQueryRequest;
import org.example.blogsakuraDDD.interfaces.dto.picture.PictureUpdateRequest;
import org.example.blogsakuraDDD.interfaces.dto.picture.PictureUploadRequest;
import org.example.blogsakuraDDD.interfaces.vo.picture.PictureVO;

/**
 * 图片管理 服务层。
 *
 * @author <a href="https://github.com/liuweixu">liuweixu</a>
 */
public interface PictureDomainService extends IService<Picture> {

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
     * 彻底删除图片，需要在回收箱部分删除
     *
     * @param id
     * @return
     */
    boolean deleteDeepPicture(Long id);


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
