package com.baidu.shop.web;

import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.business.UserOauthService;
import com.baidu.shop.config.JwtConfig;
import com.baidu.shop.dto.UserInfo;
import com.baidu.shop.entity.UserEntity;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.CookieUtils;
import com.baidu.shop.utils.JwtUtils;
import com.baidu.shop.utils.ObjectUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName UserOauthController
 * @Description: TODO
 * @Author xinmengdan
 * @Date 2020/10/15
 * @Version V1.0
 **/
@RestController
@Api(tags = "用户认证接口")
public class UserOauthController extends BaseApiService {

    @Resource
    private UserOauthService userOauthService;

    @Resource
    private JwtConfig jwtConfig;

    @ApiOperation(value = "用户登录")
    @PostMapping(value = "oauth/login")
    public Result<JSONObject> login(@RequestBody UserEntity userEntity, HttpServletRequest servletRequest, HttpServletResponse servletResponse){

        String token = userOauthService.login(userEntity,jwtConfig);

        //判断token是否为null
        if(ObjectUtil.isNull(token)){
            return this.setResultError(HTTPStatus.VALID_USER_PASSWORD_ERROR,"用户名或密码错误");
        }
        //将token放到cookie 延长过期时间
        CookieUtils.setCookie(servletRequest,servletResponse,jwtConfig.getCookieName(),token,jwtConfig.getCookieMaxAge(),true);

        return this.setResultSuccess();
    }

    //检查用户登录
    @GetMapping(value = "oauth/verify")     //CookieValue:从cookie中取值  value="cookie的名称"
    public Result<UserInfo> checkUserIsLogin(@CookieValue(value = "MRSHOP_TOKEN") String token,HttpServletRequest servletRequest,HttpServletResponse servletResponse){

        try {
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtConfig.getPublicKey());//token当前token已执行完

            //此token是重新生成的 登录状态又刷新30分钟后失效
            token = JwtUtils.generateToken(userInfo,jwtConfig.getPrivateKey(),jwtConfig.getExpire());

            //将新的token放到cookie中 过期时间延长
            CookieUtils.setCookie(servletRequest,servletResponse,jwtConfig.getCookieName(),token,jwtConfig.getCookieMaxAge(),true);

            return this.setResultSuccess(userInfo);
        } catch (Exception e) {
            return this.setResultError(HTTPStatus.VERIFY_ERROR,"用户失效");
        }

    }


}
