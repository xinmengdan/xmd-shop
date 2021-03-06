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
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "品牌接口")
public interface BrandService {

    @ApiOperation(value = "查询品牌信息")
    @GetMapping(value = "brand/list")
    Result<PageInfo<BrandEntity>> getBrandInfo(@SpringQueryMap BrandDTO brandDTO);

    @ApiModelProperty(value = "新增品牌信息")
    @PostMapping(value = "brand/save")
    Result<JsonObject> save(@Validated({MrOperation.Add.class}) @RequestBody BrandDTO brandDTO);

    @ApiModelProperty(value = "修改品牌信息")
    @PutMapping(value = "brand/save")
    Result<JsonObject> editBrand(@Validated({MrOperation.Update.class}) @RequestBody BrandDTO brandDTO);

    @ApiOperation(value = "通过id删除品牌信息")
    @DeleteMapping(value = "brand/delete")
    Result<JsonObject> deleteBrand(Integer id);


    @ApiModelProperty(value = "通过分类id获取品牌信息")
    @GetMapping(value = "brand/getBrandByCatefory")
    Result<List<BrandEntity>> getBrandByCategory(Integer cid);


    @ApiOperation(value = "通过品牌id集合获取品牌")
    @GetMapping(value = "brand/getBrandByIdList")
    Result<List<BrandEntity>> getBrandByIdList(@RequestParam String brandIds);
}
