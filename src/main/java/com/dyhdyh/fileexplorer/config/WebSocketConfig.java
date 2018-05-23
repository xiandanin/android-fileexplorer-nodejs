package com.dyhdyh.fileexplorer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * author  dengyuhan
 * created 2018/5/23 14:38
 */
@Configuration
//注解开启使用STOMP协议来传输基于代理(message broker)的消息,这时控制器支持使用@MessageMapping,就像使用@RequestMapping一样
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Bean
    public ServerEndpointExporter serverEndpointExporter(){
        return new ServerEndpointExporter();
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
        // 添加服务端点，可以理解为某一服务的唯一key值
        stompEndpointRegistry.addEndpoint("/progress");
        //当浏览器支持sockjs时执行该配置
        stompEndpointRegistry.addEndpoint("/progress").setAllowedOrigins("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 配置接受订阅消息地址前缀为topic的消息
        config.enableSimpleBroker("/pull");
        // Broker接收消息地址前缀
        config.setApplicationDestinationPrefixes("/pull");
    }
}
