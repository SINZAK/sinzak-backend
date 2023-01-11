package net.sinzak.server.user.service;

import lombok.RequiredArgsConstructor;
import net.sinzak.server.common.error.UserNotFoundException;
import net.sinzak.server.user.dto.request.UserIdDto;
import net.sinzak.server.user.dto.respond.GetFollowDto;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.user.dto.respond.UserDto;
import net.sinzak.server.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserQueryService {

    private final UserRepository userRepository;

//    @PostConstruct
//    public void makeMockData(){
//        User user = new User("insi2000@naver.com","송인서","그림2");
//        User saved = userRepository.save(user);
//        WorkPostDto dto = new WorkPostDto("테스트","내용테스트");
//    }
    public UserDto getMyProfile(User user){
        if(user ==null){
            throw new UserNotFoundException("로그인한 유저 존재하지 않음");
        }
        Optional<User> findUser = userRepository.findById(user.getId());
        return makeUserDto(user,findUser);
    }
    public UserDto getUserProfile(UserIdDto userIdDto, User user) {
        Optional<User> findUser = userRepository.findById(userIdDto.getUserId());
        return checkIfTwoUserPresent(user,findUser);
    }
    public UserDto checkIfTwoUserPresent(User user,Optional<User> findUser){
        if(!findUser.isPresent()){
            throw new UserNotFoundException();
        }
        return makeUserDto(user,findUser);
    }
    private UserDto makeUserDto(User user, Optional<User> findUser) {
        findUser.get().updateFollowNumber(); //팔로우,팔로잉 숫자-> 한글
        UserDto userDto = UserDto.builder()
                .userId(findUser.get().getId())
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
    public List<GetFollowDto> getFollowerDtoList(UserIdDto userIdDto){
        Long userId = userIdDto.getUserId();
        Set<Long> followerList = userRepository.findById(userId).get().getFollowerList();
        return getGetFollowDtoList(followerList);
    }
    //팔로잉가져오기
    public List<GetFollowDto> getFollowingDtoList(UserIdDto userIdDto){
        Long userId = userIdDto.getUserId();
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




}
