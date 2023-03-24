package net.sinzak.server.cert;

import net.sinzak.server.user.domain.User;
import net.sinzak.server.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.annotation.Timed;
import org.springframework.util.StopWatch;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;


@SpringBootTest
@EnableAspectJAutoProxy
class CertServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    private static User USER;

    @Test
    @Transactional
    @Rollback(false)
    @Timed(millis = 1000)
    public void testUpdateCertifiedUniv1() {
        // given
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        User user = userRepository.findById(202L).orElseThrow();
        MailDto dto = new MailDto("New University","new_univ_email@example.com");
        user.updateCertifiedUniv(dto.getUnivName(), dto.getUniv_email());

        stopWatch.stop();
        System.out.println("실행 시간: " + stopWatch.getTotalTimeMillis() + "ms");
    }

    @Test
    @Transactional
    @Rollback(false)
    public void testUpdateCertifiedUniv2() {
        User user = new User(203L,"test@example.com","test");
        userRepository.save(user);
        USER = user;
        entityManager.merge(USER);
    }

    @Test
    @Transactional
    @Rollback(false)
    @Timed(millis = 1000)
    public void testUpdateCertifiedUniv3() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        MailDto dto = new MailDto("New University","new_univ_email@example.com");
        User managedUser = entityManager.contains(USER) ? USER : entityManager.merge(USER);
        managedUser.updateCertifiedUniv(dto.getUnivName(), dto.getUniv_email());
        entityManager.flush();

        stopWatch.stop();
        System.out.println("실행 시간: " + stopWatch.getTotalTimeMillis() + "ms");
    }
}