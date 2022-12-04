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
    public JSONObject updateUser(UpdateUserDto dto, SessionUser user){
        Optional<User> User =userRepository.findByEmail(user.getEmail());
        if(User.isPresent()){
            User.get().update(dto.getName(),dto.getPicture(),dto.getIntroduction());
            return PropertyUtil.response(true);
        }
        return PropertyUtil.response(false);
    }
    public User sessionUserToUser(SessionUser user){
        User newUser = User.builder()
                .email(user.getEmail()).name(user.getName()).picture(user.getPicture()).build();
        return newUser;
    }
    @Transactional
    public JSONObject follow(Long userId, SessionUser user){
        try{
            User User = getUser(user);
            User findUser = getFindUser(userId);
            if(User.equals(findUser)){
                return PropertyUtil.responseMessage("본인한테는 친구 추가 불가능");
            }
            User.getFollowingList().add(userId);
            findUser.getFollowerList().add(User.getId());
            return PropertyUtil.response(true);
        }
        catch(InstanceNotFoundException e){
            return PropertyUtil.response(false);
        }
    }
    @Transactional
    public JSONObject unFollow(Long userId,SessionUser user){
        try{
            User User = getUser(user);
            User findUser = getFindUser(userId);
            if(User.equals(findUser)){
                return PropertyUtil.responseMessage("본인한테는 친구 추가 불가능");
            }
            User.getFollowingList().remove(userId);
            findUser.getFollowerList().remove(User.getId());
            return PropertyUtil.response(true);
        }
        catch(InstanceNotFoundException e){
            return PropertyUtil.response(false);
        }
    }
    public User getUser(SessionUser user) {
        User User = userRepository
                .findByEmail(user.getEmail())
                .orElseThrow(()-> new UserNotFoundException());
        return User;
    }
    public User getFindUser(Long userId){
        User findUser = userRepository
                .findById(userId)
                .orElseThrow(()->new UserNotFoundException());
        return findUser;
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
