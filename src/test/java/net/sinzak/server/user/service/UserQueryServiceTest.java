package net.sinzak.server.user.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserQueryServiceTest {
    @Autowired
    private UserQueryService userQueryService;
    private static int ronaldo = 490000000;
    private static int son = 8860000;
    private static int parkSeoJun = 22970000;
    private static int standard = 999;
    @Test
    void followNumberTrans() {
//        Assertions.assertThat(userQueryService.followNumberTrans(ronaldo)).isEqualTo("4억");
//        Assertions.assertThat(userQueryService.followNumberTrans(son)).isEqualTo("886만");
//        Assertions.assertThat(userQueryService.followNumberTrans(parkSeoJun)).isEqualTo("2,297만");
//        Assertions.assertThat(userQueryService.followNumberTrans(standard)).isEqualTo("999");
    }
}