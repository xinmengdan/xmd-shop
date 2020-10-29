package com.baidu.shop.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @ClassName BannerEntity
 * @Description: TODO
 * @Author xinmengdan
 * @Date 2020/10/27
 * @Version V1.0
 **/
@Data
@Table(name = "tb_banner")
public class BannerEntity {

    @Id
    private Integer id;

    private String name;

    private String image;

    private Integer spuId;

    private String title;

    private Date createTime;

}
