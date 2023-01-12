package net.sinzak.server.common;

public enum PostType {
    WORK("work"),PRODUCT("product");
    private String name;

    public String getName() {
        return name;
    }

    PostType(String name) {
        this.name = name;
    }
}
