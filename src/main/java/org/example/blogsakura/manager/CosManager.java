package org.example.blogsakura.manager;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.ml.inference.preprocessing.Multi;
import org.example.blogsakura.common.configuration.CosClientConfig;
import org.example.blogsakura.common.exception.BusinessException;
import org.example.blogsakura.common.exception.ErrorCode;
import org.example.blogsakura.common.exception.ThrowUtils;
import org.example.blogsakura.model.dto.picture.UploadPictureResult;
import org.example.blogsakura.model.vo.picture.PictureVO;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class CosManager {

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private COSClient cosClient;

    @Resource
    private PictureMessage pictureMessage;


    /**
     * 上传对象
     *
     * @param key  唯一键
     * @param file 文件
     */
    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key,
                file);
        return cosClient.putObject(putObjectRequest);
    }

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

    /**
     * 在COS中删除图像
     *
     * @param url
     * @return
     */
    public boolean deleteCOSPicture(String url) {
        String[] splits = url.split(cosClientConfig.getHost());
        String key = splits[splits.length - 1];
        try {
            cosClient.deleteObject(cosClientConfig.getBucket(), key);
        } catch (CosClientException e) {
            e.printStackTrace();
        }
        log.info("在cos删除图像成功");
        return true;
    }

    /**
     * 从URL获取图像并上传（为了能够获取图像信息，就需要先保存到临时文件，然后读取图像获取信息）
     *
     * @param fileUrl
     * @param uploadPathPrefix
     * @return
     */
    public UploadPictureResult uploadPictureByUrl(String fileUrl, String uploadPathPrefix) {
        // 校验Url
        validPicture(fileUrl);
        // 构建图片上传地址
        String uuid = UUID.randomUUID().toString();
        LocalDateTime nowTime = LocalDateTime.now();
        String[] strs = fileUrl.split("\\.");
        String suffix = strs[strs.length - 1];
//        String date = nowTime.getYear() + "/" + nowTime.getMonthValue() + "/" + nowTime.getDayOfMonth() + "/";
        String uploadFilename = uploadPathPrefix + "/" + uuid +
                "." + suffix;
        String fileTempPath = String.format("/test/%s", uploadFilename);
        File file = null;
        try {
            file = File.createTempFile(fileTempPath, null);
            HttpUtil.downloadFile(fileUrl, file);
            // 上传临时文件到COS，key是uploadFilename
            this.putObject(uploadFilename, file);
            UploadPictureResult uploadPictureResult = pictureMessage.getPicture(file);
            String url = String.format("%s%s", cosClientConfig.getHost(), uploadFilename);
            uploadPictureResult.setUrl(url);
            uploadPictureResult.setPicName(FileUtil.mainName(uploadFilename));
            return uploadPictureResult;
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            if (file != null) {
                // 删除临时文件
                boolean delete = file.delete();
                if (!delete) {
                    log.info("file delete error, filepath = {}", fileTempPath);
                }
            }
        }
    }

    /**
     * 校验URL
     *
     * @param fileUrl
     */
    private void validPicture(String fileUrl) {
        ThrowUtils.throwIf(StrUtil.isBlank(fileUrl), ErrorCode.PARAMS_ERROR, "文件地址不能为空");

        try {
            // 1. 验证 URL 格式
            new URL(fileUrl); // 验证是否是合法的 URL
        } catch (MalformedURLException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件地址格式不正确");
        }

        // 2. 校验 URL 协议
        ThrowUtils.throwIf(!(fileUrl.startsWith("http://") || fileUrl.startsWith("https://")),
                ErrorCode.PARAMS_ERROR, "仅支持 HTTP 或 HTTPS 协议的文件地址");

        // 3. 发送 HEAD 请求以验证文件是否存在
        HttpResponse response = null;
        try {
            response = HttpUtil.createRequest(Method.HEAD, fileUrl).execute();
            // 未正常返回，无需执行其他判断
            if (response.getStatus() != HttpStatus.HTTP_OK) {
                return;
            }
            // 4. 校验文件类型
            String contentType = response.header("Content-Type");
            if (StrUtil.isNotBlank(contentType)) {
                // 允许的图片类型
                final List<String> ALLOW_CONTENT_TYPES = Arrays.asList("image/jpeg",
                        "image/jpg", "image/png", "image/webp", "image/gif",
                        "image/bmp");
                ThrowUtils.throwIf(!ALLOW_CONTENT_TYPES.contains(contentType.toLowerCase()),
                        ErrorCode.PARAMS_ERROR, "文件类型错误");
            }
            // 5. 校验文件大小
            String contentLengthStr = response.header("Content-Length");
            if (StrUtil.isNotBlank(contentLengthStr)) {
                try {
                    long contentLength = Long.parseLong(contentLengthStr);
                    final long THIRTY_MB = 30 * 1024 * 1024L; // 限制文件大小为 30MB
                    ThrowUtils.throwIf(contentLength > THIRTY_MB, ErrorCode.PARAMS_ERROR, "文件大小不能超过 30M");
                } catch (NumberFormatException e) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小格式错误");
                }
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }
}
