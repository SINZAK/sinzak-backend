package net.sinzak.server.user.dto.respond;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
public class MyPageShowForm {
    @ApiModelProperty(example = "글 ID")
    private Long id;
    @ApiModelProperty(example = "https://sinzakimage.s3.ap-northeast-2.amazonaws.com/7aea0508-4b3b-4b52-a98e-8f699b5b4bc7.jpg")
    private String thumbnail;

    public MyPageShowForm(Long id, String thumbnail) {
        this.id = id;
        this.thumbnail = thumbnail;
    }
}
