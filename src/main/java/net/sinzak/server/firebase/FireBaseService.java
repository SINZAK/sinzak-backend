package net.sinzak.server.firebase;


import net.sinzak.server.user.domain.User;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Async
@Transactional
public class FireBaseService {

    public void sendNotification(User user, String title, String body, String route){

    }
}
