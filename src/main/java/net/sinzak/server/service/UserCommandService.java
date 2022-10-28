package net.sinzak.server.service;


import lombok.RequiredArgsConstructor;
import net.sinzak.server.config.auth.dto.SessionUser;
import net.sinzak.server.dto.request.UpdateUserDto;
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
    @Transactional
    public JSONObject updateUser(UpdateUserDto dto, SessionUser user){
        User findUser =
                userRepository
                        .findByEmail(user.getEmail())
                        .orElseThrow(()-> new InstanceNotFoundException("유저가 존재하지 않습니다."+user.getEmail()));
        findUser.update(dto.getName(),dto.getPicture(),dto.getIntroduction());
        return PropertyUtil.response(true);
    }
    public User sessionUserToUser(SessionUser user){
        User newUser = User.builder()
                .email(user.getEmail()).name(user.getName()).picture(user.getPicture()).build();
        return newUser;
    }
    @Transactional
    public JSONObject follow(Long userId, SessionUser user){
        User User = getUserAndCheckIfMe(userId,user);
        User.getFollowingList().add(userId);
        return PropertyUtil.response(true);
    }
    @Transactional
    public JSONObject unFollow(Long userId,SessionUser user){
        User User = getUserAndCheckIfMe(userId,user);
        User.getFollowerList().remove(userId);
        return PropertyUtil.response(true);
    }
    public User getUserAndCheckIfMe(Long userId, SessionUser user) {
        User findUser = userRepository
                .findById(userId)
                .orElseThrow(()->new InstanceNotFoundException("유저가 존재하지 않습니다"));
        User User = userRepository
                .findByEmail(user.getEmail())
                .orElseThrow(()-> new InstanceNotFoundException("유저가 존재하지 않습니다"));
        if(user.getEmail().equals(findUser.getEmail())){
            throw new IllegalStateException("자신한테는 친구 신청 불가능"); //이게 애초에 프론트엔드에서 팔로우 버튼이 안 보이게 해야하긴 함
        }
        return User;
    }
}
