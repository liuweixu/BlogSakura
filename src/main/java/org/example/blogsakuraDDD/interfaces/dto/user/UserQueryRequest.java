package org.example.blogsakuraDDD.interfaces.dto.user;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.blogsakuraDDD.infrastruct.common.PageRequest;


import java.io.Serializable;

@EqualsAndHashCode(callSuper = false)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

    private static final long serialVersionUID = 1L;

}

