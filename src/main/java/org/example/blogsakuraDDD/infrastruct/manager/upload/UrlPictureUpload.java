package org.example.blogsakuraDDD.infrastruct.manager.upload;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.blogsakuraDDD.infrastruct.exception.BusinessException;
import org.example.blogsakuraDDD.infrastruct.exception.ErrorCode;
import org.example.blogsakuraDDD.infrastruct.exception.ThrowUtils;
import org.example.blogsakuraDDD.infrastruct.manager.cos.CosManager;
import org.example.blogsakuraDDD.interfaces.dto.picture.UploadResultWithPutObjectResultAndLong;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class UrlPictureUpload extends PictureUploadTemplate {
    @Resource
    private CosManager cosManager;


    /**
     * 校验文档
     *
     * @param inputSource
     */
    @Override
    protected void validPicture(Object inputSource) {
        String fileUrl = (String) inputSource;
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

    /**
     * 获取key
     *
     * @param inputSource
     * @param uploadPathPrefix
     */
    @Override
    protected String getUploadFileName(Object inputSource, String uploadPathPrefix) {
        String fileUrl = (String) inputSource;
        // 构建图片上传地址
        String uuid = UUID.randomUUID().toString();
        String[] strs = fileUrl.split("\\.");
        String suffix = strs[strs.length - 1];
        return uploadPathPrefix + "/" + uuid +
                "." + suffix;
    }

    /**
     * 获取文件名
     *
     * @param inputSource
     * @return
     */
    @Override
    protected String getOriginalFileName(Object inputSource) {
        String fileUrl = (String) inputSource;
        return FileUtil.mainName(fileUrl);
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
        String fileUrl = (String) inputSource;
        return cosManager.uploadFileWithoutLocalByUrl(fileUrl, uploadFileName);
    }
}
