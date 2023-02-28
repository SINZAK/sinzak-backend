package net.sinzak.server.chatroom.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class
PostDto {
    @ApiModelProperty(value ="게시글 타입",example = "product",dataType = "string")
    private String postType;
    @ApiModelProperty(value ="게시글 아이디", example = "1",dataType = "int")
    private Long postId;
}
