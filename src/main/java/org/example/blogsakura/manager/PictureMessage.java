package org.example.blogsakura.manager;

import lombok.extern.slf4j.Slf4j;
import org.example.blogsakura.model.dto.picture.Picture;
import org.springframework.stereotype.Component;

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

    public Picture getPicture(File file) throws IOException {
        Picture picture = new Picture();
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
        picture.setPicFormat(formatName);
        picture.setPicHeight(height);
        picture.setPicWidth(width);
        picture.setPicScale(aspectRatio);
        picture.setPicSize(fileSizeBytes);
        return picture;
    }

    /**
     * 获取图像格式（如 jpg、png、gif 等）
     */
    private static String getFormatName(File file) throws IOException {
        try (ImageInputStream iis = ImageIO.createImageInputStream(file)) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            if (!readers.hasNext()) {
                return "unknown";
            }
            ImageReader reader = readers.next();
            String format = reader.getFormatName();
            reader.dispose();
            return format;
        }
    }
}
