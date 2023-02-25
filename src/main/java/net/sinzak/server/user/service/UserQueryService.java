package net.sinzak.server.user.service;

import lombok.RequiredArgsConstructor;
import net.sinzak.server.CustomJSONArray;
import net.sinzak.server.common.PropertyUtil;
import net.sinzak.server.common.error.InstanceNotFoundException;
import net.sinzak.server.common.error.UserNotFoundException;
import net.sinzak.server.product.domain.Product;
import net.sinzak.server.product.domain.ProductWish;
import net.sinzak.server.product.repository.ProductWishRepository;
import net.sinzak.server.user.domain.SearchHistory;
import net.sinzak.server.user.dto.respond.GetFollowDto;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.user.dto.respond.ProfileShowForm;
import net.sinzak.server.user.dto.respond.UserDto;
import net.sinzak.server.user.dto.respond.WishShowForm;
import net.sinzak.server.user.repository.SearchHistoryRepository;
import net.sinzak.server.user.repository.UserRepository;
import net.sinzak.server.work.domain.Work;
import net.sinzak.server.work.domain.WorkWish;
import net.sinzak.server.work.repository.WorkWishRepository;
import org.jetbrains.annotations.NotNull;
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
    private final WorkWishRepository workWishRepository;
    private final ProductWishRepository productWishRepository;

    public JSONObject getMyProfile(User user){
        JSONObject obj = new JSONObject();
        User findUser = userRepository.findByEmailFetchProductPostList(user.getEmail()).orElseThrow(UserNotFoundException::new);
        List<ProfileShowForm> productShowForms = makeProductShowForm(findUser.getProductPostList());
        obj.put("products", productShowForms);
        List<ProfileShowForm> workShowForms = makeWorkShowForm(findUser.getWorkPostList(),false);
        obj.put("works", workShowForms);
        List<ProfileShowForm> workEmployShowForms = makeWorkShowForm(findUser.getWorkPostList(),true);
        obj.put("workEmploys",workEmployShowForms);
        obj.put("profile",makeUserDto(user,findUser));
        return PropertyUtil.response(obj);
    }
    public JSONObject getWishList(Long userId){
        List<WorkWish> workWishes= workWishRepository.findByUserIdFetchWork(userId);
        List<ProductWish> productWishes = productWishRepository.findByUserIdFetchProduct(userId);
        JSONObject obj = new JSONObject();
        List<WishShowForm> workWishShowForms = makeWorkWishShowForms(workWishes);
        obj.put("workWishes",workWishShowForms);
        List<WishShowForm> productWishShowForms = makeProductWishShowForms(productWishes);
        obj.put("productWishes",productWishShowForms);
        return PropertyUtil.response(obj);
    }

    public JSONObject getWorkEmploys(User user){
        JSONObject obj = new JSONObject();
        User findUser = userRepository.findByEmailFetchWorkPostList(user.getEmail()).orElseThrow(UserNotFoundException::new);
        List<ProfileShowForm> workEmploys=  makeWorkShowForm(findUser.getWorkPostList(),true);
        obj.put("workEmploys",workEmploys);
        return PropertyUtil.response(obj);
    }

    @NotNull
    private List<WishShowForm> makeProductWishShowForms(List<ProductWish> productWishes) {
        List<WishShowForm> productWishShowForms = new ArrayList<>();
        for(ProductWish productWish : productWishes){
            Product product = productWish.getProduct();
            WishShowForm wishShowForm = WishShowForm.builder()
                    .id(product.getId())
                    .thumbnail(product.getThumbnail())
                    .complete(product.isComplete())
                    .title(product.getTitle())
                    .price(product.getPrice())
                    .build();
            productWishShowForms.add(wishShowForm);
        }
        return productWishShowForms;
    }

    @NotNull
    private List<WishShowForm> makeWorkWishShowForms(List<WorkWish> workWishes) {
        List<WishShowForm> workWishShowForms = new ArrayList<>();
        for(WorkWish workWish : workWishes){
            Work work = workWish.getWork();
            WishShowForm wishShowForm = WishShowForm.builder()
                    .id(work.getId())
                    .complete(work.isComplete())
                    .price(work.getPrice())
                    .title(work.getTitle())
                    .thumbnail(work.getThumbnail())
                    .build();
            workWishShowForms.add(wishShowForm);
        }
        return workWishShowForms;
    }

    private List<ProfileShowForm> makeProductShowForm(List<Product> productList) {
        List<ProfileShowForm> showFormList = new ArrayList<>();
        System.out.println(productList.size());
        for (Product product : productList) {
            ProfileShowForm form = ProfileShowForm.builder()
                    .id(product.getId())
                    .complete(product.isComplete())
                    .date(product.getCreatedDate())
                    .thumbnail(product.getThumbnail())
                    .title(product.getTitle()).build();
            showFormList.add(form);
        }
        return showFormList;
    }

    private List<ProfileShowForm> makeWorkShowForm(Set<Work> workList,boolean isEmploy) {
        List<ProfileShowForm> showFormList = new ArrayList<>();
        for (Work work : workList) {
            if(work.isEmployment()==isEmploy){
                ProfileShowForm form = ProfileShowForm.builder()
                        .id(work.getId())
                        .complete(work.isComplete())
                        .date(work.getCreatedDate())
                        .thumbnail(work.getThumbnail())
                        .title(work.getTitle()).build();
                showFormList.add(form);
            }
        }
        showFormList.sort((o1, o2) -> (int) (o2.getId()-o1.getId()));
        return showFormList;
    }

    public JSONObject getUserProfile(Long userId, User user) {
        JSONObject obj = new JSONObject();
        User findUser = userRepository.findByIdFetchProductPostList(userId).orElseThrow(UserNotFoundException::new);
        List<ProfileShowForm> productShowForms = makeProductShowForm(findUser.getProductPostList());
        obj.put("products", productShowForms);
        List<ProfileShowForm> workShowForms = makeWorkShowForm(findUser.getWorkPostList(),false);
        obj.put("works", workShowForms);
        obj.put("profile",makeUserDto(user,findUser));
        //System.out.println("쿼리 수 확인");
        return PropertyUtil.response(obj);
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
                .isFollow(checkIfFollowFindUser(user,findUser))
                .cert_uni(findUser.isCert_uni())
                .categoryLike(findUser.getCategoryLike())
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

}
