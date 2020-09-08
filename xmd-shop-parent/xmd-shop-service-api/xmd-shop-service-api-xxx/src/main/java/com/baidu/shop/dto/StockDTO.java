package com.baidu.shop.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName StockDTO
 * @Description: TODO
 * @Author xinmengdan
 * @Date 2020/9/8
 * @Version V1.0
 **/
@ApiModel(value = "库存数据传输")
@Data
public class StockDTO {

    @ApiModelProperty(value = "库存对应的商品sku id",example = "1")
    private Long skuId;

    @ApiModelProperty(value = "可秒杀库存",example = "1")
    private Integer seckillStock;

    @ApiModelProperty(value = "秒杀总数量",example = "1")
    private Integer seckillTotal;

    @ApiModelProperty(value = "库存总数量",example = "1")
    private Integer stock;


}
