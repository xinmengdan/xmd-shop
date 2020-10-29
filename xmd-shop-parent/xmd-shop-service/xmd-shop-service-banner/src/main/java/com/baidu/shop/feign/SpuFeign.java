package com.baidu.shop.feign;

import com.baidu.shop.base.Result;
import com.baidu.shop.entity.SkuEntity;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "xxx-service",contextId = "GoodsService")
public interface SpuFeign  {

    @ApiModelProperty(value = "通过skuId查询sku信息")
    @GetMapping(value = "goods/getSpuById")
    Result<SkuEntity> getSpuById(@RequestParam Integer spuId);

}
