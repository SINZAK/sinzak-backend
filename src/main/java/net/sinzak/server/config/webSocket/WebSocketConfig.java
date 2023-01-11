package net.sinzak.server.config.webSocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {



    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/stomp/chat")
                .setAllowedOrigins("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/sub");//구독
        registry.setApplicationDestinationPrefixes("/pub"); //발행
    }
}


//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry){
//        registry
//                .addHandler(mySocketHandler(),"/room")
//                .setAllowedOrigins("*");
//    }
//    @Bean
//    public WebSocketHandler mySocketHandler(){
//        return new WebSocketHandler();
//    }
//
//}
