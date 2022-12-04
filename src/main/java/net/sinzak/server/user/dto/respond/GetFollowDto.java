package net.sinzak.server.user.dto.respond;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetFollowDto {
    private Long userId;

    private String name;
    private String picture;

    @Builder()
    public GetFollowDto(Long userId, String name, String picture) {
        this.userId = userId;
        this.name = name;
        this.picture = picture;
    }
}
