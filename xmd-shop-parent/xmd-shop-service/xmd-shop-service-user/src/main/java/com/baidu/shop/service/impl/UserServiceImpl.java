package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaiduBeanUtil;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.constant.UserConstant;
import com.baidu.shop.constant.ValidConstant;
import com.baidu.shop.dto.UserDTO;
import com.baidu.shop.entity.UserEntity;
import com.baidu.shop.mapper.UserMapper;
import com.baidu.shop.redis.repository.RedisRepository;
import com.baidu.shop.service.UserService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.BCryptUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @ClassName UserServiceImpl
 * @Description: TODO
 * @Author xinmengdan
 * @Date 2020/10/13
 * @Version V1.0
 **/
@RestController
@Slf4j
public class UserServiceImpl extends BaseApiService implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisRepository redisRepository;

    @Override
    public Result<JSONObject> register(UserDTO userDTO) {

        UserEntity userEntity = BaiduBeanUtil.copyProperties(userDTO, UserEntity.class);
        userEntity.setPassword(BCryptUtil.hashpw(userEntity.getPassword(),BCryptUtil.gensalt()));
        userEntity.setCreated(new Date());//创建时间

        userMapper.insertSelective(userEntity);

        return this.setResultSuccess();
    }

    @Override
    public Result<List<UserEntity>> checkUsernameOrPhone(String value, Integer type) {

        Example example = new Example(UserEntity.class);
        Example.Criteria criteria = example.createCriteria();

        if(type == UserConstant.USER_NAME){
            criteria.andEqualTo("username",value);
        }else if(type == UserConstant.USER_PHONE){
            criteria.andEqualTo("phone",value);
        }

        List<UserEntity> userList = userMapper.selectByExample(example);

        return this.setResultSuccess(userList);
    }

    @Override
    public Result<JSONObject> sendValidCode(UserDTO userDTO) {

        //生成六位 随机验证码
        String code = (int)((Math.random() * 9 + 1) * 100000) + "";

        log.debug("发送手机验证码 手机号:{} 验证码:{}",userDTO.getPhone(),code);

        //发送短信验证码
        //LuosimaoDuanxinUtil.sendSpeak(userDTO.getPhone(),code);

        redisRepository.set(ValidConstant.VALID_USER_PHONE_CODE + userDTO.getPhone(),code);//验证有效期
        redisRepository.expire(ValidConstant.VALID_USER_PHONE_CODE + userDTO.getPhone(),120);

        return this.setResultSuccess();
    }

    @Override
    public Result<JSONObject> checkValidCode(String phone, String code) {

        String redisValid = redisRepository.get(ValidConstant.VALID_USER_PHONE_CODE + phone);
        if(!code.equals(redisValid)){
            return this.setResultError(HTTPStatus.VALID_CODE_ERROR,"验证码输入错误");
        }

        return this.setResultSuccess();
    }


}
