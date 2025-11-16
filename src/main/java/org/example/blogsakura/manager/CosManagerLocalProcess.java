package org.example.blogsakura.manager;

import cn.hutool.core.io.FileUtil;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.blogsakura.common.configuration.CosClientConfig;
import org.example.blogsakura.common.exception.ErrorCode;
import org.example.blogsakura.common.exception.ThrowUtils;
import org.example.blogsakura.model.dto.picture.UploadPictureResult;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class CosManagerLocalProcess {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private COSClient cosClient;

    @Resource
    private PictureMessage pictureMessage;

    /**
     * 通用文件上传到COS并返回访问URL（非云图部分）
     *
     * @param file
     * @param key  cos的对象键，即图片的存储路径
     * @return
     * @throws IOException
     */
    public String uploadFileWithoutLocal(MultipartFile file, String key) throws IOException {
        InputStream inputStream = file.getInputStream();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        PutObjectRequest putObjectRequest = new PutObjectRequest(
                cosClientConfig.getBucket(), key, inputStream, metadata
        );
        PutObjectResult result = cosClient.putObject(putObjectRequest);
        if (result != null) {
            // 构建访问url
            String url = String.format("%s%s", cosClientConfig.getHost(), key);
            log.info("文件上传COS成功：{} -> {}", file.getName(), url);
            return url;
        } else {
            log.error("文件上传COS失败，返回结果为空");
            return null;
        }
    }


    /**
     * 个人图库部分，上传图片到COS，返回图片的具体信息.
     * 第二个参数是图片上传前缀，用于构建图片上传地址
     *
     * @param file             文件
     * @param uploadPathPrefix 上传路径前缀
     * @return
     */
    public UploadPictureResult uploadPicture(MultipartFile file, String uploadPathPrefix) {
        // 校验图片
        validPicture(file);
        // 构建图片上传地址
        String uuid = UUID.randomUUID().toString();
        String originalFileName = file.getOriginalFilename();
        LocalDateTime nowTime = LocalDateTime.now();
//        String date = nowTime.getYear() + "/" + nowTime.getMonthValue() + "/" + nowTime.getDayOfMonth() + "/";
        String uploadFilename = uploadPathPrefix + "/" + uuid +
                "." + pictureMessage.getFormatName(file);
        log.info("文件名: {}", FileUtil.mainName(originalFileName));
        try {
            UploadPictureResult uploadPictureResult = pictureMessage.getPicture(file);
            String url = this.uploadFileWithoutLocal(file, uploadFilename);
            uploadPictureResult.setUrl(url);
            uploadPictureResult.setPicName(FileUtil.mainName(originalFileName));
            return uploadPictureResult;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 校验图片
     *
     * @param file
     */
    public void validPicture(MultipartFile file) {
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
}
