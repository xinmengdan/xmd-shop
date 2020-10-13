package com.baidu.shop.component;

import com.baidu.shop.constant.MqMessageConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @ClassName MrRabbitMQ
 * @Description: TODO
 * @Author xinmengdan
 * @Date 2020/10/12
 * @Version V1.0
 **/
@Component
@Slf4j
public class MrRabbitMQ implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnCallback {

    private RabbitTemplate rabbitTemplate;

    //注入构造方法
    @Autowired
    public MrRabbitMQ(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate;
        //设置回调能收到 发送响应
        rabbitTemplate.setConfirmCallback(this);
        //如果设置备份 队列不起作用
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnCallback(this);
    }

    public void send(String sendMsg,String routingkey){
        CorrelationData correlationId = new CorrelationData(UUID.randomUUID().toString());
        //交换机名称 路由关键字 发送的消息内容 消息ID
        rabbitTemplate.convertAndSend(MqMessageConstant.EXCHANGE,routingkey,sendMsg,correlationId);
    }


    @Override
    public void confirm(CorrelationData correlationData, boolean b, String s) {
        if(b){
            log.info("消息发送成功:correlationData({}),ack({}),cause({})",correlationData,b,s);
        }else{
            log.error("消息发送失败:correlationData({}),ack({}),cause({})",correlationData,b,s);
        }
    }

    @Override
    public void returnedMessage(Message message, int i, String s, String s1, String s2) {
        log.warn("消息丢失:exchange({}),route({}),replyCode({}),replyText({}),message:{}",s1,s2,i,s,message);
    }
}
