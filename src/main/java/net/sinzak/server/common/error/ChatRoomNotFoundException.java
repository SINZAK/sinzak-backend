package net.sinzak.server.common.error;

public class ChatRoomNotFoundException extends RuntimeException{

    public ChatRoomNotFoundException() {
        super();
    }
    @Override
    public synchronized Throwable fillInStackTrace() {return this;}
}
