package net.sinzak.server.common.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InstanceNotFoundException extends  RuntimeException {
    public static final String REPORT_NOT_FOUND ="존재하지 않는 유저를 조회하고 있습니다.";
    public InstanceNotFoundException() {
        super();
    }
    public InstanceNotFoundException(String s) {
        super(s);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {return this;}
}
