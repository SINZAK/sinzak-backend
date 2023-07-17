package net.sinzak.server.user.dto.respond;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ProfileShowForm {
    @ApiModelProperty(example = "글 ID")
    private Long id;
    @ApiModelProperty(example = "https://sinzakimage.s3.ap-northeast-2.amazonaws.com/7aea0508-4b3b-4b52-a98e-8f699b5b4bc7.jpg")
    private String thumbnail;
    private String title;
    private LocalDateTime date;
    private boolean complete;
}
