package com.baidu.shop.service.impl;

import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.constant.MrshopConstant;
import com.baidu.shop.dto.Car;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.entity.SkuEntity;
import com.baidu.shop.feign.GoodsFeign;
import com.baidu.shop.redis.repository.RedisRepository;
import com.baidu.shop.service.CarService;
import com.baidu.shop.utils.JSONUtil;
import com.baidu.shop.utils.JwtUtils;
import com.baidu.shop.utils.ObjectUtil;
import com.baidu.shop.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName CarServiceImpl
 * @Description: TODO
 * @Author xinmengdan
 * @Date 2020/10/19
 * @Version V1.0
 **/
@RestController
@Slf4j
public class CarServiceImpl extends BaseApiService implements CarService {

    @Autowired
    private RedisRepository redisRepository;

    @Autowired
    private GoodsFeign goodsFeign;

    @Autowired
    private JwtConfig jwtConfig;

    @Override
    public Result<JSONObject> addCar(Car car,String token) {

        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());//从token中获取用户信息

            //通过userID和skuID获取购物车中的数据
            Car redisCar = redisRepository.getHash(MrshopConstant.USER_GOODS_CAR + userInfo.getId(),car.getSkuId() + "",Car.class);
            Car saveCar = null;
            
            log.debug("通过key:{} skuId:{} 获得的数据为:{}", MrshopConstant.USER_GOODS_CAR + userInfo.getId() + car.getSkuId() + Car.class);

            if(ObjectUtil.isNotNull(redisCar)){//原先用户购物车中 没有当前要添加到购物车中的商品

                redisCar.setNum(car.getNum() + redisCar.getNum());
                saveCar = redisCar;
                //redisRepository.setHash(ValidConstant.USER_GOODS_CAR + userInfo.getId(),car.getSkuId() + "",JSONUtil.toJsonString(car));

                log.debug("当前购物车中有将要新增的商品 重新设置num:{}", redisCar.getNum());

            }else{//当前用户购物车中 没有将要新增商品的信息

                //通过skuID查询sku详细信息
                Result<SkuEntity> skuResult = goodsFeign.getSkuBySkuId(car.getSkuId());

                if (skuResult.getCode() == 200) {
                    SkuEntity skuEntity = skuResult.getData();
                    car.setTitle(skuEntity.getTitle());
                    //判断image的值是否为空  null:返回null !null:分割第一个图片
                    car.setImage(StringUtil.isNotEmpty(skuEntity.getImages()) ? skuEntity.getImages().split(",")[0]:"");

                    Map<String, Object> stringObjectMap = JSONUtil.toMap(skuEntity.getOwnSpec());

                    car.setOwnSpec(skuEntity.getOwnSpec());
                    car.setPrice(Long.valueOf(skuEntity.getPrice()));
                    car.setUserId(userInfo.getId());

                    //redisRepository.setHash(ValidConstant.USER_GOODS_CAR + userInfo.getId(),car.getSkuId() + "",JSONUtil.toJsonString(car));
                    saveCar = car;

                    log.debug("新增商品到购物车redis,key:{} skuId:{} car:{}", MrshopConstant.USER_GOODS_CAR + userInfo.getId() + car.getSkuId() +JSONUtil.toJsonString(car));

                }
            }

            redisRepository.setHash(MrshopConstant.USER_GOODS_CAR + userInfo.getId(),car.getSkuId() + "",JSONUtil.toJsonString(saveCar));
            log.debug("新增到redis数据成功");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> mergeCar(String checkUserLogin, String token) {

        //将json字符串 转 json对象
        com.alibaba.fastjson.JSONObject jsonObject = com.alibaba.fastjson.JSONObject.parseObject(checkUserLogin);
        List<Car> carList = com.alibaba.fastjson.JSONObject.parseArray(jsonObject.get("checkUserLogin").toString(), Car.class);

        //遍历新增到购物车
        carList.stream().forEach(car -> {
            this.addCar(car,token);
        });

        return this.setResultSuccess();
    }

    @Override
    public Result<List<Car>> getUserGoodsCar(String token) {

        List<Car> carList = new ArrayList<>();

        try {
            //获取当前登录用户
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());

            //通过用户id从redis获取购物车数据
            Map<String, String> map = redisRepository.getHash(MrshopConstant.USER_GOODS_CAR + userInfo.getId());

            map.forEach((key,value) -> {
                carList.add(JSONUtil.toBean(value,Car.class));
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        

        return this.setResultSuccess(carList);
    }

    @Override
    public Result<JSONObject> carNumUpdate(Long skuId, Integer type, String token) {

        //获取当前登录用户
        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());

            Car car = redisRepository.getHash(MrshopConstant.USER_GOODS_CAR + userInfo.getId(), skuId + "", Car.class);

            if(car != null){
                if(type == 1){
                    car.setNum(car.getNum() + 1);
                }else{
                    car.setNum(car.getNum() - 1);
                }
                redisRepository.setHash(MrshopConstant.USER_GOODS_CAR + userInfo.getId(), car.getSkuId() + "", JSONUtil.toJsonString(car));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this.setResultSuccess();

    }

}
