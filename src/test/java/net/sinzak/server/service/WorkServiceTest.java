package net.sinzak.server.service;

import net.sinzak.server.config.auth.dto.SessionUser;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.work.dto.WorkPostDto;
import net.sinzak.server.user.repository.UserRepository;
import net.sinzak.server.work.service.WorkService;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class WorkServiceTest {


    @Autowired
    private WorkService service;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void makePostTest() {
        User user = new User("insi2000@naver.com","송인서","그림2");
        User saved = userRepository.save(user);
        WorkPostDto dto = new WorkPostDto("테스트","내용테스트");
        SessionUser findUser = new SessionUser(user);
        JSONObject obj = service.makeWorkPost(findUser, dto);
        Assertions.assertTrue((Boolean)obj.get("success"));
        Assertions.assertTrue(saved.getWorkPostList().size()==1);
    }

}

