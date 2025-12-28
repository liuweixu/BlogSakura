package org.example.blogsakura.interfaces.vo.picture;

import cn.hutool.json.JSONUtil;
import lombok.Data;
import org.example.blogsakura.domain.picture.entity.Picture;
import org.example.blogsakura.interfaces.vo.user.UserVO;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO: 里面的PictureVO类中的tags是String数组的，方便前端传递数据。
 * TODO: 另外，里面还额外关联上传的用户信息，可以暂时用于单独的空间管理，这算是未来的一个计划。
 */
@Data
public class PictureVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 图片 url
     */
    private String url;

    /**
     * 图片名称
     */
    private String name;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 标签
     */
    private List<String> tags;

    /**
     * 分类
     */
    private String category;

    /**
     * 文件体积
     */
    private Long picSize;

    /**
     * 图片宽度
     */
    private Integer picWidth;

    /**
     * 图片高度
     */
    private Integer picHeight;

    /**
     * 图片比例
     */
    private Double picScale;

    /**
     * 图片格式
     */
    private String picFormat;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 编辑时间
     */
    private LocalDateTime editTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 私有空间id，id为null表示公共图库
     */
    private Long spaceId;

    /**
     * 创建用户信息
     */
    private UserVO user;

    private static final long serialVersionUID = 1L;


    /**
     * Picture类转为PictureVO类，类似脱敏
     */
    public static PictureVO objToVo(Picture picture) {
        if (picture == null) {
            return null;
        }
        PictureVO pictureVO = new PictureVO();
        BeanUtils.copyProperties(picture, pictureVO);
        // tags 类型不同，需要转换
        pictureVO.setTags(JSONUtil.toList(picture.getTags(), String.class));
        return pictureVO;
    }
}
