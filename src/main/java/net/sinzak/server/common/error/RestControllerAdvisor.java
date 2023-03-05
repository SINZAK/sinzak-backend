package net.sinzak.server.common.error;

import net.sinzak.server.common.PropertyUtil;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestControllerAdvisor {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.OK)
    protected JSONObject handleUserNotFoundException(UserNotFoundException e) {
        return PropertyUtil.responseMessage(e.getMessage());
    }

    @ExceptionHandler(PostNotFoundException.class)
    @ResponseStatus(HttpStatus.OK)
    protected JSONObject handlePostNotFoundException() {
        return PropertyUtil.responseMessage("존재하지 않는 글입니다.");
    }

    @ExceptionHandler(InstanceNotFoundException.class)
    @ResponseStatus(HttpStatus.OK)
    protected JSONObject handleInstanceNotFoundException() {
        return PropertyUtil.responseMessage("이미 완료된 요청이거나, 존재하지 않는 객체입니다.");
    }

    @ExceptionHandler(ChatRoomNotFoundException.class)
    @ResponseStatus(HttpStatus.OK)
    protected JSONObject handleChatRoomNotFoundException() {
        return PropertyUtil.responseMessage("존재하지 않는 채팅방입니다.");
    }

    @ExceptionHandler(UserNotLoginException.class)
    @ResponseStatus(HttpStatus.OK)
    protected JSONObject handleUserNotLoginException(){return PropertyUtil.responseMessage("로그인이 필요한 작업입니다.");}

}
