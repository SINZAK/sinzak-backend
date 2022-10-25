package net.sinzak.server.service;


import net.sinzak.server.config.dto.UpdateUserDto;
import net.sinzak.server.domain.User;
import net.sinzak.server.error.InstanceNotFoundException;
import net.sinzak.server.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import javax.transaction.Transactional;
import javax.validation.constraints.AssertTrue;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserCommandServiceTest {


    @Autowired
    private UserCommandService userCommandService;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void init(){
        User user = new User("송인서@지메일","송인서","그림2");
        userRepository.save(user);
        User user1 = new User("최지현@지메일","최지현","그림2");
        userRepository.save(user1);
    }
    @Test
    @Transactional
    public void createUserTest(){
        User user = new User("유성욱@지메일","유성욱","그림1");
        long userId = userCommandService.createUser(user);
        Optional<User> findUser = userRepository.findById(userId);
        assertThat(findUser.get()).isEqualTo(user);
    }
    @Test
    @Transactional
    public void createSameUserTest(){
        User user = new User("송인서@지메일","유성욱","그림1");
        Assertions.assertThrows(InstanceNotFoundException.class,()->{
            userCommandService.createUser(user);
        });
    }
    @Test
    @Transactional
    public void updateUserTest(){
        UpdateUserDto dto =  new UpdateUserDto("인서","저는 소프 개발자입니다","그림3");
        User findUser = userRepository.findByEmail("송인서@지메일").get();
        Assertions.assertTrue(userCommandService.updateUser(dto,findUser));
    }



}