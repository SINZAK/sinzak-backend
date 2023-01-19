package net.sinzak.server.user.service;

import lombok.RequiredArgsConstructor;
import net.sinzak.server.CustomJSONArray;
import net.sinzak.server.common.PropertyUtil;
import net.sinzak.server.common.error.InstanceNotFoundException;
import net.sinzak.server.common.error.UserNotFoundException;
import net.sinzak.server.user.domain.SearchHistory;
import net.sinzak.server.user.dto.respond.GetFollowDto;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.user.dto.respond.UserDto;
import net.sinzak.server.user.repository.SearchHistoryRepository;
import net.sinzak.server.user.repository.UserRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserQueryService {

    private final UserRepository userRepository;
    private final SearchHistoryRepository historyRepository;
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
    public UserDto getUserProfile(Long userId, User user) {
        Optional<User> findUser = userRepository.findByIdFetchFollowerList(userId);
        //System.out.println("쿼리 수 확인");
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
                .imageUrl(findUser.get().getPicture())
                .univ(findUser.get().getUniv())
                .ifFollow(checkIfFollowFindUser(user,findUser))
                .cert_uni(findUser.get().isCert_uni())
                .build();
        return userDto;
    }
    public boolean checkIfFollowFindUser(User user,Optional<User> findUser){
        if(user== null){
            return false;
        }
        if(findUser.get().getFollowerList().contains(user.getId())){
            return true;
        }
        return false;
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
    public JSONObject getFollowerDtoList(Long userId){
        Set<Long> followerList = userRepository.findById(userId).get().getFollowerList();
        return getGetFollowDtoList(followerList);
    }
    //팔로잉가져오기
    public JSONObject getFollowingDtoList(Long userId){
        Set<Long> followingList = userRepository.findById(userId).get().getFollowingList();
        return getGetFollowDtoList(followingList);
    }
    private JSONObject getGetFollowDtoList(Set<Long> followList) {
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
        return PropertyUtil.response(getFollowingDtoList);
    }

    @Transactional(readOnly = true)
    public JSONObject showSearchHistory(User User){
        User user = historyRepository.findByEmailFetchHistoryList(User.getEmail()).orElseThrow(InstanceNotFoundException::new);
        List<JSONArray> searchList = new ArrayList<>();
        for (SearchHistory history : user.getHistoryList()) {
            CustomJSONArray tuple = new CustomJSONArray(history.getId(),history.getWord()); /** [358,"가나"] */
            searchList.add(tuple);
        }
        searchList.sort((o1, o2) -> (int) ((Long)o2.get(0)-(Long)o1.get(0)));
        return PropertyUtil.response(searchList);
    }

    @Transactional
    public JSONObject deleteSearchHistory(Long id, User User){
        User user = historyRepository.findByEmailFetchHistoryList(User.getEmail()).orElseThrow(InstanceNotFoundException::new);
        for (SearchHistory history : user.getHistoryList()) {
            if(history.getId().equals(id))
                historyRepository.delete(history);
        }
        return PropertyUtil.response(true);
    }

}
