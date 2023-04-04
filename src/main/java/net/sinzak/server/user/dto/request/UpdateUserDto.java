package net.sinzak.server.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDto {
    @Pattern(regexp = "^(?=.*[a-z0-9가-힣])[a-z0-9가-힣]{2,16}$",
            message = "2자 이상 16자 이하, 영어 또는 숫자 또는 한글로 구성\n특이사항 : 한글 초성 및 모음은 허가하지 않는다.")
    private String name;
    private String introduction;
    private String picture;
}
