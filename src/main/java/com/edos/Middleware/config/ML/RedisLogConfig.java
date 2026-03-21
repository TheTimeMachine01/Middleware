package com.edos.Middleware.config.ML;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class RedisLogConfig {

    @Value("${app.channels.ml-logs}")
    private String logChannel;

    @Bean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                            MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, new PatternTopic(logChannel));
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(com.edos.Middleware.service.ML.LiveLogBridgeService handler) {
        return new MessageListenerAdapter(handler, "receiveMessage");
    }
}
