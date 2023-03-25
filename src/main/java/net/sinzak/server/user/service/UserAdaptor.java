package net.sinzak.server.user.service;

import lombok.RequiredArgsConstructor;
import net.sinzak.server.common.error.UserNotFoundException;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAdaptor {
    private final UserRepository userRepository;

    public User queryUser(Long userId) {
        return userRepository.findByIdNotDeleted(userId).orElseThrow(UserNotFoundException::new);
    }
}
