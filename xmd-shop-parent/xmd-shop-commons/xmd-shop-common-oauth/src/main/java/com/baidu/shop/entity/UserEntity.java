package com.baidu.shop.entity;

import com.sun.istack.internal.NotNull;
import lombok.Data;
import sun.plugin2.message.Message;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @ClassName UserEntity
 * @Description: TODO
 * @Author xinmengdan
 * @Date 2020/10/15
 * @Version V1.0
 **/
@Data
@Table(name = "tb_user")
public class UserEntity {

    @Id
    private Integer id;

    private String username;

    private String password;

    private String phone;

    private Date created;

    private String salt;

}
