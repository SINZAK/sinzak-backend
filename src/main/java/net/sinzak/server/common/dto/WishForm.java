package net.sinzak.server.common.dto;

import lombok.Getter;

@Getter
public class WishForm {
    private boolean mode;  //true면 추가 false면 삭제
    private Long id;
}