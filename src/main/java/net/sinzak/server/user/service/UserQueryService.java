package net.sinzak.server.user.service;

import lombok.RequiredArgsConstructor;
import net.sinzak.server.CustomJSONArray;
import net.sinzak.server.common.PropertyUtil;
import net.sinzak.server.common.error.InstanceNotFoundException;
import net.sinzak.server.common.error.UserNotFoundException;
import net.sinzak.server.product.domain.Product;
import net.sinzak.server.product.domain.ProductWish;
import net.sinzak.server.product.repository.ProductWishRepository;
import net.sinzak.server.user.domain.Report;
import net.sinzak.server.user.domain.SearchHistory;
import net.sinzak.server.user.dto.respond.*;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.user.repository.ReportRepository;
import net.sinzak.server.user.repository.SearchHistoryRepository;
import net.sinzak.server.user.repository.UserRepository;
import net.sinzak.server.work.domain.Work;
import net.sinzak.server.work.domain.WorkWish;
import net.sinzak.server.work.repository.WorkWishRepository;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserQueryService {

    private final UserRepository userRepository;
    private final SearchHistoryRepository historyRepository;
    private final WorkWishRepository workWishRepository;
    private final ProductWishRepository productWishRepository;
    private final ReportRepository reportRepository;

    public JSONObject getMyProfile(User user){
        JSONObject obj = new JSONObject();
        User findUser = userRepository.findByIdFetchProductPostList(user.getId()).orElseThrow(()-> new UserNotFoundException(UserNotFoundException.USER_NOT_LOGIN));
        List<ProfileShowForm> productShowForms = makeProductShowForm(findUser.getProductPostList());
        obj.put("products", productShowForms);
        List<ProfileShowForm> workShowForms = makeWorkShowForm(findUser.getWorkPostList(),false);
        obj.put("works", workShowForms);
        List<ProfileShowForm> workEmployShowForms = makeWorkShowForm(findUser.getWorkPostList(),true);
        obj.put("workEmploys",workEmployShowForms);
        obj.put("profile",makeUserDto(user,findUser));
        return PropertyUtil.response(obj);
    }

    public JSONObject getWishList(User loginUser){
        List<WorkWish> workWishes= Optional
                .ofNullable(workWishRepository.findByUserIdFetchWork(loginUser.getId()))
                .orElseThrow(() -> new UserNotFoundException(UserNotFoundException.USER_NOT_LOGIN));
        List<ProductWish> productWishes = productWishRepository.findByUserIdFetchProduct(loginUser.getId());
        JSONObject obj = new JSONObject();
        List<WishShowForm> workWishShowForms = makeWorkWishShowForms(workWishes);
        obj.put("workWishes",workWishShowForms);
        List<WishShowForm> productWishShowForms = makeProductWishShowForms(productWishes);
        obj.put("productWishes",productWishShowForms);
        return PropertyUtil.response(obj);
    }

    public JSONObject getWorkEmploys(User user){
        JSONObject obj = new JSONObject();
        User loginUser = userRepository.findByIdFetchWorkPostList(user.getId()).orElseThrow(()->new UserNotFoundException(UserNotFoundException.USER_NOT_LOGIN));
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


    public JSONObject getUserProfile(Long userId, User user) {
        JSONObject obj = new JSONObject();
        User findUser = userRepository.findByIdFetchProductPostList(userId).orElseThrow(()->new UserNotFoundException(UserNotFoundException.USER_NOT_FOUND));
        List<ProfileShowForm> productShowForms = makeProductShowForm(findUser.getProductPostList());
        obj.put("products", productShowForms);
        List<ProfileShowForm> workShowForms = makeWorkShowForm(findUser.getWorkPostList(),false);
        obj.put("works", workShowForms);
        obj.put("profile",makeUserDto(user,findUser));
        return PropertyUtil.response(obj);
    }
    private UserDto makeUserDto(User user, User findUser) {
        return UserDto.builder()
                .userId(findUser.getId())
                .name(findUser.getNickName())
                .introduction(findUser.getIntroduction())
                .portFolioUrl(findUser.getPortFolioUrl())
                .followingNumber(findUser.getFollowingNum())
                .followerNumber(findUser.getFollowerNum())
                .myProfile(checkIfMyProfile(user,findUser))
                .imageUrl(findUser.getPicture())
                .univ(findUser.getUniv())
                .isFollow(checkIfFollowFindUser(user,findUser))
                .cert_uni(findUser.isCert_uni())
                .cert_celeb(findUser.isCert_celeb())
                .categoryLike(findUser.getCategoryLike())
                .build();
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


    public JSONObject getFollowerDtoList(Long userId){
        Set<Long> followerList = userRepository.findByIdFetchFollowerList(userId).orElseThrow(UserNotFoundException::new).getFollowerList();
        return makeFollowDtos(followerList);
    }


    public JSONObject getFollowingDtoList(Long userId){
        Set<Long> followingList = userRepository.findByIdFetchFollowingList(userId).orElseThrow(UserNotFoundException::new).getFollowingList();
        return makeFollowDtos(followingList);
    }

    private JSONObject makeFollowDtos(Set<Long> followList) {
        List<GetFollowDto> getFollowDtoList = new ArrayList<>();
        for(Long follow : followList){
            Optional<User> findUser = userRepository.findByIdNotDeleted(follow);
            if(findUser.isPresent()){
                GetFollowDto getFollowDto = GetFollowDto.builder()
                        .userId(findUser.get().getId())
                        .name(findUser.get().getNickName())
                        .picture(findUser.get().getPicture())
                        .build();
                getFollowDtoList.add(getFollowDto);
            }
        }
        return PropertyUtil.response(getFollowDtoList);
    }


    public JSONObject showReportList(User User){
        User loginUser = userRepository.findByIdFetchReportList(User.getId()).orElseThrow(UserNotFoundException::new);
        List<Report> reportList = reportRepository.findByUserId(loginUser.getId());

        List<ReportRespondDto> reportRespondDtos = reportList.stream()
                .filter(report -> !report.getOpponentUser().isDelete())
                .map(report -> new ReportRespondDto(report.getOpponentUser().getId(), report.getOpponentUser().getNickName(), report.getOpponentUser().getPicture()))
                .collect(Collectors.toList());
        return PropertyUtil.response(reportRespondDtos);
    }


    public JSONObject showSearchHistory(User User){
        User user = historyRepository.findByIdFetchHistoryList(User.getId()).orElseThrow(InstanceNotFoundException::new);
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

}
