package net.sinzak.server.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    GUEST("ROLE_GUEST", "회원"), ADMIN("ROLE_ADMIN","관리자"), ADVERTISER("ROLE_ADVERTISER","광고주");
    private final String key;
    private final String title;
}