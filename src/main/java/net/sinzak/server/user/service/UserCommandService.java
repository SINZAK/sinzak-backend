package net.sinzak.server.user.service;


import lombok.RequiredArgsConstructor;
import net.sinzak.server.config.auth.dto.SessionUser;
import net.sinzak.server.user.dto.request.UpdateUserDto;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.common.error.InstanceNotFoundException;
import net.sinzak.server.common.error.UserNotFoundException;
import net.sinzak.server.user.repository.UserRepository;
import net.sinzak.server.common.PropertyUtil;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class UserCommandService {
    private final UserRepository userRepository;


    @Transactional
    public JSONObject updateUser(UpdateUserDto dto, User loginUser){
        if(loginUser ==null){
            return PropertyUtil.responseMessage(UserNotFoundException.USER_NOT_LOGIN);
        }
        User user = userRepository.findById(loginUser.getId()).get();
        user.update(dto.getName(),dto.getPicture(),dto.getIntroduction());
        return PropertyUtil.response(true);
    }

    @Transactional
    public JSONObject follow(Long userId, User loginUser){
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
    @Transactional
    public JSONObject unFollow(Long userId,User loginUser){
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
    @Transactional
    public JSONObject checkUsersExist(Optional<User> findUser,User user){
        if(user ==null){
            return PropertyUtil.responseMessage(UserNotFoundException.USER_NOT_LOGIN);
        }
        if(!findUser.isPresent()){
            return PropertyUtil.responseMessage(UserNotFoundException.USER_NOT_FOUND);
        }
        if(user.getId().equals(findUser.get().getId())){
            return PropertyUtil.responseMessage("본인한테는 팔로우 불가능");
        }
        return PropertyUtil.response(true);
    }
    @Transactional //실제론 연동로그인이기에 api테스트용
    public long createUser(SessionUser user){ //이건 테스트
        Optional<User> findUser =
                userRepository.findByEmail(user.getEmail());
        if(findUser.isPresent()){
            throw new InstanceNotFoundException("이미 존재하는 이메일입니다");
        }
        User newUser = sessionUserToUser(user);
        userRepository.save(newUser);
        return userRepository.findByEmail(user.getEmail()).get().getId();
    }
    public User sessionUserToUser(SessionUser user){
        User newUser = User.builder()
                .email(user.getEmail()).name(user.getName()).picture(user.getPicture()).build();
        return newUser;
    }

//    @Transactional //실제론 연동로그인이기에 api테스트용
//    public JSONObject createUser2(SessionUser user){
//        Optional<User> findUser =
//                userRepository.findByEmail(user.getEmail());
//        if(findUser.isPresent()){
//            return PropertyUtil.responseMessage("이미 존재하는 회원입니다.");
//        }
//        User newUser = sessionUserToUser(user);
//        userRepository.save(newUser);
//        return PropertyUtil.response(true);
//    }
}
