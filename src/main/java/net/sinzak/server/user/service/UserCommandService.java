package net.sinzak.server.user.service;


import io.swagger.annotations.ApiModelProperty;
import lombok.RequiredArgsConstructor;
import net.sinzak.server.user.domain.JoinTerms;
import net.sinzak.server.user.dto.request.JoinDto;
import net.sinzak.server.user.dto.request.UnivDto;
import net.sinzak.server.user.dto.request.UpdateUserDto;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.common.error.InstanceNotFoundException;
import net.sinzak.server.common.error.UserNotFoundException;
import net.sinzak.server.user.dto.request.UserIdDto;
import net.sinzak.server.user.repository.JoinTermsRepository;
import net.sinzak.server.user.repository.UserRepository;
import net.sinzak.server.common.PropertyUtil;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;


import java.util.Collections;
import java.util.Optional;
@Service
@RequiredArgsConstructor
@Transactional
public class UserCommandService {
    private final UserRepository userRepository;


    public JSONObject updateUser(UpdateUserDto dto, User loginUser){
        if(loginUser ==null){
            return PropertyUtil.responseMessage(UserNotFoundException.USER_NOT_LOGIN);
        }
        User user = userRepository.findById(loginUser.getId()).get();
        user.update(dto.getName(),dto.getPicture(),dto.getIntroduction());
        return PropertyUtil.response(true);
    }


    public JSONObject follow(UserIdDto userIdDto, User loginUser){
        Long userId = userIdDto.getUserId();
        Optional<User> findUser = userRepository.findById(userId);
        JSONObject userNotExist = checkUsersExist(findUser,loginUser);
        System.out.println((boolean)userNotExist.get(PropertyUtil.SUCCESS_WORD));
        if(!(boolean)userNotExist.get(PropertyUtil.SUCCESS_WORD)){
            return userNotExist;
        }
        User user = userRepository.findById(loginUser.getId()).get();
        user.getFollowingList().add(userId);
        findUser.get().getFollowerList().add(user.getId());
        return PropertyUtil.response(true);
    }
    public JSONObject unFollow(UserIdDto userIdDto,User loginUser){
        Long userId = userIdDto.getUserId();
        Optional<User> findUser = userRepository.findById(userId);
        JSONObject userNotExist = checkUsersExist(findUser,loginUser);
        if(!(boolean)userNotExist.get(PropertyUtil.SUCCESS_WORD)){
            return userNotExist;
        }
        User user = userRepository.findById(loginUser.getId()).get();
        user.getFollowingList().remove(userId);
        findUser.get().getFollowerList().remove(user.getId());
        return PropertyUtil.response(true);
    }

    public JSONObject checkUsersExist(Optional<User> findUser,User user){
        if(user ==null){
            return PropertyUtil.responseMessage(UserNotFoundException.USER_NOT_LOGIN);
        }
        if(!findUser.isPresent()){
            return PropertyUtil.responseMessage(UserNotFoundException.USER_NOT_FOUND);
        }
        if(user.getId().equals(findUser.get().getId())){
            return PropertyUtil.responseMessage("??????????????? ????????? ?????????");
        }
        return PropertyUtil.response(true);
    }

}
