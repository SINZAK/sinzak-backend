package net.sinzak.server.common.redis;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class SearchNode {
    private String value;
    private SearchNode children;
    private int score;

    public SearchNode(String value, SearchNode children, int score) {
        this.value = value;
        this.children = children;
        this.score = score;
    }
}
