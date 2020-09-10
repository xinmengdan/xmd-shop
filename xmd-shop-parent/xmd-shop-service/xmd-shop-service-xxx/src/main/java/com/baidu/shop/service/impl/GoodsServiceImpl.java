package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaiduBeanUtil;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.dto.BrandDTO;
import com.baidu.shop.dto.SkuDTO;
import com.baidu.shop.dto.SpuDTO;
import com.baidu.shop.entity.*;
import com.baidu.shop.mapper.*;
import com.baidu.shop.service.BrandService;
import com.baidu.shop.service.GoodsService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.ObjectUtil;
import com.baidu.shop.utils.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName GoodsServiceImpl
 * @Description: TODO
 * @Author xinmengdan
 * @Date 2020/9/7
 * @Version V1.0
 **/
@RestController
public class GoodsServiceImpl extends BaseApiService implements GoodsService {

    @Resource
    private SpuMapper spuMapper;

    @Resource
    private BrandService brandService;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private SpuDetailMapper spuDetailMapper;

    @Resource
    private SkuMapper skuMapper;

    @Resource
    private StockMapper stockMapper;


    @Override
    public Result<List<SpuDTO>> list(SpuDTO spuDTO) {

        //分页
        if(ObjectUtil.isNotNull(spuDTO.getPage()) && ObjectUtil.isNotNull(spuDTO.getRows())){
            PageHelper.startPage(spuDTO.getPage(),spuDTO.getRows());
        }

        //构造条件查询
        Example example = new Example(SpuEntity.class);

        //查询条件
        Example.Criteria criteria = example.createCriteria();

        //按标题模糊匹配
        if(StringUtil.isNotEmpty(spuDTO.getTitle())){
            criteria.andLike("title","%" + spuDTO.getTitle() + "%");
        }
        //如果值为2的话不进行拼接查询,默认查询所有
        if(ObjectUtil.isNotNull(spuDTO.getSaleable()) && spuDTO.getSaleable() != 2){
            criteria.andEqualTo("saleable",spuDTO.getSaleable());
        }

        //排序
        if(ObjectUtil.isNotNull(spuDTO.getSort())){
            example.setOrderByClause(spuDTO.getOrderByClause());
        }

        List<SpuEntity> list = spuMapper.selectByExample(example);

        //代码优化
        List<SpuDTO> spuDtoList = list.stream().map(spuEntity -> {

            SpuDTO spuDTO1 = BaiduBeanUtil.copyProperties(spuEntity, SpuDTO.class);

            //设置品牌名称
            BrandDTO brandDTO = new BrandDTO();
            brandDTO.setId(spuEntity.getBrandId());

            Result<PageInfo<BrandEntity>> brandInfo = brandService.getBrandInfo(brandDTO);

            if(ObjectUtil.isNotNull(brandDTO)){
                PageInfo<BrandEntity> data = brandInfo.getData();
                List<BrandEntity> list1 = data.getList();

                if(!list1.isEmpty() && list1.size() == 1){
                    spuDTO1.setBrandName(list1.get(0).getName());
                }

            }

            //分类名称
            String caterogyName = categoryMapper.selectByIdList(
                    Arrays.asList(spuDTO1.getCid1(), spuDTO1.getCid2(), spuDTO1.getCid3()))
                    .stream().map(category -> category.getName())
                    .collect(Collectors.joining("/"));

            spuDTO1.setCategoryName(caterogyName);

            return spuDTO1;

        }).collect(Collectors.toList());


         //代码拆分
//        List<SpuDTO> spuDTOS = new ArrayList<>();
//        list.stream().forEach(spuEntity -> {
//            //通过品牌id查询品牌名称
//
//            SpuDTO spuDTO1 = BaiduBeanUtil.copyProperties(spuEntity, SpuDTO.class);
//
//            BrandDTO brandDTO = new BrandDTO();
//            brandDTO.setId(spuEntity.getBrandId());
//
//            Result<PageInfo<BrandEntity>> brandInfo = brandService.getBrandInfo(brandDTO);
//            if (ObjectUtil.isNotNull(brandInfo)) {
//
//                PageInfo<BrandEntity> data = brandInfo.getData();
//                List<BrandEntity> list1 = data.getList();
//
//                if(!list1.isEmpty() && list1.size() == 1){
//                    spuDTO1.setBrandName(list1.get(0).getName());
//                }
//            }
//        });


        //PageInfo<SpuDTO> spuDTOPageInfo = new PageInfo<>(spuDtoList);

        //要返回spuDTO1数据  但pageinfo中没有总条数
        PageInfo<SpuEntity> spuDTOPageInfo = new PageInfo<>(list);
        long total = spuDTOPageInfo.getTotal();

        //借用message属性
        return this.setResult(HTTPStatus.OK,total + "",spuDtoList);

        //map实现分页 总条数
//       Map<String, Object> map = new HashMap<>();
//       map.put("list",spuDtoList);
//       map.put("total",total);

//       return this.setResultSuccess(map);

    }

    //商品管理 新增
    @Transactional //回滚
    @Override
    public Result<JSONObject> save(SpuDTO spuDTO) {
        //System.out.println(spuDTO);

        Date date = new Date();

        //spu
        SpuEntity spuEntity = BaiduBeanUtil.copyProperties(spuDTO, SpuEntity.class);
        spuEntity.setSaleable(1);
        spuEntity.setValid(1);
        spuEntity.setCreateTime(date);
        spuEntity.setLastUpdateTime(date);
        spuMapper.insertSelective(spuEntity);


        Integer spuId = spuEntity.getId();

        //spudetail
        SpuDetailEntity spuDetailEntity = BaiduBeanUtil.copyProperties(spuDTO.getSpuDetail(), SpuDetailEntity.class);
        spuDetailEntity.setSpuId(spuId);
        spuDetailMapper.insertSelective(spuDetailEntity);

        //新增数据 代码抽取
        this.addSkuAndstocks(spuDTO.getSkus(),spuId,date);

        return this.setResultSuccess();
    }


    //通过 spuId 获取SpuDetail信息
    @Override
    public Result<SpuDetailEntity> getSpuDetailBySpu(Integer spuId) {

        SpuDetailEntity spuDetailEntity = spuDetailMapper.selectByPrimaryKey(spuId);

        return this.setResultSuccess(spuDetailEntity);
    }


    @Override
    public Result<List<SkuDTO>> getSkuBySpuId(Integer spuId) {
        List<SkuDTO> list = skuMapper.seleckAndSkuAndStockBySpuId(spuId);
        return this.setResultSuccess(list);
    }


    @Transactional
    @Override
    public Result<JSONObject> edit(SpuDTO spuDTO) {

        Date date = new Date();

        //修改spu
        SpuEntity spuEntity = BaiduBeanUtil.copyProperties(spuDTO, SpuEntity.class);
        spuEntity.setLastUpdateTime(date); //设置最后更新时间
        spuMapper.updateByPrimaryKeySelective(spuEntity);

        //修改spuDetail
        SpuDetailEntity spuDetailEntity = BaiduBeanUtil.copyProperties(spuDTO.getSpuDetail(), SpuDetailEntity.class);
        spuDetailMapper.updateByPrimaryKeySelective(spuDetailEntity);

        //提取代码
        this.deleteSkusAndStocks(spuDTO.getId());

        //新增数据 代码抽取
        this.addSkuAndstocks(spuDTO.getSkus(),spuDTO.getId(),date);


        return this.setResultSuccess();
    }

    @Transactional
    @Override
    public Result<JSONObject> delete(Integer spuId) {



        //删除spu
        spuMapper.deleteByPrimaryKey(spuId);

        //删除detail
        spuDetailMapper.deleteByPrimaryKey(spuId);

        //提取代码
        this.deleteSkusAndStocks(spuId);

        return this.setResultSuccess();
    }


    //代码提取  代码重复
    //新增 修改
    private void addSkuAndstocks(List<SkuDTO> skus,Integer spuId,Date date){

        skus.stream().forEach(skuDTO -> {

            //新增 sku
            SkuEntity skuEntity = BaiduBeanUtil.copyProperties(skuDTO, SkuEntity.class);
            skuEntity.setSpuId(spuId);
            skuEntity.setCreateTime(date);
            skuEntity.setLastUpdateTime(date);
            skuMapper.insertSelective(skuEntity);

            //新增 stock
            StockEntity stockEntity = new StockEntity();
            stockEntity.setSkuId(skuEntity.getId());
            stockEntity.setStock(skuDTO.getStock());
            stockMapper.insertSelective(stockEntity);

        });

    }

    //删除
    private void deleteSkusAndStocks(Integer spuId){

        Example example = new Example(SkuEntity.class);
        example.createCriteria().andEqualTo("spuId",spuId);

        //通过spuId 查询出要 删除sku
        List<Long> skuIdList = skuMapper.selectByExample(example).stream().map(sku -> sku.getId()).collect(Collectors.toList());

        if(skuIdList.size() > 0){ //判断 以防全表删除
            //通过SkuIdList 删除sku
            skuMapper.deleteByIdList(skuIdList);

            //通过skuIdList 删除库存(stock)
            stockMapper.deleteByIdList(skuIdList);
        }

    }

}
