//package net.sinzak.server.config.webSocket;
//
//
//import antlr.Utils;
//import groovy.util.logging.Slf4j;
//import jdk.jshell.execution.Util;
//import net.sinzak.server.chatroom.domain.ChatMessage;
//import org.springframework.web.socket.CloseStatus;
//import org.springframework.web.socket.TextMessage;
//import org.springframework.web.socket.WebSocketMessage;
//import org.springframework.web.socket.WebSocketSession;
//import org.springframework.web.socket.handler.TextWebSocketHandler;
//
//import java.nio.charset.StandardCharsets;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Slf4j
//public class WebSocketHandler extends TextWebSocketHandler {
//
//    private final Map<String,WebSocketSession> sessions = new ConcurrentHashMap<>();
//
//    @Override
//    public void afterConnectionEstablished(WebSocketSession session) throws Exception { //연결 근데 아마 쓸일 없을듯 나가는 것만 쓸듯
//        var sessionId = session.getId();
//        sessions.put(sessionId,session);
//        ChatMessage chatMessage = ChatMessage.builder().sender(sessionId).receiver("all").build();
//        chatMessage.newConnect();
//        sessions.values().forEach(s->{
//            try{
//                if(!s.getId().equals(sessionId)){
//                    s.sendMessage(new TextMessage(sessionId+" has joined"));
//                }
//            }
//            catch(Exception e){
//                System.out.println(e.getMessage());
//            }
//        });
//    }
//    @Override
//    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        var sessionId = session.getId();
//        sessions.values().forEach(receiver->{
//            try{
//                if(!receiver.getId().equals(sessionId)&&receiver!= null&&receiver.isOpen()){
//                    receiver.sendMessage(new TextMessage(message.getPayload()));
//                }
//            }
//            catch(Exception e){
//                System.out.println(e.getMessage());
//            }
//        });
//    }
//    @Override
//    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {//에러
//    }
//    @Override
//    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {//나갈때
//        var sessionId = session.getId();
//        sessions.remove(sessionId);
//        sessions.values().forEach(receiver ->{
//            try {
//                receiver.sendMessage(new TextMessage(sessionId +" has exited"));
//            }
//            catch (Exception e){
//                System.out.println(e.getMessage());
//            }
//        });
//
//    }
//}
