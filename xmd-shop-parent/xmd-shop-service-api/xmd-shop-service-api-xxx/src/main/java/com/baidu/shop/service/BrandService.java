package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.validate.group.MrOperation;
import com.github.pagehelper.PageInfo;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Api(value = "品牌接口")
public interface BrandService {

    @ApiOperation(value = "查询商品品牌信息")
    @GetMapping(value = "brand/list")
    public Result<PageInfo<BrandEntity>> getBrandInfo(BrandDTO brandDTO);

    @ApiModelProperty(value = "查询商品品牌信息")
    @PostMapping(value = "brand/save")
    public Result<JsonObject> save(@Validated({MrOperation.Add.class}) @RequestBody BrandDTO brandDTO);

}
