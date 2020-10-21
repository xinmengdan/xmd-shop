package com.baidu.shop.status;

/**
 * @ClassName HTTPStatus
 * @Description: TODO
 * @Author xinmengdan
 * @Date 2020/8/27
 * @Version V1.0
 **/
public class HTTPStatus {

    public static final int OK = 200;//成功

    public static final int ERROR = 500; //失败

    public static final int OPERATION_ERROR = 5001;//操作失败

    public static final int PARAMS_VALIDATE_ERROR = 5002;//参数校验失败

    public static final int VALID_CODE_ERROR = 5003;//验证码错误

    public static final int VALID_USER_PASSWORD_ERROR = 5004;//参数效验失败(用户名或密码错误)

    public static final int VERIFY_ERROR = 403;//参数效验失败(用户失效)

}
