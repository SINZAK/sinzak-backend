package net.sinzak.server.user.domain.embed;

import javax.persistence.Embeddable;

@Embeddable
public class Size {
    private int width; //가로
    private int vertical; //세로
    private int height; //높이

    public Size(int width, int vertical, int height) {
        this.width = width;
        this.vertical = vertical;
        this.height = height;
    }

    protected Size() {}
}
