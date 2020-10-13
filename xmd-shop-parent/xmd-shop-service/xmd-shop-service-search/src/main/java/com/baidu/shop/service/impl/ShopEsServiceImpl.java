package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.document.GoodsDoc;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpecParamDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryEntity;
import com.baidu.shop.entity.SpecParamEntity;
import com.baidu.shop.entity.SpuDetailEntity;
import com.baidu.shop.feign.BrandFeign;
import com.baidu.shop.feign.CategoryFeign;
import com.baidu.shop.feign.GoodsFeign;
import com.baidu.shop.feign.SpecificationFeign;
import com.baidu.shop.response.GoodsResponse;
import com.baidu.shop.service.ShopEsService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.ESHighLightUtil;
import com.baidu.shop.utils.JSONUtil;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName ShopEsServiceImpl
 * @Description: TODO
 * @Author xinmengdan
 * @Date 2020/9/16
 * @Version V1.0
 **/
@RestController
@Slf4j
public class ShopEsServiceImpl  extends BaseApiService implements ShopEsService {

    @Autowired
    private GoodsFeign goodsFeign;

    @Autowired
    private SpecificationFeign specificationFeign;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Autowired
    private BrandFeign brandFeign;

    @Autowired
    private CategoryFeign categoryFeign;


    private List<GoodsDoc> esGoodsInfo(SpuDTO spuDTO) {

//        spuDTO.setPage(1);
//        spuDTO.setRows(5);
        Result<List<SpuDTO>> spuInfo = goodsFeign.list(spuDTO);

        List<GoodsDoc> goodsDocs = new ArrayList<>();

        if(spuInfo.getCode() == HTTPStatus.OK){

            //spu数据
             spuInfo.getData().stream().forEach(spu -> {

                Integer spuId = spu.getId();

                GoodsDoc goodsDoc = new GoodsDoc();

                goodsDoc.setId(spuId.longValue());
                goodsDoc.setTitle(spu.getTitle());
                goodsDoc.setSubTitle(spu.getSubTitle());
                goodsDoc.setCid1(spu.getCid1().longValue());
                goodsDoc.setCid2(spu.getCid2().longValue());
                goodsDoc.setCid3(spu.getCid3().longValue());
                goodsDoc.setBrandName(spu.getBrandName());
                goodsDoc.setCategoryName(spu.getCategoryName());

                goodsDoc.setBrandId(spu.getBrandId().longValue());
                //通过spuID查询skuList
                Result<List<SkuDTO>> skuResult = goodsFeign.getSkuBySpuId(spu.getId());

                if (skuResult.getCode() == HTTPStatus.OK) {

                    List<SkuDTO> skuList = skuResult.getData();
                    List<Long> priceList = new ArrayList<>();

                    List<HashMap<Object, Object>> skuListMap = skuList.stream().map(sku -> {

                        HashMap<Object, Object> map = new HashMap<>();

                        map.put("id", sku.getId());
                        map.put("title", sku.getTitle());
                        map.put("image", sku.getImages());
                        map.put("price", sku.getPrice());

                        priceList.add(sku.getPrice().longValue());
                        return map;
                    }).collect(Collectors.toList());

                    goodsDoc.setPrice(priceList);
                    goodsDoc.setSkus(JSONUtil.toJsonString(skuListMap));

                }

                //获取规格参数
                SpecParamDTO specParamDTO = new SpecParamDTO();
                specParamDTO.setCid(spu.getCid3());
                Result<List<SpecParamEntity>> specParamResult = specificationFeign.list(specParamDTO);

                Map<String, Object> specMap = new HashMap<>();

                if (specParamResult.getCode() == HTTPStatus.OK) {

                    List<SpecParamEntity> paramList = specParamResult.getData();

                    Result<SpuDetailEntity> spuDetailResult = goodsFeign.getSpuDetailBySpu(spu.getId());


                    if(spuDetailResult.getCode() == HTTPStatus.OK){
                        SpuDetailEntity spuDetaiInfo = spuDetailResult.getData();

                        //通用规格参数的值
                        String genericSpecStr = spuDetaiInfo.getGenericSpec();
                        Map<String, String> genericSpecMap = JSONUtil.toMapValueString(genericSpecStr);

                        //特有规格参数的值
                        String specialSpecStr = spuDetaiInfo.getSpecialSpec();
                        Map<String, List<String>> specialSpecMap = JSONUtil.toMapValueStrList(specialSpecStr);

                        paramList.stream().forEach(param -> {

                            if (param.getGeneric()) {

                                if(param.getNumeric() && param.getSearching()){
                                    specMap.put(param.getName(), this.chooseSegment(genericSpecMap.get(param.getId() + ""),param.getSegments(),param.getUnit()));
                                }else{
                                    specMap.put(param.getName(), genericSpecMap.get(param.getId() + ""));
                                }
                            } else {
                                specMap.put(param.getName(), specialSpecMap.get(param.getId().toString()));
                            }
                        });
                    }
                }
                goodsDoc.setSpecs(specMap);
                goodsDocs.add(goodsDoc);
             });
        }
        return goodsDocs;
    }


    //把具体的值 转换为区间
    private String chooseSegment(String value, String segments, String unit) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : segments.split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + unit + "以上";
                }else if(begin == 0){
                    result = segs[1] + unit + "以下";
                }else{
                    result = segment + unit;
                }
                break;
            }
        }
        return result;
    }


    @Override
    public Result<JsonObject> initEsData() {

        IndexOperations indexOps = elasticsearchRestTemplate.indexOps(GoodsDoc.class);
        if(!indexOps.exists()){
            indexOps.create();
            log.info("索引创建成功");
            indexOps.createMapping();
            log.info("映射创建成功");
        }

        //批量新增数据
        List<GoodsDoc> goodsDocs = this.esGoodsInfo(new SpuDTO());

        elasticsearchRestTemplate.save(goodsDocs);

        return this.setResultSuccess();

    }


    @Override
    public Result<JsonObject> clearEsData() {

        IndexOperations indexOps = elasticsearchRestTemplate.indexOps(GoodsDoc.class);

        if(indexOps.exists()){
            indexOps.delete();
            log.info("索引删除成功");
        }

        return this.setResultSuccess();
    }


    @Override
    public GoodsResponse search(String search,Integer page,String filter) {

        if(StringUtils.isEmpty(search)) throw new RuntimeException("内容不能为空");

        SearchHits<GoodsDoc> searchHits = elasticsearchRestTemplate.search(this.getSearchQueryBuilder(search,page,filter).build(),GoodsDoc.class);
        List<SearchHit<GoodsDoc>> highLightHit = ESHighLightUtil.getHighLightHit(searchHits.getSearchHits());

        //返回的数据
        List<GoodsDoc> goodsDocList = highLightHit.stream().map(searchHit -> searchHit.getContent()).collect(Collectors.toList());

        //分页 总条数 总页数
        long total = searchHits.getTotalHits();
        long totalPage = Double.valueOf(Math.ceil(Long.valueOf(total).doubleValue() / 10)).longValue();

        //获取聚合函数数据
        Aggregations aggregations = searchHits.getAggregations();

        Map<Integer, List<CategoryEntity>> map = this.getCategoryList(aggregations);

        List<CategoryEntity> categoryList = null;
        Integer hotCid = 0;

        //遍历map
        for(Map.Entry<Integer,List<CategoryEntity>> mapEntry : map.entrySet()){
            hotCid = mapEntry.getKey();
            categoryList = mapEntry.getValue();
        }

        Map<String, List<String>> specParamValueMap = this.getpecParam(hotCid, search);

        //获取品牌集合
        List<BrandEntity> brandList = this.getBrandList(aggregations);

        GoodsResponse goodsResponse = new GoodsResponse(total, totalPage, brandList, categoryList, goodsDocList,specParamValueMap);

        return goodsResponse;
    }

    @Override
    public Result<JSONObject> saveData(Integer spuId) {
        SpuDTO spuDTO = new SpuDTO();
        spuDTO.setId(spuId);
        List<GoodsDoc> goodsDoc = this.esGoodsInfo(spuDTO);

        elasticsearchRestTemplate.save(goodsDoc.get(0));

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> deleteData(Integer spuId) {
        GoodsDoc goodsDoc = new GoodsDoc();
        goodsDoc.setId(spuId.longValue());
        elasticsearchRestTemplate.delete(goodsDoc);
        return this.setResultSuccess();
    }


    private Map<String,List<String>> getpecParam(Integer hotCid,String search){

        SpecParamDTO specParamDTO = new SpecParamDTO();
        specParamDTO.setCid(hotCid);
        specParamDTO.setSearching(true);

        Result<List<SpecParamEntity>> specParamResult = specificationFeign.list(specParamDTO);
        if(specParamResult.getCode() == 200){

            List<SpecParamEntity> specParamList = specParamResult.getData();

            //聚合查询
            NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
            searchQueryBuilder.withQuery(QueryBuilders.multiMatchQuery(search,"title","brandName","categoryName"));

            searchQueryBuilder.withPageable(PageRequest.of(0,1));

            specParamList.stream().forEach(specParam -> {

                searchQueryBuilder.addAggregation(AggregationBuilders.terms(specParam.getName()).field("specs." + specParam.getName() + ".keyword"));

            });

            SearchHits<GoodsDoc> searchHits = elasticsearchRestTemplate.search(searchQueryBuilder.build(), GoodsDoc.class);

            Map<String,List<String>> map = new HashMap<>();

            Aggregations aggregations = searchHits.getAggregations();

            specParamList.stream().forEach(specParam -> {

                Terms  terms = aggregations.get(specParam.getName());
                List<? extends Terms.Bucket> buckets = terms.getBuckets();

                List<String> valueList  = buckets.stream().map(bucket -> bucket.getKeyAsString()).collect(Collectors.toList());

                map.put(specParam.getName(),valueList);

            });

            return map;
        }

        return null;

    }

    //构造条件查询
    private NativeSearchQueryBuilder getSearchQueryBuilder(String search,Integer page,String filter){

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        if(!StringUtils.isEmpty(filter) && filter.length() > 2){

            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            Map<String, String> filterMap = JSONUtil.toMapValueString(filter);

            filterMap.forEach((key,value) -> {
                MatchQueryBuilder matchQueryBuilder = null;

                if(key.equals("cid3") || key.equals("brandId")){
                    matchQueryBuilder = QueryBuilders.matchQuery(key, value);
                }else{
                    matchQueryBuilder = QueryBuilders.matchQuery("specs." + key + ".keyword", value);
                }

                boolQueryBuilder.must(matchQueryBuilder);

            });

            queryBuilder.withFilter(boolQueryBuilder);

        }


        //多字段查询
        queryBuilder.withQuery(QueryBuilders.multiMatchQuery(search,"title","brandName","categoryName"));

        //聚合
        queryBuilder.addAggregation(AggregationBuilders.terms("category_agg").field("cid3").size(100000));
        queryBuilder.addAggregation(AggregationBuilders.terms("brand_agg").field("brandId").size(100000));

        //高亮
        queryBuilder.withHighlightBuilder(ESHighLightUtil.getHighlightBuilder("title"));

        //分页
        queryBuilder.withPageable(PageRequest.of(page-1,10));

        return queryBuilder;
    }


    //获取品牌集合
    private List<BrandEntity> getBrandList(Aggregations aggregations){

        Terms brand_agg = aggregations.get("brand_agg");
        List<? extends Terms.Bucket> BrandBuckets = brand_agg.getBuckets();

        List<String> brandIdList = BrandBuckets.stream().map(BrandBucket -> BrandBucket.getKeyAsString()).collect(Collectors.toList());

        //通过品牌id查询数据
        Result<List<BrandEntity>> brandResult = brandFeign.getBrandByIdList(String.join(",", brandIdList));

        return brandResult.getData();
    }


    //获取分类集合
    private Map<Integer, List<CategoryEntity>> getCategoryList(Aggregations aggregations){

        Terms category_agg = aggregations.get("category_agg");

        List<? extends Terms.Bucket> categoryBuckets = category_agg.getBuckets();


        Map<Integer, List<CategoryEntity>> map = new HashMap<>();

        //热度最高的分类id
        List<Integer> hotCidList = Arrays.asList(0);
        List<Long> maxCountList = Arrays.asList(0L);

        List<String> categoryIdList = categoryBuckets.stream().map(cateBucket ->{
            Number keyAsNumber = cateBucket.getKeyAsNumber();

            if(cateBucket.getDocCount() > maxCountList.get(0)){
                maxCountList.set(0,cateBucket.getDocCount());
                hotCidList.set(0,keyAsNumber.intValue());

            }
            return keyAsNumber.toString();

        }).collect(Collectors.toList());

        //通过分类id集合查询数据
        String cidsStr = String.join(",", categoryIdList);
        Result<List<CategoryEntity>> caterogyResult  = categoryFeign.getCategoryByIdList(cidsStr);

        map.put(hotCidList.get(0),caterogyResult.getData());

        return map;

    }



}
