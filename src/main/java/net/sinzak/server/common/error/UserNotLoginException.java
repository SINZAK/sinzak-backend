package net.sinzak.server.common.error;

import javax.management.relation.RoleUnresolved;

public class UserNotLoginException extends RuntimeException {
    public UserNotLoginException() {
        super();
    }

    @Override
    public synchronized Throwable fillInStackTrace() {return this;}
}
