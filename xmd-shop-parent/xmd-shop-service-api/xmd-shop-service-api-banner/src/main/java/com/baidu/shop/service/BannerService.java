package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.baidu.shop.entity.BannerEntity;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "轮播图接口")
public interface BannerService {

    @GetMapping(value = "banner/getBannerInfo")
    Result<List<BannerEntity>> getBannerInfo();

    @ApiOperation(value = "新增")
    @PostMapping(value = "/banner/addBanner")
    Result<JsonObject> addBanner(@RequestBody String spuId);

    @ApiOperation(value = "修改")
    @PutMapping(value = "banner/addBanner")
    Result<JsonObject> editBanner(@RequestBody BannerEntity bannerEntity);

    @ApiOperation(value = "删除")
    @DeleteMapping(value = "banner/deleteBanner")
    Result<JsonObject> deleteBanner(Integer id);

    @GetMapping(value = "/banner/getbannerById/{id}")
    Result<BannerEntity> getbannerById(@PathVariable(value = "id") Integer id);

}
