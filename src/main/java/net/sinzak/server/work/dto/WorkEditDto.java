package net.sinzak.server.work.dto;

import lombok.Getter;
import net.sinzak.server.common.dto.EditDto;

@Getter
public class WorkEditDto extends EditDto {
    public WorkEditDto(String title, String content, int price, boolean suggest) {
        super(title, content, price, suggest);
    }
}
