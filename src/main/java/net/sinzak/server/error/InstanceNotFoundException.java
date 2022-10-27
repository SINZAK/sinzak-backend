package net.sinzak.server.error;

public class InstanceNotFoundException extends  RuntimeException {

    public InstanceNotFoundException() {
        super();
    }
    public InstanceNotFoundException(String s) {
        super(s);
    }
    public InstanceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    public InstanceNotFoundException(Throwable cause) {
        super(cause);
    }
}
