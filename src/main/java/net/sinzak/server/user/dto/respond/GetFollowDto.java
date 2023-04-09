package net.sinzak.server.user.dto.respond;


import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class GetFollowDto {
    private Long userId;
    private String name;
    private String picture;

    @QueryProjection
    @Builder
    public GetFollowDto(Long userId, String name, String picture) {
        this.userId = userId;
        this.name = name;
        this.picture = picture;
    }
}
