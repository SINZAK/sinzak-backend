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
    private static final int hundredMillion = 100000000;
    private static final int tenThousand =10000;
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
            refreshFollowNumber(findUser);
            putUserInformation(object, findUser);
            object.put("myProfile",checkIfMyProfile(User,findUser));
        }
        return object;
    }
    public void putUserInformation(JSONObject object, Optional<User> findUser) {
        User user = findUser.get();
        object.put("name", user.getName());
        object.put("introduction", user.getIntroduction());
        object.put("followingNumber", user.getFollowingNum());
        object.put("followerNumber", user.getFollowerNum());
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


    ////methods
    public void refreshFollowNumber(Optional<User>findUser){
        User user = findUser.get();
        String followerNum = followNumberTrans(user.getFollowerList().size());
        String followingNum = followNumberTrans(user.getFollowingList().size());
        user.updateFollowNumber(followingNum,followerNum);
    }
    public String followNumberTrans(int number){
        String unit =getUnit(number);
        if(number>=hundredMillion){
            number /= hundredMillion;
        }
        if(number>=tenThousand){
            number /= tenThousand;
        }
        String transNumber = Integer.toString(number);
        if(transNumber.length()>=4){
            transNumber = transNumber.substring(0,1)+","+transNumber.substring(1);
        }
        transNumber +=unit;
        return transNumber;
    }
    public String getUnit(int number){
        if(number>=hundredMillion){
            return "억";
        }
        if(number>=tenThousand){
            return "만";
        }
        return "";
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

}
