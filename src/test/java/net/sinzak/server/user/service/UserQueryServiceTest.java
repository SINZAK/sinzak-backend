package net.sinzak.server.user.service;


import net.sinzak.server.user.domain.User;
import net.sinzak.server.user.repository.UserRepository;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Timed;


@SpringBootTest
class UserQueryServiceTest {


    @Autowired private UserQueryService userQueryService;
    @Autowired private UserRepository userRepository;
    User user;

    @Test
    @Timed(millis = 1000)
    public void testMethod() throws Exception {
        user = userRepository.findById(152L).orElseThrow();
        JSONObject myProfile = userQueryService.getMyProfile(user);
        System.out.println(myProfile.toJSONString());

    }
}