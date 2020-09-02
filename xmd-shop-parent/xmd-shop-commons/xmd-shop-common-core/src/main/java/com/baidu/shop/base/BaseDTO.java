package com.baidu.shop.base;

import com.baidu.shop.utils.StringUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName BaseDTO
 * @Description: TODO
 * @Author xinmengdan
 * @Date 2020/8/31
 * @Version V1.0
 **/
@Data
@ApiModel(value = "数据传输DTO")
public class BaseDTO {

    @ApiModelProperty(value = "当前页",example = "1")
    private Integer page;

    @ApiModelProperty(value = "每页显示多少条",example = "5")
    private Integer rows;

    @ApiModelProperty(value = "排序")
    private String sort;

    @ApiModelProperty(value = "是否降序")
    private  Boolean desc;

    @ApiModelProperty(hidden = true)
    public String getOrderByClause(){
        if(StringUtil.isNotEmpty(sort)) return sort + " " + (desc?"desc":"");
        return null;
    }


}
