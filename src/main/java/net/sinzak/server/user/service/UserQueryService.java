package net.sinzak.server.user.service;

import lombok.RequiredArgsConstructor;
import net.sinzak.server.CustomJSONArray;
import net.sinzak.server.common.PropertyUtil;
import net.sinzak.server.common.error.InstanceNotFoundException;
import net.sinzak.server.common.error.UserNotFoundException;
import net.sinzak.server.product.domain.Product;
import net.sinzak.server.user.domain.SearchHistory;
import net.sinzak.server.user.dto.respond.GetFollowDto;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.user.dto.respond.MyPageShowForm;
import net.sinzak.server.user.dto.respond.UserDto;
import net.sinzak.server.user.repository.SearchHistoryRepository;
import net.sinzak.server.user.repository.UserRepository;
import net.sinzak.server.work.domain.Work;
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

    public JSONObject getMyProfile(User user){
        User findUser = userRepository.findByEmailFetchProductPostList(user.getEmail()).orElseThrow(UserNotFoundException::new);
        List<MyPageShowForm> productShowForms = makeProductShowForm(findUser.getProductPostList());
        findUser = userRepository.findByEmailFetchWorkPostList(user.getEmail()).orElseThrow(UserNotFoundException::new);
        List<MyPageShowForm> workShowForms = makeWorkShowForm(findUser.getWorkPostList());  /** fetch 조인 2개 연속으로 하면 꼬여서 두개로 나눔 **/
        JSONObject obj = new JSONObject();
        obj.put("products", productShowForms);
        obj.put("works", workShowForms);
        obj.put("profile",makeUserDto(user,findUser));
        return PropertyUtil.response(obj);
    }

    private List<MyPageShowForm> makeProductShowForm(List<Product> productList) {
        List<MyPageShowForm> showFormList = new ArrayList<>();
        System.out.println(productList.size());
        for (Product product : productList) {
            MyPageShowForm form = new MyPageShowForm(product.getId(), product.getThumbnail());
            showFormList.add(form);
        }
        return showFormList;
    }

    private List<MyPageShowForm> makeWorkShowForm(Set<Work> workList) {
        List<MyPageShowForm> showFormList = new ArrayList<>();
        for (Work work : workList) {
            MyPageShowForm form = new MyPageShowForm(work.getId(), work.getThumbnail());
            showFormList.add(form);
        }
        showFormList.sort((o1, o2) -> (int) (o2.getId()-o1.getId()));
        return showFormList;
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
        List<GetFollowDto> getFollowDtoList = new ArrayList<>();
        for(Long follow : followList){
            Optional<User> findUser = userRepository.findById(follow);
            if(findUser.isPresent()){
                GetFollowDto getFollowDto = GetFollowDto.builder().
                        userId(findUser.get().getId()).
                        name(findUser.get().getName()).
                        picture(findUser.get().getPicture()).
                        build();
                getFollowDtoList.add(getFollowDto);
            }
        }
        return PropertyUtil.response(getFollowDtoList);
    }

    @Transactional
    public JSONObject showSearchHistory(User User){
        User user = historyRepository.findByEmailFetchHistoryList(User.getEmail()).orElseThrow(InstanceNotFoundException::new);
        List<SearchHistory> searchHistoryList = getUserHistoryList(user);
        List<JSONArray> searchList = new ArrayList<>();
        for (SearchHistory history : searchHistoryList) {
            CustomJSONArray tuple = new CustomJSONArray(history.getId(),history.getWord()); /** [358,"가나"] */
            searchList.add(tuple);
        }
        return PropertyUtil.response(searchList);
    }

    private List<SearchHistory> getUserHistoryList(User user) {
        List<SearchHistory> historyList = new ArrayList<>(user.getHistoryList());
        historyList.sort((o1, o2) -> (int) (o2.getId()-o1.getId()));
        return historyList;
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
