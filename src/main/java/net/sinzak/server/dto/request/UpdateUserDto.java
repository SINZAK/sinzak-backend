package net.sinzak.server.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDto {
    private String name;
    private String introduction;
    private String picture;
    //    private String 학교
}
