package net.sinzak.server.common.error;

import javax.management.relation.RoleUnresolved;

public class UserNotLoginException extends RuntimeException {
    public UserNotLoginException(String message) {
        super(message);
    }
    public UserNotLoginException() {
        super();
    }

}
