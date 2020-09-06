package com.baidu.shop.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.SpecGroupDTO;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.entity.SpecGroupEntity;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.validate.group.MrOperation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName SpecificationService
 * @Description: TODO
 * @Author xinmengdan
 * @Date 2020/9/3
 * @Version V1.0
 **/
@Api(value = "规格接口")
public interface SpecificationService {

    // 规格组
    @ApiModelProperty(value = "查询规格组信息")
    @GetMapping(value = "specgroup/list")
    Result<List<SpecGroupEntity>> list(SpecGroupDTO specGroupDTO);

    @ApiModelProperty(value = "新增规格组")
    @PostMapping(value = "specgroup/save")
    Result<List<JSONObject>> save(@Validated({MrOperation.Add.class}) @RequestBody SpecGroupDTO specGroupDTO);

    @ApiModelProperty(value = "修改规格组")
    @PutMapping(value = "specgroup/save")
    Result<List<JSONObject>> edit(@Validated({MrOperation.Update.class})@RequestBody SpecGroupDTO specGroupDTO);

    @ApiModelProperty(value = "删除规格组")
    @DeleteMapping(value = "specgroup/delete")
    Result<List<JSONObject>> delete(Integer id);


    // 规格参数
    @ApiModelProperty(value = "查询规格参数")
    @GetMapping(value = "specparam/list")
    Result<List<SpecParamEntity>> list(SpecParamDTO specParamDTO);

    @ApiModelProperty(value = "新增规格参数")
    @PostMapping(value = "specparam/save")
    Result<List<JSONObject>> saveparam(@Validated ({MrOperation.Add.class}) @RequestBody SpecParamDTO specParamDTO);


    @ApiModelProperty(value = "新增规格参数")
    @PutMapping(value = "specparam/save")
    Result<List<JSONObject>> editparam(@Validated ({MrOperation.Update.class}) @RequestBody SpecParamDTO specParamDTO);

    @ApiModelProperty(value = "删除规格参数")
    @DeleteMapping(value = "specparam/delete")
    Result<List<JSONObject>> deleteparam(Integer id);


}
