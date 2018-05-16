package ca.bc.gov.nrs.infra.cmdb.infrastructure;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer
{
    public static final String BROKER_ROOT_PATH = "/topic";

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry)
    {
        registry.setApplicationDestinationPrefixes("/app")
                .enableSimpleBroker(BROKER_ROOT_PATH);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry)
    {
        // gs-guide-websocket
        stompEndpointRegistry.addEndpoint("/socket")
                .setAllowedOrigins("*")
                .withSockJS();
    }
}
