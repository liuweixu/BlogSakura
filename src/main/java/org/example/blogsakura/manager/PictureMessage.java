package org.example.blogsakura.manager;

import lombok.extern.slf4j.Slf4j;
import org.example.blogsakura.model.dto.picture.Picture;
import org.example.blogsakura.model.dto.picture.UploadPictureResult;
import org.example.blogsakura.model.vo.picture.PictureVO;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

@Component
@Slf4j
public class PictureMessage {

    /**
     * 从MultipartFile类型获取数据
     *
     * @param file
     * @return
     * @throws IOException
     */
    public UploadPictureResult getPicture(MultipartFile file) throws IOException {
        UploadPictureResult uploadPictureResult = new UploadPictureResult();
        // 1. 文件体积（字节）
        long fileSizeBytes = file.getSize();
        double fileSizeKB = fileSizeBytes / 1024.0;
        double fileSizeMB = fileSizeBytes / (1024.0 * 1024.0);

        // 2. 读取图像宽度、高度和宽高比
        BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
        if (bufferedImage == null) {
            log.info("无法读取图像文件，可能不是有效的格式");
            return null;
        }

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        double aspectRatio = (double) width / height;

        // 获取图像格式
        String formatName = getFormatName(file);
        uploadPictureResult.setPicFormat(formatName);
        uploadPictureResult.setPicHeight(height);
        uploadPictureResult.setPicWidth(width);
        uploadPictureResult.setPicScale(aspectRatio);
        uploadPictureResult.setPicSize(fileSizeBytes);
        return uploadPictureResult;
    }

    /**
     * 从File类型获取数据
     *
     * @param file
     * @return
     * @throws IOException
     */
    public UploadPictureResult getPicture(File file) throws IOException {
        UploadPictureResult uploadPictureResult = new UploadPictureResult();
        // 1. 文件体积（字节）
        long fileSizeBytes = file.length();
        double fileSizeKB = fileSizeBytes / 1024.0;
        double fileSizeMB = fileSizeBytes / (1024.0 * 1024.0);
        // 2. 读取图像宽度、高度和宽高比
        BufferedImage bufferedImage = ImageIO.read(file);
        if (bufferedImage == null) {
            log.info("无法读取图像文件，可能不是有效的格式");
            return null;
        }

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        double aspectRatio = (double) width / height;

        // 获取图像格式
        String formatName = getFormatName(file);
        uploadPictureResult.setPicFormat(formatName);
        uploadPictureResult.setPicHeight(height);
        uploadPictureResult.setPicWidth(width);
        uploadPictureResult.setPicScale(aspectRatio);
        uploadPictureResult.setPicSize(fileSizeBytes);
        return uploadPictureResult;
    }

    /**
     * 获取图像格式（如 jpg、png、gif 等）
     */
    public String getFormatName(MultipartFile file) {
        String[] strs = file.getOriginalFilename().split("\\.");
        return strs[strs.length - 1].trim().toLowerCase();
    }

    public String getFormatName(File file) {
        String[] strs = file.getName().split("\\.");
        return strs[strs.length - 1].trim().toLowerCase();
    }
}
