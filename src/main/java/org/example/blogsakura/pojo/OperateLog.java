package org.example.blogsakura.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperateLog {
    private Long id;
    private String operateTime;
    private String operateName;
    private Long costTime;
}
