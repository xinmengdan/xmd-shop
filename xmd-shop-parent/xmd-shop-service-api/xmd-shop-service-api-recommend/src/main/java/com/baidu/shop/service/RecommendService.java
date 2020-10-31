package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.RecommendDTO;
import com.google.gson.JsonObject;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface RecommendService {

    @PostMapping(value = "recommend/addRecommend")
    Result<JsonObject> addRecommend(@RequestBody RecommendDTO recommendDTO,@CookieValue(value = "MRSHOP_TOKEN") String token);

    @GetMapping(value = "recommend/getRecommend")
    Result<List<RecommendDTO>> getRecommend(@CookieValue(value = "MRSHOP_TOKEN") String token);

    @GetMapping(value = "recommend/getGoodsInfo")
    Result<List<RecommendDTO>> getGoodsInfo();

}
