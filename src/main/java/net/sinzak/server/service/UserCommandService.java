package net.sinzak.server.service;


import lombok.Builder;
import lombok.RequiredArgsConstructor;
import net.sinzak.server.config.auth.dto.SessionUser;
import net.sinzak.server.config.dto.request.UpdateUserDto;
import net.sinzak.server.domain.User;
import net.sinzak.server.error.InstanceNotFoundException;
import net.sinzak.server.repository.UserRepository;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserCommandService {
    private final UserRepository userRepository;

    @Transactional //실제론 연동로그인이기에 api테스트용
    public long createUser(SessionUser user){
        Optional<User> findUser =
                userRepository.findByEmail(user.getEmail());
        if(findUser.isPresent()){
            throw new InstanceNotFoundException("이미 존재하는 이메일입니다");
        }
        User newUser = sessionUserToUser(user);
        userRepository.save(newUser);
        return userRepository.findByEmail(user.getEmail()).get().getId();
    }

    @Transactional //실제론 연동로그인이기에 api테스트용
    public JSONObject createUser2(SessionUser user){
        Optional<User> findUser =
                userRepository.findByEmail(user.getEmail());
        if(findUser.isPresent()){
            return PropertyUtil.responseMessage("이미 존재하는 회원입니다.");
        }
        User newUser = sessionUserToUser(user);
        userRepository.save(newUser);
        return PropertyUtil.response(true);
    }




    @Transactional
    public boolean updateUser(UpdateUserDto dto,SessionUser user){
        User findUser =
                userRepository
                        .findByEmail(user.getEmail())
                        .orElseThrow(()-> new InstanceNotFoundException("유저가 존재하지 않습니다."));
        findUser.update(dto.getName(),dto.getPicture(),dto.getIntroduction());
        return true;
    }
    public User sessionUserToUser(SessionUser user){
        User newUser = User.builder()
                .email(user.getEmail()).name(user.getName()).picture(user.getPicture()).build();
        return newUser;
    }
//    @Transactional
//    public void clickFollowButton(FollowDto dto, User user){
//        //프론트엔드 형식이 어케 되는지 모르겠네
//        if(user.getId().equals(dto.getId())){
//            throw new IllegalStateException("자신한테는 친구 신청 불가능");
//        }
//        User findUser =
//                userRepository
//                        .findByEmail(user.getEmail())
//                        .orElseThrow(()-> new InstanceNotFoundException("유저가 존재하지 않습니다."));
//
//    }
}
