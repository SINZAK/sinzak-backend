package net.sinzak.server.user.service;

import lombok.RequiredArgsConstructor;
import net.sinzak.server.config.auth.dto.SessionUser;
import net.sinzak.server.user.dto.respond.GetFollowDto;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.user.repository.UserRepository;
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

//    @PostConstruct
//    public void makeMockData(){
//        User user = new User("insi2000@naver.com","송인서","그림2");
//        User saved = userRepository.save(user);
//        WorkPostDto dto = new WorkPostDto("테스트","내용테스트");
//    }

    public JSONObject getUserProfile(Long otherUserId, SessionUser user) {
        JSONObject object = new JSONObject();
        Optional<User> User = userRepository.findByEmail(user.getEmail());
        Optional<User> findUser = userRepository.findById(otherUserId);
        if(User.isPresent() && findUser.isPresent()){
            if(findUser.get().equals(User.get())){//만약 Id가 똑같다면 본인프로파일
                object.put("myProfile",true); //내 프로필임
            }
            else{
                object.put("myProfile",false);
            }
            object.put("name",findUser.get().getName());
            object.put("introduction",findUser.get().getIntroduction());
            object.put("success",true);
        }
        else{
            object.put("success",false);
        }
        return object;
    }
    //팔로우,팔로잉 가져오기
    private List<GetFollowDto> getGetFollowDtoList(Set<Long> followList) {
        List<GetFollowDto> getFollowingDtoList = new ArrayList<>();
        for(Long follow : followList){
            Optional<User> findUser = userRepository.findById(follow);
            if(findUser.isPresent()){
                GetFollowDto getFollowDto = GetFollowDto.builder().userId(findUser.get().getId()).name(findUser.get().getName()).picture(findUser.get().getPicture()).build();
                getFollowingDtoList.add(getFollowDto);
            }
            else{
                followList.remove(follow); //만약 UserRepository에 존재하지 않는다면(유저가 삭제된 거임) 삭제해주기
            }
        }
        return getFollowingDtoList;
    }
    //팔로워가져오기
    public List<GetFollowDto> getFollowerDtoList(Long userId){
        Set<Long> followerList = userRepository.findById(userId).get().getFollowerList();
        return getGetFollowDtoList(followerList);
    }
    //팔로잉가져오기
    public List<GetFollowDto> getFollowingDtoList(Long userId){
        Set<Long> followingList = userRepository.findById(userId).get().getFollowingList();
        return getGetFollowDtoList(followingList);
    }


}
