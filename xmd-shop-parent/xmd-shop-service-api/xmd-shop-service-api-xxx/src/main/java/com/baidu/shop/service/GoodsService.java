package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.SpuDetailEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.web.bind.annotation.*;

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

    @ApiModelProperty(value = "修改商品信息")
    @PutMapping(value = "goods/save")
    Result<JSONObject> edit(@RequestBody  SpuDTO spuDTO);

    @ApiModelProperty(value = "删除商品信息")
    @DeleteMapping(value = "goods/delete")
    Result<JSONObject> delete(Integer spuId);


    @ApiModelProperty(value = "商品上下架")
    @PutMapping(value = "goods/upOrDown")
    Result<JSONObject> upOrDown(@RequestBody SpuDTO spuDTO);

}
