package net.sinzak.server.user.service;

import com.google.api.client.json.Json;
import lombok.RequiredArgsConstructor;
import net.sinzak.server.common.PropertyUtil;
import net.sinzak.server.common.error.InstanceNotFoundException;
import net.sinzak.server.common.error.UserNotFoundException;
import net.sinzak.server.user.domain.SearchHistory;
import net.sinzak.server.user.dto.respond.GetFollowDto;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.user.dto.respond.UserDto;
import net.sinzak.server.user.repository.SearchHistoryRepository;
import net.sinzak.server.user.repository.UserRepository;
import org.json.simple.JSONObject;
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
    private final SearchHistoryRepository historyRepository;
//    @PostConstruct
//    public void makeMockData(){
//        User user = new User("insi2000@naver.com","송인서","그림2");
//        User saved = userRepository.save(user);
//        WorkPostDto dto = new WorkPostDto("테스트","내용테스트");
//    }
    public UserDto getMyProfile(User user){
        User findUser = userRepository.findById(user.getId()).orElseThrow(UserNotFoundException::new);
        return makeUserDto(user,findUser);
    }
    public UserDto getUserProfile(Long userId, User user) {
        User findUser = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        //System.out.println("쿼리 수 확인");
        return makeUserDto(user,findUser);
    }
    private UserDto makeUserDto(User user, User findUser) {
        UserDto userDto = UserDto.builder()
                .userId(findUser.getId())
                .name(findUser.getName())
                .introduction(findUser.getIntroduction())
                .followingNumber(findUser.getFollowingNum())
                .followerNumber(findUser.getFollowerNum())
                .myProfile(checkIfMyProfile(user,findUser))
                .imageUrl(findUser.getPicture())
                .univ(findUser.getUniv())
                .ifFollow(checkIfFollowFindUser(user,findUser))
                .cert_uni(findUser.isCert_uni())
                .build();
        return userDto;
    }
    public boolean checkIfFollowFindUser(User user,User findUser){
        if(user== null){
            return false;
        }
        if(findUser.getFollowerList().contains(user.getId())){
            return true;
        }
        return false;
    }
    public boolean checkIfMyProfile(User user, User findUser){
        if(user == null){
            return false;
        }
        if(findUser.getId().equals(user.getId())){
            return true;
        }
        return false;
    }
    //팔로워가져오기
    public JSONObject getFollowerDtoList(Long userId){
        Set<Long> followerList = userRepository.findByIdFetchFollowerList(userId).orElseThrow(UserNotFoundException::new).getFollowerList();
        return getGetFollowDtoList(followerList);
    }
    //팔로잉가져오기
    public JSONObject getFollowingDtoList(Long userId){
        Set<Long> followingList = userRepository.findByIdFetchFollowingList(userId).orElseThrow(UserNotFoundException::new).getFollowingList();
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
        JSONObject obj = new JSONObject();
        for (SearchHistory history : user.getHistoryList()) {
            obj.put(String.valueOf(history.getId()),history.getWord());
        }
        return PropertyUtil.response(obj);
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
