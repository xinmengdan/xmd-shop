package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.SpuDetailEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@ApiModel(value = "商品接口")
public interface GoodsService {

    @ApiModelProperty(value = "获取spu信息")
    @GetMapping(value = "goods/list")
    Result<List<SpuDTO>> list(SpuDTO spuDTO);

    @ApiModelProperty(value = "保存商品信息")
    @PostMapping(value = "goods/save")
    Result<JSONObject> save(@RequestBody SpuDTO spuDTO);

    @ApiModelProperty(value = "通过spuId 获取spuDetail信息")
    @GetMapping(value = "goods/getSpuDetailBySpu")
    Result<SpuDetailEntity> getSpuDetailBySpu(Integer spuId);

    @ApiModelProperty(value = "获取sku信息")
    @GetMapping(value = "goods/getSkuBySpuId")
    Result<List<SkuDTO>> getSkuBySpuId(Integer spuId);

}
