package org.example.blogsakura.model.dto.picture;

import com.qcloud.cos.model.PutObjectResult;
import lombok.Data;

@Data
public class UploadResultWithPutObjectResultAndLong {
    private PutObjectResult putObjectResult;
    private Long size;
}
