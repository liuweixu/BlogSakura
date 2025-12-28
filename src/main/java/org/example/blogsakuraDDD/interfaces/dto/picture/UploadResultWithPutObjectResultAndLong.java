package org.example.blogsakuraDDD.interfaces.dto.picture;

import com.qcloud.cos.model.PutObjectResult;
import lombok.Data;

@Data
public class UploadResultWithPutObjectResultAndLong {
    private PutObjectResult putObjectResult;
    private Long size;
}
