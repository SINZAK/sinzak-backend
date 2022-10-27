package net.sinzak.server.service;

import lombok.RequiredArgsConstructor;
import net.sinzak.server.config.dto.respond.GetFollowDto;
import net.sinzak.server.domain.User;
import net.sinzak.server.error.InstanceNotFoundException;
import net.sinzak.server.repository.UserRepository;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    public List<GetFollowDto> getFollowerDtoList(Long userId){
        Set<Long> followerList = userRepository.findById(userId).get().getFollowerList();
        return getGetFollowDtoList(followerList);
    }
    public List<GetFollowDto> getFollowingDtoList(Long userId){
        Set<Long> followingList = userRepository.findById(userId).get().getFollowingList();
        return getGetFollowDtoList(followingList);
    }
    private List<GetFollowDto> getGetFollowDtoList(Set<Long> followList) {
        List<GetFollowDto> getFollowingDtoList = new ArrayList<>();
        for(Long follow : followList){
            User findUser = userRepository
                    .findById(follow)
                    .orElseThrow(()->new InstanceNotFoundException("유저가 없습니다"));
            GetFollowDto getFollowDto = GetFollowDto.builder().name(findUser.getName()).picture(findUser.getPicture()).build();
            getFollowingDtoList.add(getFollowDto);
        }
        return getFollowingDtoList;
    }

}
