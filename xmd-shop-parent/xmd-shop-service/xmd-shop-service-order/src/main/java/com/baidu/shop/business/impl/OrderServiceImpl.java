package com.baidu.shop.business.impl;

import com.baidu.shop.base.BaiduBeanUtil;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.business.OrderService;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.constant.MrshopConstant;
import com.baidu.shop.dto.Car;
import com.baidu.shop.dto.OrderDTO;
import com.baidu.shop.dto.OrderInfo;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.entity.OrderDetailEntity;
import com.baidu.shop.entity.OrderEntity;
import com.baidu.shop.entity.OrderStatusEntity;
import com.baidu.shop.mapper.OrderDetailMapper;
import com.baidu.shop.mapper.OrderMapper;
import com.baidu.shop.mapper.OrderStatusMapper;
import com.baidu.shop.redis.repository.RedisRepository;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.IdWorker;
import com.baidu.shop.utils.JwtUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.xml.crypto.Data;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName OrderServiceImpl
 * @Description: TODO
 * @Author xinmengdan
 * @Date 2020/10/21
 * @Version V1.0
 **/
@RestController
public class OrderServiceImpl extends BaseApiService implements OrderService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderDetailMapper orderDetailMapper;

    @Resource
    private OrderStatusMapper orderStatusMapper;

    @Resource
    private JwtConfig jwtConfig;

    @Resource
    private IdWorker idWorker;

    @Resource
    private RedisRepository redisRepository;

    @Transactional
    @Override
    public Result<String> createOrder(OrderDTO orderDTO,String token) {

        long orderId = idWorker.nextId();//通过雪花算法生成订单

        try {
            //order  订单
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());

            OrderEntity orderEntity = new OrderEntity();
            Date date = new Date();

            orderEntity.setOrderId(orderId); //订单id
            orderEntity.setUserId(userInfo.getId().toString()); //用户id
            orderEntity.setCreateTime(date); //订单生成时间
            orderEntity.setPaymentType(1); //支付类型
            orderEntity.setSourceType(1); //订单来源
            orderEntity.setBuyerMessage("奈斯"); //买家留言
            orderEntity.setBuyerNick(userInfo.getUsername()); //买家昵称
            orderEntity.setBuyerRate(1); //买家是否已评价
            orderEntity.setInvoiceType(1); //发票类型


            List<Long> longs = Arrays.asList(0L);

            //detail  订单详情
            List<OrderDetailEntity> orderDetailList = Arrays.asList(orderDTO.getSkuIds().split(",")).stream().map(skuIdStr -> {

                Car car = redisRepository.getHash(MrshopConstant.USER_GOODS_CAR + userInfo.getId(), skuIdStr, Car.class);
                if(car == null){
                    throw new RuntimeException("数据异常");
                }

                OrderDetailEntity orderDetailEntity = new OrderDetailEntity();

                orderDetailEntity.setSkuId(Long.valueOf(skuIdStr)); //商品id
                orderDetailEntity.setTitle(car.getTitle()); //商品标题
                orderDetailEntity.setPrice(car.getPrice()); //商品价钱
                orderDetailEntity.setNum(car.getNum()); //购买数量
                orderDetailEntity.setImage(car.getImage()); //商品图片
                orderDetailEntity.setOrderId(orderId); //订单id

                longs.set(0,car.getPrice() * car.getNum() + longs.get(0));

                return orderDetailEntity;

            }).collect(Collectors.toList());

            orderEntity.setActualPay(longs.get(0)); //实付金额
            orderEntity.setTotalPay(longs.get(0)); //总金额

            //status  订单状态
            OrderStatusEntity orderStatusEntity = new OrderStatusEntity();
            orderStatusEntity.setCreateTime(date); //订单创建时间
            orderStatusEntity.setOrderId(orderId); //订单id
            orderStatusEntity.setStatus(1); //已创建订单 但没有支付

            //入库
            orderMapper.insertSelective(orderEntity);
            orderDetailMapper.insertList(orderDetailList);
            orderStatusMapper.insertSelective(orderStatusEntity);

            //通过用户id和skuid 删除购物车中的数据
            Arrays.asList(orderDTO.getSkuIds().split(",")).stream().forEach(skuidStr -> {
                redisRepository.delHash(MrshopConstant.USER_GOODS_CAR + userInfo.getId(),skuidStr);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        return this.setResult(HTTPStatus.OK,"",orderId + "");
    }

    @Override
    public Result<OrderInfo> getOrderInfoByOrderId(Long orderId) {

        OrderEntity orderEntity = orderMapper.selectByPrimaryKey(orderId);
        OrderInfo orderInfo = BaiduBeanUtil.copyProperties(orderEntity, OrderInfo.class);

        Example example = new Example(OrderDetailEntity.class);
        example.createCriteria().andEqualTo("orderId",orderInfo.getOrderId());

        List<OrderDetailEntity> orderDetailList = orderDetailMapper.selectByExample(example);
        orderInfo.setOrderDetailList(orderDetailList);

        OrderStatusEntity orderStatusEntity = orderStatusMapper.selectByPrimaryKey(orderInfo.getOrderId());
        orderInfo.setOrderStatusEntity(orderStatusEntity);

        return this.setResultSuccess(orderInfo);

    }


}
