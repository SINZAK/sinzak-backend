package net.sinzak.server.user.service;

import lombok.RequiredArgsConstructor;
import net.sinzak.server.CustomJSONArray;
import net.sinzak.server.common.PropertyUtil;
import net.sinzak.server.common.UserUtils;
import net.sinzak.server.common.error.InstanceNotFoundException;
import net.sinzak.server.common.error.UserNotFoundException;
import net.sinzak.server.product.domain.Product;
import net.sinzak.server.product.domain.ProductWish;
import net.sinzak.server.product.repository.ProductWishRepository;
import net.sinzak.server.user.domain.Report;
import net.sinzak.server.user.domain.SearchHistory;
import net.sinzak.server.user.domain.follow.Follow;
import net.sinzak.server.user.domain.follow.Following;
import net.sinzak.server.user.dto.respond.*;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.user.repository.*;
import net.sinzak.server.work.domain.Work;
import net.sinzak.server.work.domain.WorkWish;
import net.sinzak.server.work.repository.WorkWishRepository;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserQueryService {
    private final UserUtils userUtils;
    private final UserRepository userRepository;
    private final SearchHistoryRepository historyRepository;
    private final WorkWishRepository workWishRepository;
    private final ProductWishRepository productWishRepository;
    private final ReportRepository reportRepository;
    private final FollowingRepository followingRepository;
    private final FollowerRepository followerRepository;
    private final UserQDSLRepositoryImpl QDSLRepository;
    private final FollowRepository followRepository;

    public JSONObject getMyProfile(){
        JSONObject obj = new JSONObject();
        User findUser = userRepository.findByIdFetchProductPostList(userUtils.getCurrentUserId()).orElseThrow(()-> new UserNotFoundException(UserNotFoundException.USER_NOT_LOGIN));
        List<ProfileShowForm> productShowForms = makeProductShowForm(findUser.getProductPostList());
        obj.put("products", productShowForms);
        List<ProfileShowForm> workShowForms = makeWorkShowForm(findUser.getWorkPostList(),false);
        obj.put("works", workShowForms);
        List<ProfileShowForm> workEmployShowForms = makeWorkShowForm(findUser.getWorkPostList(),true);
        obj.put("workEmploys",workEmployShowForms);
        obj.put("profile",makeUserDto(userUtils.getCurrentUserId(), findUser));
        return PropertyUtil.response(obj);
    }

    public JSONObject getWishList(){
        List<WorkWish> workWishes= Optional
                .ofNullable(workWishRepository.findByUserIdFetchWork(userUtils.getCurrentUserId()))
                .orElseThrow(() -> new UserNotFoundException(UserNotFoundException.USER_NOT_LOGIN));
        List<ProductWish> productWishes = productWishRepository.findByUserIdFetchProduct(userUtils.getCurrentUserId());
        JSONObject obj = new JSONObject();
        List<WishShowForm> workWishShowForms = makeWorkWishShowForms(workWishes);
        obj.put("workWishes",workWishShowForms);
        List<WishShowForm> productWishShowForms = makeProductWishShowForms(productWishes);
        obj.put("productWishes",productWishShowForms);
        return PropertyUtil.response(obj);
    }

    public JSONObject getWorkEmploys(){
        JSONObject obj = new JSONObject();
        User loginUser = userRepository.findByIdFetchWorkPostList(userUtils.getCurrentUserId()).orElseThrow(()->new UserNotFoundException(UserNotFoundException.USER_NOT_LOGIN));
        List<ProfileShowForm> workEmploys = makeWorkShowForm(loginUser.getWorkPostList(),true);
        obj.put("workEmploys",workEmploys);
        return PropertyUtil.response(obj);
    }

    public boolean checkReported(User postUser,User loginUser){
        List<Report> report = reportRepository.findByUserIdAndOpponentUserIdBoth(postUser.getId(), loginUser.getId());
        if(report.isEmpty()){
            return false;
        }
        return true;
    }


    private List<WishShowForm> makeProductWishShowForms(List<ProductWish> productWishes) {
        return productWishes.stream()
                .map(productWish -> productWish.getProduct())
                .filter(product -> !product.isDeleted())
                .map(product -> WishShowForm.builder()
                        .id(product.getId())
                        .thumbnail(product.getThumbnail())
                        .complete(product.isComplete())
                        .title(product.getTitle())
                        .price(product.getPrice())
                        .build())
                .collect(Collectors.toList());
    }


    private List<WishShowForm> makeWorkWishShowForms(List<WorkWish> workWishes) {
        return workWishes.stream()
                .map(workWish -> workWish.getWork())
                .filter(work -> !work.isDeleted())
                .map(work -> WishShowForm.builder()
                        .id(work.getId())
                        .complete(work.isComplete())
                        .price(work.getPrice())
                        .title(work.getTitle())
                        .thumbnail(work.getThumbnail())
                        .build())
                .collect(Collectors.toList());
    }

    private List<ProfileShowForm> makeProductShowForm(List<Product> productList) {
        return productList.stream()
                .filter(product -> !product.isDeleted())
                .map(product -> ProfileShowForm.builder()
                        .id(product.getId())
                        .complete(product.isComplete())
                        .date(product.getCreatedDate())
                        .thumbnail(product.getThumbnail())
                        .title(product.getTitle()).build())
                .collect(Collectors.toList());
    }

    private List<ProfileShowForm> makeWorkShowForm(Set<Work> workList, boolean isEmploy) {
        return workList.stream()
                .filter(work -> !work.isDeleted() && work.isEmployment() == isEmploy)
                .map(work -> ProfileShowForm.builder()
                        .id(work.getId())
                        .complete(work.isComplete())
                        .date(work.getCreatedDate())
                        .thumbnail(work.getThumbnail())
                        .title(work.getTitle()).build())
                .sorted((o1, o2) -> (int) (o2.getId()-o1.getId()))
                .collect(Collectors.toList());
    }


    public JSONObject getUserProfileForUser(Long currentUserId, Long userId) {
        JSONObject obj = new JSONObject();
        User findUser = userRepository.findByIdFetchProductPostList(userId).orElseThrow(()->new UserNotFoundException(UserNotFoundException.USER_NOT_FOUND));
        List<ProfileShowForm> productShowForms = makeProductShowForm(findUser.getProductPostList());
        obj.put("products", productShowForms);
        List<ProfileShowForm> workShowForms = makeWorkShowForm(findUser.getWorkPostList(),false);
        obj.put("works", workShowForms);
        obj.put("profile",makeUserDto(currentUserId, findUser));
        return PropertyUtil.response(obj);
    }

    public JSONObject getUserProfileForGuest(Long userId) {
        JSONObject obj = new JSONObject();
        User findUser = userRepository.findByIdFetchProductPostList(userId).orElseThrow(()->new UserNotFoundException(UserNotFoundException.USER_NOT_FOUND));
        List<ProfileShowForm> productShowForms = makeProductShowForm(findUser.getProductPostList());
        obj.put("products", productShowForms);
        List<ProfileShowForm> workShowForms = makeWorkShowForm(findUser.getWorkPostList(),false);
        obj.put("works", workShowForms);
        obj.put("profile",makeUserDto(0L, findUser));
        return PropertyUtil.response(obj);
    }
    private UserDto makeUserDto(Long loginUserId, User findUser) {
        return UserDto.builder()
                .userId(findUser.getId())
                .name(findUser.getNickName())
                .introduction(findUser.getIntroduction())
                .followingNumber(findUser.getFollowingNum())
                .followerNumber(findUser.getFollowerNum())
                .myProfile(checkIfMyProfile(loginUserId,findUser))
                .imageUrl(findUser.getPicture())
                .univ(findUser.getUniv())
                .isFollow(checkIfFollowFindUser(loginUserId,findUser))
                .cert_uni(findUser.isCert_uni())
                .cert_author(findUser.isCert_author())
                .categoryLike(findUser.getCategoryLike())
                .build();
    }
    public boolean checkIfFollowFindUser(Long loginUserId,User findUser){
        if(loginUserId.equals(0L)){
            return false;
        }
        return findUser
                .getFollowers()
                .stream()
                .map(Follow::getFollowerUser)
                .map(User::getId)
                .peek(System.out::println)
                .anyMatch(x -> x.equals(loginUserId));
    }
    public boolean checkIfMyProfile(Long loginUserId, User findUser){
        if(loginUserId.equals(0L)){
            return false;
        }
        if(findUser.getId().equals(loginUserId)){
            return true;
        }
        return false;
    }


    public JSONObject getFollowerDtoList(Long userId){
        Set<Follow> followers = followRepository.findByFollowingUserIdFetchFollower(userId);
        List<User> users = followers.stream()
                .map(Follow::getFollowerUser)
                .collect(Collectors.toList());
        return makeFollowDtos(users);
    }

    public JSONObject getFollowingDtoList(Long userId){
        Set<Follow> followings = followRepository.findByFollowerUserIdFetchFollowings(userId);
        List<User> users = followings.stream()
                .map(Follow::getFollowingUser)
                .collect(Collectors.toList());
        return makeFollowDtos(users);
    }
    private JSONObject makeFollowDtos(List<User> users) {
        List<GetFollowDto> getFollowDtoList = new ArrayList<>();
        for(User user :users){
            getFollowDtoList.add(
                    GetFollowDto.builder()
                            .userId(user.getId())
                            .name(user.getNickName())
                            .picture(user.getPicture())
                            .build()
            );
        }
        return PropertyUtil.response(getFollowDtoList);
    }


    public JSONObject showReportList(){
        User loginUser = userRepository.findByIdFetchReportList(userUtils.getCurrentUserId()).orElseThrow(UserNotFoundException::new);
        List<Report> reportList = reportRepository.findByUserId(loginUser.getId());

        List<ReportRespondDto> reportRespondDtos = reportList.stream()
                .filter(report -> !report.getOpponentUser().isDelete())
                .map(report -> new ReportRespondDto(report.getOpponentUser().getId(), report.getOpponentUser().getNickName(), report.getOpponentUser().getPicture()))
                .collect(Collectors.toList());
        return PropertyUtil.response(reportRespondDtos);
    }


    public JSONObject showSearchHistory(){
        User user = historyRepository.findByIdFetchHistoryList(userUtils.getCurrentUserId()).orElseThrow(InstanceNotFoundException::new);
        List<SearchHistory> searchHistoryList = getUserHistoryList(user);

        List<CustomJSONArray> histories = searchHistoryList.stream()
                .map(history -> new CustomJSONArray(history.getId(), history.getWord()))
                .collect(Collectors.toList());
        return PropertyUtil.response(histories);
    }

    private List<SearchHistory> getUserHistoryList(User user) {
        List<SearchHistory> historyList = new ArrayList<>(user.getHistoryList());
        historyList.sort((o1, o2) -> (int) (o2.getId()-o1.getId()));
        return historyList;
    }


    public JSONObject getAllUser(){
        List<User> users = userRepository.findAll();
        List<JSONObject> obj = new ArrayList<>();
        for(User user : users){
            JSONObject jsonObject =new JSONObject();
            jsonObject.put("id",user.getId());
            jsonObject.put("email",user.getEmail());
            obj.add(jsonObject);
        }

        return PropertyUtil.response(obj);
    }

    public Optional<String> getUserNickName(Long id){
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        return Optional.of(user.getNickName());
    }


    public Optional<User> getCertifiedRandomUser() throws NoSuchAlgorithmException {
        List<User> users = userRepository.findAllNotDeleted();
        List<User> certUsers = users.stream()
                .filter(User::isCert_author)
                .collect(Collectors.toList());
        if(certUsers.size() == 0)
            return Optional.empty();
        Random random = SecureRandom.getInstanceStrong();
        int randomNumber = random.nextInt(certUsers.size());
        User randomUser = certUsers.get(randomNumber);
        return Optional.of(randomUser);
    }

}
