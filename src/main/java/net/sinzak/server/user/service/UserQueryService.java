package net.sinzak.server.user.service;

import lombok.RequiredArgsConstructor;
import net.sinzak.server.common.PropertyUtil;
import net.sinzak.server.config.auth.dto.SessionUser;
import net.sinzak.server.user.dto.respond.GetFollowDto;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.user.dto.respond.UserDto;
import net.sinzak.server.user.repository.UserRepository;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

import javax.management.InstanceNotFoundException;
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

    public UserDto getUserProfile(Long otherUserId, User user) {
        Optional<User> findUser = userRepository.findById(otherUserId);
        return checkIfTwoUserPresent(user,findUser);
    }
    public UserDto checkIfTwoUserPresent(User user,Optional<User> findUser){
        if(!findUser.isPresent()){
            throw new IllegalArgumentException("찾는 유저가 존재하지 않음");
        }
        return makeUserDto(user,findUser);
    }
    private UserDto makeUserDto(User user, Optional<User> findUser) {
        refreshFollowNumber(findUser); //팔로우,팔로잉 숫자-> 한글
        UserDto userDto = UserDto.builder()
                .name(findUser.get().getName())
                .introduction(findUser.get().getIntroduction())
                .followingNumber(findUser.get().getFollowingNum())
                .followerNumber(findUser.get().getFollowerNum())
                .myProfile(checkIfMyProfile(user,findUser))
                .build();
        return userDto;
    }
    public boolean checkIfMyProfile(User user, Optional<User> findUser){
        if(user == null){
            return false;
        }
        if(findUser.get().getId().equals(user.getId())){
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



}
