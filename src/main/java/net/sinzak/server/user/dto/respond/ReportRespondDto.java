package net.sinzak.server.user.dto.respond;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportRespondDto {
    private Long userId;
    private String name;
    private String imageUrl;

    public ReportRespondDto(Long userId, String name, String imageUrl) {
        this.userId = userId;
        this.name = name;
        this.imageUrl = imageUrl;
    }
}
