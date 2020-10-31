package com.baidu.shop.config;

import com.baidu.shop.utils.RsaUtils;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.security.PublicKey;
import java.util.List;
/**
 * @ClassName JwtConfig
 * @Description: TODO
 * @Author xinmengdan
 * @Date 2020/10/29
 * @Version V1.0
 **/
@Configuration
@Data
public class JwtConfig {

    private static final Logger logger = LoggerFactory.getLogger(JwtConfig.class);

    @Value("${mrshop.jwt.pubKeyPath}")
    private String pubKeyPath;// 公钥地址

    @Value("${mrshop.jwt.cookieName}")
    private String cookieName;

    //#{}代表要使用springEL表达式
    //@Value("#{'${mrshop.filter.excludes}'.split(',')}")
    private List<String> excludePath;

    private PublicKey publicKey; // 公钥

    @PostConstruct
    public void init(){
        try {
            // 获取公钥和私钥
            this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        } catch (Exception e) {
            logger.error("初始化公钥失败！", e);
            throw new RuntimeException();
        }
    }

}

