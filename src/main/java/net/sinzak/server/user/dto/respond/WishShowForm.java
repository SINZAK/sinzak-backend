package net.sinzak.server.user.dto.respond;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class WishShowForm {
    private Long id;
    private int price;
    private String title;
    private String thumbnail;
    private boolean complete;
}
