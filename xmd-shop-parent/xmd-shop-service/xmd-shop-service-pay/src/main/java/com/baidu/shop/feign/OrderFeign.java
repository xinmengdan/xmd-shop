package com.baidu.shop.feign;

import com.baidu.shop.base.Result;
import com.baidu.shop.dto.OrderInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "order-server",contextId = "OrderServer")
public interface OrderFeign {

    @GetMapping(value = "order/getOrderInfoByOrderId")
    Result<OrderInfo> getOrderInfoByOrderId(@RequestParam Long orderId);

}
