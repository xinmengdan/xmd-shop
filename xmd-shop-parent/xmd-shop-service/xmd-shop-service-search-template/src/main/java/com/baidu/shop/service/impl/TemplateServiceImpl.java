package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaiduBeanUtil;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.*;
import com.baidu.shop.entity.*;
import com.baidu.shop.feign.BrandFeign;
import com.baidu.shop.feign.CategoryFeign;
import com.baidu.shop.feign.GoodsFeign;
import com.baidu.shop.feign.SpecificationFeign;
import com.baidu.shop.service.TemplateService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName TemplateServiceImpl
 * @Description: TODO
 * @Author xinmengdan
 * @Date 2020/9/25
 * @Version V1.0
 **/
@RestController
public class TemplateServiceImpl extends BaseApiService implements TemplateService {

    @Autowired
    private GoodsFeign goodsFeign;

    @Autowired
    private BrandFeign brandFeign;

    @Autowired
    private CategoryFeign categoryFeign;

    @Autowired
    private SpecificationFeign specificationFeign;

    @Value(value = "${mrshop.static.html.path}")
    private String staticHTMLPath;

    @Autowired
    private TemplateEngine templateEngine; //静态模板

    @Override
    public Result<JSONObject> createStaticHTMLTemplate(Integer spuId) {

        Map<String, Object> map = this.getPageInfoBySpuId(spuId);
        Context context = new Context();
        context.setVariables(map);

        File file = new File(staticHTMLPath, spuId + ".html");
        PrintWriter writer = null;

        try {
            writer = new PrintWriter(file, "UTF-8");
            templateEngine.process("item",context,writer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }finally {
            writer.close();
        }

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> initStaticHTMLTemplate() {

        Result<List<SpuDTO>> spuInfoResult = goodsFeign.list(new SpuDTO());

        if (spuInfoResult.getCode() == 200) {
            List<SpuDTO> spuDToList = spuInfoResult.getData();

            spuDToList.stream().forEach(spuDTO -> {
                createStaticHTMLTemplate(spuDTO.getId());
            });
        }

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> deleteHTMlBySpuId(Integer spuId) {

        File file = new File(staticHTMLPath + File.separator + spuId + ".html");
        if (!file.delete()) {
            return this.setResultError("文件删除失败");
        }

        return this.setResultSuccess();
    }


    private Map<String, Object> getPageInfoBySpuId(Integer spuId) {

        Map<String, Object> map = new HashMap<>();

        SpuDTO spuDTO = new SpuDTO();
        spuDTO.setId(spuId);

        //获取spu信息
        Result<List<SpuDTO>> spuInfoResult  = goodsFeign.list(spuDTO);

        if (spuInfoResult.getCode() == 200) {
            if (spuInfoResult.getData().size() == 1) {
                //spu信息
                SpuDTO spuInfo = spuInfoResult.getData().get(0);
                map.put("spuInfo",spuInfo);

                //品牌信息
                BrandDTO brandDTO = new BrandDTO();
                brandDTO.setId(spuInfo.getBrandId());
                Result<PageInfo<BrandEntity>> brandInfoResult = brandFeign.getBrandInfo(brandDTO);

                if (brandInfoResult.getCode() == 200) {
                    PageInfo<BrandEntity> data = brandInfoResult.getData();
                    List<BrandEntity> brandList = data.getList();

                    if (brandList.size() == 1) {
                        map.put("brandInfo",brandList.get(0));
                    }
                }

                //分类信息
                Result<List<CategoryEntity>> categoryResult = categoryFeign.getCategoryByIdList(String.join(",", Arrays.asList(spuInfo.getCid1() + "", spuInfo.getCid2() + "", spuInfo.getCid3() + "")));
                if (categoryResult.getCode() == 200) {
                    map.put("categoryList",categoryResult.getData());
                }

                //spudetail信息
                Result<SpuDetailEntity> spuDetailResult = goodsFeign.getSpuDetailBySpu(spuId);
                if (spuDetailResult.getCode() == 200) {
                    SpuDetailEntity spuDetailEntity = spuDetailResult.getData();
                    map.put("spuDetailInfo",spuDetailEntity);
                }

                //特有规格参数
                SpecParamDTO specParamDTO = new SpecParamDTO();
                specParamDTO.setCid(spuInfo.getCid3());
                specParamDTO.setGeneric(false);
                Result<List<SpecParamEntity>> specParamResult = specificationFeign.list(specParamDTO);

                if (specParamResult.getCode() == 200) {

                    List<SpecParamEntity> specParamList = specParamResult.getData();
                    Map<Integer, Object> specParamMap = new HashMap<>();

                    specParamList.stream().forEach(param -> {
                        specParamMap.put(param.getId(),param.getName());
                    });
                    map.put("specParamMap",specParamMap);
                }

                //skus 通过spuId查询sku集合
                Result<List<SkuDTO>> skusResult = goodsFeign.getSkuBySpuId(spuInfo.getId());
                if (skusResult.getCode() == 200) {
                    List<SkuDTO> skuList = skusResult.getData();
                    map.put("skus",skuList);
                }

                //规格组 规格参数
                SpecGroupDTO specGroupDTO = new SpecGroupDTO();
                specGroupDTO.setCid(spuInfo.getCid3());

                Result<List<SpecGroupEntity>> specGroupResult = specificationFeign.list(specGroupDTO);

                if (specGroupResult.getCode() == 200) {

                    List<SpecGroupEntity> specGroupEntityList = specGroupResult.getData();
                    List<SpecGroupDTO> specGroupDTOList = specGroupEntityList.stream().map(specGroup -> {

                        SpecGroupDTO groupDTO = BaiduBeanUtil.copyProperties(specGroup, SpecGroupDTO.class);
                        SpecParamDTO paramDTO = new SpecParamDTO();
                        paramDTO.setGroupId(groupDTO.getId());
                        paramDTO.setGeneric(true);

                        Result<List<SpecParamEntity>> specParamInfoResult = specificationFeign.list(paramDTO);
                        if (specParamInfoResult.getCode() == 200) {
                            groupDTO.setParamList(specParamInfoResult.getData());
                        }
                        return groupDTO;
                    }).collect(Collectors.toList());

                    map.put("specGroupDTOList",specGroupDTOList);
                }
            }
        }
        return map;
    }

}
