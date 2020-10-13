package com.baidu.shop.service;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @ClassName TemplateService
 * @Description: TODO
 * @Author xinmengdan
 * @Date 2020/9/25
 * @Version V1.0
 **/
@Api(tags = "模板接口")
public interface TemplateService {

    @ApiOperation(value = "通过spuId 生成html静态模板")
    @GetMapping(value = "template/createStaticHTMLTemplate")
    Result<JSONObject> createStaticHTMLTemplate(Integer spuId);

    @ApiOperation(value = "初始化静态html模板")
    @GetMapping(value = "template/initStaticHTMLTemplate")
    Result<JSONObject> initStaticHTMLTemplate();

    @ApiOperation(value = "通过spuId删除文件")
    @DeleteMapping(value = "template/deleteHTMlBySpuId")
    Result<JSONObject> deleteHTMlBySpuId(Integer spuId);

}
