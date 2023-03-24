package net.sinzak.server.common.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InstanceNotFoundException extends RuntimeException {
    public InstanceNotFoundException() {
        super();
    }
    public InstanceNotFoundException(String s) {
        super(s);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {return this;}
}
