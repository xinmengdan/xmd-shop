package com.baidu.shop.dto;

import com.baidu.shop.base.BaseDTO;
import com.baidu.shop.validate.group.MrOperation;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @ClassName SpecGroupDTO
 * @Description: TODO
 * @Author xinmengdan
 * @Date 2020/9/3
 * @Version V1.0
 **/

@ApiOperation(value = "规格组数据传输DTO")
@Data
public class SpecGroupDTO extends BaseDTO {

    @ApiModelProperty(value = "规格id",example = "1")
    @NotNull(message = "id不能为空",groups = {MrOperation.Update.class})
    private Integer id;

    @ApiModelProperty(value = "商品分类id",example = "1")
    @NotNull(message = "商品分类id不能为空",groups = {MrOperation.Add.class})
    private Integer cid;

    @ApiModelProperty(value = "规格组名称")
    @NotEmpty(message = "规格组名称不能为空",groups = {MrOperation.Add.class})
    private String name;

}