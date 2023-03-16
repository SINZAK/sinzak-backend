package net.sinzak.server.common.error;

public class PostNotFoundException extends RuntimeException {

    public PostNotFoundException() {
        super();
    }
    public PostNotFoundException(String s) {
        super(s);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {return this;}
}
