package net.sinzak.server.common.error;

public class ChatRoomNotFoundException extends RuntimeException{

    public ChatRoomNotFoundException() {
        super();
    }
    public ChatRoomNotFoundException(String s) {
        super(s);
    }
    @Override
    public synchronized Throwable fillInStackTrace() {return this;}
}
