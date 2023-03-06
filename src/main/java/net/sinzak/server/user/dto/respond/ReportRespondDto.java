package net.sinzak.server.user.dto.respond;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ReportRespondDto {
    private Long userId;
    private String nickName;
    private String picture;

    public ReportRespondDto(Long userId, String nickName, String picture) {
        this.userId = userId;
        this.nickName = nickName;
        this.picture = picture;
    }
}
