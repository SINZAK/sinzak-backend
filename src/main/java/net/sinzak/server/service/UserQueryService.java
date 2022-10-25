package net.sinzak.server.service;

import lombok.RequiredArgsConstructor;
import net.sinzak.server.domain.User;
import net.sinzak.server.error.InstanceNotFoundException;
import net.sinzak.server.repository.UserRepository;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserQueryService {

    private final UserRepository userRepository;

    public JSONObject getUserProfile(Long otherUserId, User user) {
        JSONObject object = new JSONObject();
        Optional<User> findUser = userRepository.findById(user.getId());
        Optional<User> otherUser = userRepository.findById(otherUserId);
        if(findUser.isPresent() && otherUser.isPresent()){
            if(otherUser.get().getEmail().equals(findUser)){//만약 Id가 똑같다면 본인프로파일
                object.put("myProfile",true); //내 프로필임
            }
            else{
                object.put("myProfile",false);
            }
            object.put("name",user.getName());
            object.put("introduction",user.getIntroduction());
            object.put("success",true);
        }
        else{
            object.put("success",false);
        }
        return object;
    }
}
