package org.example.blogsakuraDDD.interfaces.assembler;

import cn.hutool.json.JSONUtil;
import org.example.blogsakuraDDD.domain.picture.entity.Picture;
import org.example.blogsakuraDDD.interfaces.dto.picture.PictureEditRequest;
import org.example.blogsakuraDDD.interfaces.dto.picture.PictureUpdateRequest;
import org.example.blogsakuraDDD.interfaces.vo.picture.PictureVO;
import org.springframework.beans.BeanUtils;

public class PictureAssembler {

    public static Picture toPictureEntity(PictureEditRequest request) {
        if (request == null) {
            return null;
        }
        Picture picture = new Picture();
        BeanUtils.copyProperties(request, picture);
        // 注意将 list 转为 string
        picture.setTags(JSONUtil.toJsonStr(request.getTags()));
        return picture;
    }


    public static Picture toPictureEntity(PictureUpdateRequest request) {
        if (request == null) {
            return null;
        }
        Picture picture = new Picture();
        BeanUtils.copyProperties(request, picture);
        // 注意将 list 转为 string
        picture.setTags(JSONUtil.toJsonStr(request.getTags()));
        return picture;
    }

    public static Picture toPictureEntity(PictureVO pictureVO) {
        if (pictureVO == null) {
            return null;
        }
        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureVO, picture);
        // 注意将 list 转为 string
        picture.setTags(JSONUtil.toJsonStr(pictureVO.getTags()));
        return picture;
    }
}
