package net.sinzak.server.service;


import net.sinzak.server.config.auth.dto.SessionUser;
import net.sinzak.server.config.dto.request.UpdateUserDto;
import net.sinzak.server.domain.User;
import net.sinzak.server.error.InstanceNotFoundException;
import net.sinzak.server.repository.UserRepository;
import org.junit.Before;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserCommandServiceTest {


    @Autowired
    private UserCommandService userCommandService;
    @Autowired
    private UserRepository userRepository;


    @Test

    public void createUserTest(){
        User user1 = new User("유성욱@지메일","유성욱","그림2");
        SessionUser user = new SessionUser(user1);
        long userId = userCommandService.createUser(user);
        Optional<User> findUser = userRepository.findById(userId);
        assertThat(findUser.get().getEmail()).isEqualTo(user.getEmail());
    }
    @Test

    public void createSameUserTest(){ //중복 이메일 가입불가

        User user = new User("송인서@지메일","송인서","그림2");
        userRepository.save(user);
        User user1 = new User("송인서@지메일","유성욱","그림1");
        SessionUser user2 = new SessionUser(user1);
        Assertions.assertThrows(InstanceNotFoundException.class,()->{
            userCommandService.createUser(user2);
        });
    }
    @Test
    public void updateUserTest(){
        User user = new User("송인서@지메일","송인서","그림2");
        UpdateUserDto dto =  new UpdateUserDto("인서","저는 소프 개발자입니다","그림3");
        SessionUser findUser = new SessionUser(user);
        Assertions.assertTrue(userCommandService.updateUser(dto,findUser));
    }







}