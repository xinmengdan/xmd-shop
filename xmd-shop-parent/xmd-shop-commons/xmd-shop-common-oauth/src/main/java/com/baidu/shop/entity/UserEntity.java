package com.baidu.shop.entity;

import com.baidu.shop.validate.group.MrOperation;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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
