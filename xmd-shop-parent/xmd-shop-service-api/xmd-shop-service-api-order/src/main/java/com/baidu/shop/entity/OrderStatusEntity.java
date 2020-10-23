package com.baidu.shop.entity;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @ClassName OrderStatusEntity
 * @Description: TODO
 * @Author xinmengdan
 * @Date 2020/10/21
 * @Version V1.0
 **/
@Table(name = "tb_order_status")
@Data
public class OrderStatusEntity {//订单状态

    @Id
    private Long orderId;

    private Integer status;

    private Date createTime;

    private Date paymentTime;


}
