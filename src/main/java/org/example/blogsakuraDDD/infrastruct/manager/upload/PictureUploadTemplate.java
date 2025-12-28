package org.example.blogsakuraDDD.infrastruct.manager.upload;

import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.blogsakuraDDD.infrastruct.configuration.CosClientConfig;
import org.example.blogsakuraDDD.infrastruct.exception.BusinessException;
import org.example.blogsakuraDDD.infrastruct.exception.ErrorCode;
import org.example.blogsakuraDDD.interfaces.dto.picture.UploadPictureResult;
import org.example.blogsakuraDDD.interfaces.dto.picture.UploadResultWithPutObjectResultAndLong;

import java.io.IOException;

@Slf4j
public abstract class PictureUploadTemplate {

    @Resource
    protected CosClientConfig cosClientConfig;

    public final UploadPictureResult uploadPicture(Object inputSource, String uploadPathPrefix) {
        // 校验文档
        validPicture(inputSource);
        // 获取key
        String uploadFileName = getUploadFileName(inputSource, uploadPathPrefix);
        // 获取文件名
        String fileName = getOriginalFileName(inputSource);
        try {
            // 上传图像到cos
            UploadResultWithPutObjectResultAndLong uploadResultWithPutObjectResultAndLong
                    = getUploadResultWithPutObjectResultAndLong(inputSource, uploadFileName);
            PutObjectResult putObjectResult = uploadResultWithPutObjectResultAndLong.getPutObjectResult();
            Long size = uploadResultWithPutObjectResultAndLong.getSize();
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            // 封装结果
            UploadPictureResult uploadPictureResult = new UploadPictureResult();
            int height = imageInfo.getHeight();
            int width = imageInfo.getWidth();
            double picScale = (double) width / height;
            uploadPictureResult.setPicHeight(height);
            uploadPictureResult.setPicWidth(width);
            uploadPictureResult.setPicScale(picScale);
            uploadPictureResult.setPicFormat(imageInfo.getFormat());
            String url = String.format("%s%s", cosClientConfig.getHost(), uploadFileName);
            uploadPictureResult.setUrl(url);
            uploadPictureResult.setPicSize(size);
            uploadPictureResult.setPicName(fileName);
            return uploadPictureResult;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        }
    }

    /**
     * 校验文档
     *
     * @param inputSource
     */
    protected abstract void validPicture(Object inputSource);

    /**
     * 获取key
     */
    protected abstract String getUploadFileName(Object inputSource, String uploadPathPrefix);

    /**
     * 获取文件名
     *
     * @param inputSource
     * @return
     */
    protected abstract String getOriginalFileName(Object inputSource);

    /**
     * 上传图像到COS
     *
     * @param inputSource
     * @param uploadFileName
     * @return
     */
    protected abstract UploadResultWithPutObjectResultAndLong getUploadResultWithPutObjectResultAndLong(
            Object inputSource, String uploadFileName
    ) throws IOException;
}
