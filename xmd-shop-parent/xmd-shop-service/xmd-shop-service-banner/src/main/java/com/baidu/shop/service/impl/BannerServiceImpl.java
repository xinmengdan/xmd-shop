package com.baidu.shop.service.impl;

import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.entity.BannerEntity;
import com.baidu.shop.entity.SkuEntity;
import com.baidu.shop.feign.SpuFeign;
import com.baidu.shop.mapper.BannerMapper;
import com.baidu.shop.service.BannerService;
import com.google.gson.JsonObject;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @ClassName BannerServiceImpl
 * @Description: TODO
 * @Author xinmengdan
 * @Date 2020/10/27
 * @Version V1.0
 **/
@RestController
public class BannerServiceImpl extends BaseApiService implements BannerService {

    @Resource
    private BannerMapper bannerMapper;

    @Resource
    private SpuFeign spuFeign;

    @Override
    public Result<List<BannerEntity>> getBannerInfo() {

        BannerEntity bannerEntity = new BannerEntity();

        return this.setResultSuccess(bannerMapper.select(bannerEntity));
    }


    public Result<JsonObject> addBanner(String spuId) {

        if(spuId != null){

            BannerEntity bannerEntity = new BannerEntity();
            Result<SkuEntity> spuResult = spuFeign.getSpuById(Integer.parseInt(spuId.replace("=","")));

            if(spuResult.getCode() == 200){
                SkuEntity sku = spuResult.getData();

                bannerEntity.setImage(sku.getImages());
                bannerEntity.setCreateTime(new Date());
                bannerEntity.setTitle(sku.getTitle());
                bannerEntity.setName(sku.getSpuId() + ": 测试");
                bannerEntity.setSpuId(sku.getSpuId());

                bannerMapper.insertSelective(bannerEntity);

                return this.setResultSuccess();
            }
        }

        return this.setResultError("输入数据有误");
    }

    @Override
    public Result<JsonObject> editBanner(BannerEntity bannerEntity) {

        return this.setResultSuccess( bannerMapper.updateByPrimaryKeySelective(bannerEntity));
    }

    @Override
    public Result<JsonObject> deleteBanner(Integer id) {

        return this.setResultSuccess(bannerMapper.deleteByPrimaryKey(id));
    }


    @Override
    public Result<BannerEntity> getbannerById(Integer id) {
        return this.setResultSuccess(bannerMapper.selectByPrimaryKey(id));
    }

}
