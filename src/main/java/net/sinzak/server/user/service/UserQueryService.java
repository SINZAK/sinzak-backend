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
        boolean usersPresent = checkIfTwoUserPresent(User,findUser);
        object.put("success",usersPresent);
        if(usersPresent){
            object.put("myProfile",checkIfMyProfile(User,findUser));
            putUserInformation(object, findUser);
        }
        return object;
    }
    public void putUserInformation(JSONObject object, Optional<User> findUser) {
        object.put("name", findUser.get().getName());
        object.put("introduction", findUser.get().getIntroduction());
    }
    public boolean checkIfMyProfile(Optional<User> User, Optional<User> findUser){
        if(findUser.get().equals(User.get())){
            return true;
        }
        return false;
    }
    public boolean checkIfTwoUserPresent(Optional<User> User,Optional<User> findUser){
        if(User.isPresent()&&findUser.isPresent()){
            return true;
        }
        return false;
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
    //팔로우,팔로잉 가져오기
    private List<GetFollowDto> getGetFollowDtoList(Set<Long> followList) {
        List<GetFollowDto> getFollowingDtoList = new ArrayList<>();
        for(Long follow : followList){
            Optional<User> findUser = userRepository.findById(follow);
            if(findUser.isPresent()){
                GetFollowDto getFollowDto = GetFollowDto.builder().
                        userId(findUser.get().getId()).
                        name(findUser.get().getName()).
                        picture(findUser.get().getPicture()).
                        build();
                getFollowingDtoList.add(getFollowDto);
            }
        }
        return getFollowingDtoList;
    }

}
