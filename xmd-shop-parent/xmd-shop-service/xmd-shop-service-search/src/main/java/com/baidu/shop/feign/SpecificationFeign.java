package com.baidu.shop.feign;

import com.baidu.shop.service.SpecificationService;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "xxx-service",contextId = "SpecificationService")
public interface SpecificationFeign extends SpecificationService {
}
