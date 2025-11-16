package org.example.blogsakura.manager.upload;

import jakarta.annotation.Resource;
import org.example.blogsakura.common.exception.ErrorCode;
import org.example.blogsakura.common.exception.ThrowUtils;
import org.example.blogsakura.manager.cos.CosManager;
import org.example.blogsakura.manager.cos.PictureMessage;
import org.example.blogsakura.model.dto.picture.UploadResultWithPutObjectResultAndLong;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 本地图像上传子类
 */
@Service
public class FilePictureUpload extends PictureUploadTemplate {

    @Resource
    private PictureMessage pictureMessage;

    @Resource
    private CosManager cosManager;

    /**
     * 校验文档
     *
     * @param inputSource
     */
    @Override
    protected void validPicture(Object inputSource) {
        MultipartFile file = (MultipartFile) inputSource;
        ThrowUtils.throwIf(file == null, ErrorCode.PARAMS_ERROR);
        // 1. 校验大小
        long fileSize = file.getSize();
        final long ONE_M = 1024 * 1024L;
        ThrowUtils.throwIf(fileSize > 20 * ONE_M, ErrorCode.PARAMS_ERROR, "文件大小不能超过20M");
        // 2. 校验文件后缀
        String fileSuffix = pictureMessage.getFormatName(file);
        // 允许上传的文件后缀
        final List<String> ALLOW_FORMAT_LIST = Arrays.asList("jpeg", "jpg", "png", "wepg", "gif", "bmp", "tif", "tiff", "svg");
        ThrowUtils.throwIf(!ALLOW_FORMAT_LIST.contains(fileSuffix), ErrorCode.PARAMS_ERROR, "文件类型错误");
    }

    /**
     * 获取key
     *
     * @param uploadPathPrefix
     */
    @Override
    protected String getUploadFileName(Object inputSource, String uploadPathPrefix) {
        MultipartFile file = (MultipartFile) inputSource;
        // 构建图片上传地址
        String uuid = UUID.randomUUID().toString();
        return uploadPathPrefix + "/" + uuid +
                "." + pictureMessage.getFormatName(file);
    }

    /**
     * 获取文件名
     *
     * @param inputSource
     * @return
     */
    @Override
    protected String getOriginalFileName(Object inputSource) {
        MultipartFile file = (MultipartFile) inputSource;
        return file.getOriginalFilename();
    }

    /**
     * 上传图像到COS
     *
     * @param inputSource
     * @param uploadFileName
     * @return
     */
    @Override
    protected UploadResultWithPutObjectResultAndLong getUploadResultWithPutObjectResultAndLong(Object inputSource, String uploadFileName) throws IOException {
        MultipartFile file = (MultipartFile) inputSource;
        return cosManager.uploadFileWithoutLocal(file, uploadFileName);
    }
}
