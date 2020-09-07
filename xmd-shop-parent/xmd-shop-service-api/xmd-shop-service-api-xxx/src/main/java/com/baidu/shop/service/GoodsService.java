package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.SpuEntity;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@ApiModel(value = "商品接口")
public interface GoodsService {

    @ApiModelProperty(value = "获取spu信息")
    @GetMapping(value = "goods/list")
    Result<Map<String, Object>> list(SpuDTO spuDTO);

}
