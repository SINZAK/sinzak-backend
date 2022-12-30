package net.sinzak.server.user.domain.embed;

import javax.persistence.Embeddable;

@Embeddable
public class Size {
    public int width; //가로
    public int vertical; //세로
    public int height; //높이

    public Size(int width, int vertical, int height) {
        this.width = width;
        this.vertical = vertical;
        this.height = height;
    }

    protected Size() {}
}
