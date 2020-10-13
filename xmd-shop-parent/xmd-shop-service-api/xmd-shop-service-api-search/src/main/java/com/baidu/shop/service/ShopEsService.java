package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.response.GoodsResponse;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Api(tags = "es接口")
public interface ShopEsService {


    @ApiOperation(value = "初始化es数据")
    @GetMapping(value = "es/initEsData")
    Result<JsonObject> initEsData();

    @ApiOperation(value = "清空es中商品数据")
    @GetMapping(value = "es/clearGoodsEsData")
    Result<JsonObject> clearEsData();

    @ApiOperation(value = "搜索")
    @GetMapping(value = "es/search")
    GoodsResponse search(@RequestParam String search, @RequestParam Integer page,@RequestParam String filter);

    @ApiOperation(value = "新增数据到es")
    @PostMapping(value = "es/saveData")
    Result<JSONObject> saveData(Integer spuId);

    @ApiOperation(value = "通过id删除es数据")
    @DeleteMapping(value = "es/deleteData")
    Result<JSONObject> deleteData(Integer spuId);

}
