package com.baidu.shop.dto;

import com.baidu.shop.base.BaseDTO;
import com.baidu.shop.validate.group.MrOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.nio.charset.Charset;

/**
 * @ClassName BrandDTO
 * @Description: TODO
 * @Author xinmengdan
 * @Date 2020/8/31
 * @Version V1.0
 **/
@Data
@ApiModel(value = "品牌传输数据DTO")
public class BrandDTO extends BaseDTO {

    @ApiModelProperty(value = "品牌id",example = "1")
    @NotNull(message = "id不能为空",groups = {MrOperation.Update.class})
    private Integer id;

    @ApiModelProperty(value = "品牌名称")
    @NotEmpty(message = "品牌名称不能为空",groups = {MrOperation.Add.class})
    private String name;

    @ApiModelProperty(value = "品牌logo")
    private String image;

    @ApiModelProperty(value = "品牌首字母")
    private Character letter;

    @ApiModelProperty(value = "品牌分类信息")
    @NotEmpty(message = "品牌分类信息不能为空",groups = {MrOperation.Add.class})
    private String category;
}
