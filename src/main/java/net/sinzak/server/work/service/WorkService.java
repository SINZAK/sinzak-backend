package net.sinzak.server.work.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sinzak.server.chatroom.domain.ChatRoom;
import net.sinzak.server.common.PostService;
import net.sinzak.server.common.PropertyUtil;
import net.sinzak.server.common.dto.ActionForm;
import net.sinzak.server.common.error.PostNotFoundException;
import net.sinzak.server.common.error.UserNotFoundException;
import net.sinzak.server.image.S3Service;
import net.sinzak.server.common.dto.SuggestDto;
import net.sinzak.server.product.dto.SellDto;
import net.sinzak.server.product.dto.ShowForm;
import net.sinzak.server.user.domain.SearchHistory;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.user.repository.SearchHistoryRepository;
import net.sinzak.server.user.repository.UserRepository;
import net.sinzak.server.work.domain.*;
import net.sinzak.server.work.dto.DetailWorkForm;
import net.sinzak.server.work.dto.WorkEditDto;
import net.sinzak.server.work.dto.WorkPostDto;
import net.sinzak.server.work.repository.*;


import org.json.simple.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
@Slf4j
public class WorkService implements PostService<Work, WorkPostDto, WorkWish, WorkLikes> {
    private final UserRepository userRepository;
    private final WorkRepository workRepository;
    private final WorkWishRepository workWishRepository;
    private final WorkImageRepository imageRepository;
    private final WorkLikesRepository likesRepository;
    private final WorkSellRepository workSellRepository;
    private final WorkSuggestRepository suggestRepository;
    private final WorkQDSLRepositoryImpl QDSLRepository;
    private final SearchHistoryRepository historyRepository;
    private final S3Service s3Service;

    @Transactional
    public List<ChatRoom> getChatRoom(Long productId){
        Work work = workRepository.findByIdFetchChatRooms(productId).orElseThrow(PostNotFoundException::new);
        return work.getChatRooms();
    }
    @Transactional(rollbackFor = {Exception.class})
    public JSONObject makePost(User User, WorkPostDto postDto){
        User user = userRepository.findByIdFetchWorkPostList(User.getId()).orElseThrow(UserNotFoundException::new);
        Work work = Work.builder()
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .author(user.getNickName())
                .univ(user.getUniv())
                .category(postDto.getCategory())
                .price(postDto.getPrice())
                .suggest(postDto.isSuggest())
                .employment(postDto.isEmployment()).build();
        work.setUser(user);
        Long workId = workRepository.save(work).getId();
        return PropertyUtil.response(workId);
    }

    public JSONObject saveImageInS3AndWork(User user, List<MultipartFile> multipartFiles, Long id) {
        Work work = workRepository.findByIdNotDeleted(id).orElseThrow(PostNotFoundException::new);
        if(!user.getId().equals(work.getUser().getId()))
            return PropertyUtil.responseMessage("작성자가 아닙니다.");
        for (MultipartFile img : multipartFiles) {
            try{
                uploadImageAndSaveUrl(multipartFiles, work, img);
            }
            catch (Exception e){
                return PropertyUtil.responseMessage("이미지 저장 실패");
            }
        }
        return PropertyUtil.response(true);
    }

    private void uploadImageAndSaveUrl(List<MultipartFile> multipartFiles, Work work, MultipartFile img) {
        String url = uploadImageAndSetThumbnail(multipartFiles, work, img);
        saveImageUrl(work, url);
    }

    private String uploadImageAndSetThumbnail(List<MultipartFile> multipartFiles, Work work, MultipartFile img) {
        String url = s3Service.uploadImage(img);
        if(img.equals(multipartFiles.get(0)))
            work.setThumbnail(url);
        return url;
    }

    private void saveImageUrl(Work work, String url) {
        WorkImage image = new WorkImage(url, work);
        work.addImage(image);
        imageRepository.save(image);
    }

    @Transactional(rollbackFor = {Exception.class})
    public JSONObject deleteImage(User User, Long workId, String url){   // 글 생성
        Work work = workRepository.findByIdNotDeleted(workId).orElseThrow(PostNotFoundException::new);
        if(!User.getId().equals(work.getUser().getId()))
            return PropertyUtil.responseMessage("해당 작품의 작가가 아닙니다.");
        if(work.getImages().size()==1)
            return PropertyUtil.responseMessage("최소 1개 이상의 이미지를 보유해야 합니다.");

        for (WorkImage image : work.getImages()) {
            if(image.getImageUrl().equals(url)){
                imageRepository.delete(image);
                work.getImages().remove(image);
                break;
            }
        }
        s3Service.deleteImage(url);
        return PropertyUtil.response(workId);
    }

    @Transactional(rollbackFor = {Exception.class})
    public JSONObject editPost(User User, Long workId, WorkEditDto editDto){
        User user = userRepository.findByIdNotDeleted(User.getId()).orElseThrow(UserNotFoundException::new);
        Work work = workRepository.findByIdNotDeleted(workId).orElseThrow(PostNotFoundException::new);
        if(!user.getId().equals(work.getUser().getId()))
            return PropertyUtil.responseMessage("글 작성자가 아닙니다.");

        work.editPost(editDto);
        workRepository.save(work);
        return PropertyUtil.response(true);
    }

    @Transactional(rollbackFor = {Exception.class})
    public JSONObject deletePost(User User, Long workId){   // 글 생성
        User user = userRepository.findByIdNotDeleted(User.getId()).orElseThrow(UserNotFoundException::new);
        Work work = workRepository.findByIdFetchChatRooms(workId).orElseThrow(PostNotFoundException::new);
        if(!user.getId().equals(work.getUser().getId()))
            return PropertyUtil.responseMessage("글 작성자가 아닙니다.");
        deleteImagesInPost(work);
        work.setDeleted(true);
        return PropertyUtil.response(true);
    }



    private void deleteImagesInPost(Work work) {
        for (WorkImage image : work.getImages()) {
            s3Service.deleteImage(image.getImageUrl());
        }
    }

    @Transactional
    public JSONObject showDetail(Long id, User User){   // 글 상세 확인
        User user = userRepository.findByIdFetchFollowingAndLikesList(User.getId()).orElseThrow(UserNotFoundException::new);
        Work work = workRepository.findByIdFetchWorkNotDeletedWishAndUser(id).orElseThrow(PostNotFoundException::new);
        DetailWorkForm detailForm = makeWorkDetailForm(work);
        if(!work.getUser().isDelete()){
            User postUser = work.getUser();
            detailForm.setUserInfo(postUser.getId(),postUser.getNickName(),postUser.getPicture(),postUser.getUniv(),postUser.isCert_uni(),postUser.isCert_celeb(), postUser.getFollowerNum());
        }
        else
            detailForm.setUserInfo(null, "탈퇴한 회원", null, "??", false, false, "0");

        if(user.getId().equals(work.getUser().getId()))
            detailForm.setMyPost();

        boolean isLike = checkIsLikes(user.getWorkLikesList(), work);
        boolean isWish = checkIsWish(user, work.getWorkWishList());
        boolean isFollowing  = false;
        if(work.getUser()!=null)
            isFollowing =checkIsFollowing(user.getFollowingList(), work);

        detailForm.setUserAction(isLike, isWish, isFollowing);
        work.addViews();
        return PropertyUtil.response(detailForm);
    }

    private DetailWorkForm makeWorkDetailForm(Work work) {
        DetailWorkForm detailForm;
        detailForm = DetailWorkForm.builder()
                .id(work.getId())
                .author(work.getAuthor())
                .images(getImages(work))
                .title(work.getTitle())
                .price(work.getPrice())
                .topPrice(work.getTopPrice())
                .category(work.getCategory())
                .date(work.getCreatedDate().toString())
                .content(work.getContent())
                .suggest(work.isSuggest())
                .likesCnt(work.getLikesCnt())
                .views(work.getViews())
                .wishCnt(work.getWishCnt())
                .chatCnt(work.getChatCnt())
                .employment(work.isEmployment())
                .complete(work.isComplete()).build();
        return detailForm;
    }

    @Transactional
    public JSONObject showDetail(Long id){   // 글 상세 확인
        Work work = workRepository.findByIdFetchWorkNotDeletedWishAndUser(id).orElseThrow(PostNotFoundException::new);
        DetailWorkForm detailForm =makeWorkDetailForm(work);
        if(!work.getUser().isDelete()){
            User postUser = work.getUser();
            detailForm.setUserInfo(postUser.getId(),postUser.getNickName(),postUser.getPicture(),postUser.getUniv(),postUser.isCert_uni(),postUser.isCert_celeb(), postUser.getFollowerNum());
        }
        else{
            detailForm.setUserInfo(null, "탈퇴한 회원", null, "??", false, false, "0");
            return PropertyUtil.response(detailForm);
        }
        detailForm.setUserAction(false,false,false);
        work.addViews();
        return PropertyUtil.response(detailForm);
    }

    public boolean checkIsLikes(List<WorkLikes> userLikesList, Work work) {
        boolean isLike = false;
        for (WorkLikes likes : userLikesList) {
            if (likes.getWork().getId().equals(work.getId())) {
                isLike = true;
                break;
            }
        }
        return isLike;
    }

    public boolean checkIsWish(User user, List<WorkWish> workWishList) {
        boolean isWish = false;
        for (WorkWish workWish : workWishList) {
            if(workWish.getUser().getId().equals(user.getId())){
                isWish = true;
                break;
            }
        }
        return isWish;
    }

    public boolean checkIsFollowing(Set<Long> userFollowingList, Work work) {
        boolean isFollowing = false;
        for (Long followingId : userFollowingList) {
            if(work.getUser().getId().equals(followingId)){
                isFollowing = true;
                break;
            }
        }
        return isFollowing;
    }



    public List<String> getImages(Work work) {
        List<String> imagesUrl = new ArrayList<>();
        for (WorkImage image : work.getImages()) {
            imagesUrl.add(image.getImageUrl());
        }
        return imagesUrl;
    }

    @Transactional
    public JSONObject wish(User User, @RequestBody ActionForm form){
        JSONObject obj = new JSONObject();
        User user = userRepository.findByIdFetchWorkWishList(User.getId()).orElseThrow(UserNotFoundException::new);
        List<WorkWish> wishList = user.getWorkWishList();
        boolean isWish=false;
        Work work = workRepository.findByIdNotDeleted(form.getId()).orElseThrow(PostNotFoundException::new);

        if(wishList.size()!=0){
            for (WorkWish wish : wishList) {
                if(work.equals(wish.getWork())) {
                    isWish = true;
                    break;
                }
            }
        }

        if (form.isMode() && !isWish){
            work.plusWishCnt();
            WorkWish connect = WorkWish.createConnect(work, user);
            workWishRepository.save(connect);
            isWish=true;
            obj.put("success",true);
        }
        else if(!form.isMode() && isWish){
            work.minusWishCnt();
            for (WorkWish wish : wishList) {
                if(work.equals(wish.getWork())) {
                    workWishRepository.delete(wish);
                    isWish = false;
                    break;
                }
            }
            obj.put("success",true);
        }
        else
            obj.put("success",false);
        obj.put("isWish",isWish);
        return obj;

    }

    @Transactional
    public JSONObject likes(User User, @RequestBody ActionForm form){
        JSONObject obj = new JSONObject();
        User user = userRepository.findByIdFetchLikesList(User.getId()).orElseThrow(UserNotFoundException::new);
        List<WorkLikes> userLikesList = user.getWorkLikesList();
        boolean isLike=false;
        Work work = workRepository.findByIdNotDeleted(form.getId()).orElseThrow(PostNotFoundException::new);

        if(userLikesList.size()!=0){
            for (WorkLikes like : userLikesList) {
                if(work.equals(like.getWork())) {
                    isLike = true;
                    break;
                }
            }
        }

        if (form.isMode() && !isLike){
            work.plusLikesCnt();
            WorkLikes connect = WorkLikes.createConnect(work, user);
            likesRepository.save(connect);
            isLike=true;
            obj.put("success",true);
        }
        else if(!form.isMode() && isLike){
            work.minusLikesCnt();
            for (WorkLikes like : userLikesList) { //유저의 찜목록과 현재 누른 작품의 찜과 비교
                if(work.equals(like.getWork())) {  //같으면 이미 찜 누른 항목
                    likesRepository.delete(like);
                    isLike = false;
                    break;
                }
            }
            obj.put("success",true);
        }
        else
            obj.put("success",false);
        obj.put("isLike",isLike);
        return obj;
    }

    @Transactional
    public JSONObject sell(User User, @RequestBody SellDto dto){
        User user = userRepository.findByIdFetchProductSellList(User.getId()).orElseThrow(UserNotFoundException::new);
        Work work = workRepository.findByIdNotDeleted(dto.getPostId()).orElseThrow(PostNotFoundException::new);
        if(work.isComplete())
            return PropertyUtil.responseMessage("이미 판매완료된 작품입니다.");
        WorkSell connect = WorkSell.createConnect(work, user);
        workSellRepository.save(connect);
        work.setComplete(true);
        return PropertyUtil.response(true);
    }

    @Transactional
    public JSONObject suggest(User User, @RequestBody SuggestDto dto){
        User user = userRepository.findByIdNotDeleted(User.getId()).orElseThrow(UserNotFoundException::new);
        if(suggestRepository.findByUserIdAndWorkId(user.getId(),dto.getId()).isPresent())
            return PropertyUtil.responseMessage("이미 제안을 하신 작품입니다.");
        Work work = workRepository.findByIdNotDeleted(dto.getId()).orElseThrow();
        WorkSuggest connect = WorkSuggest.createConnect(work, user);
        work.setTopPrice(dto.getPrice());
        suggestRepository.save(connect);
        return PropertyUtil.response(true);
    }

    @Transactional
    public PageImpl<ShowForm> workListForUser(User User, String keyword, List<String> categories, String align, boolean employment, Pageable pageable){
        User user  = userRepository.findByIdFetchLikesList(User.getId()).orElseThrow(UserNotFoundException::new);
        if(!keyword.isEmpty())
            saveSearchHistory(keyword, user);
        Page<Work> workList = QDSLRepository.findSearchingByEmploymentAndCategoriesAligned(employment, keyword, categories, align, pageable);
        List<ShowForm> showList = makeShowFormList(user.getWorkLikesList(), workList.getContent());
        return new PageImpl(showList, pageable, workList.getTotalElements());
    }
    public void saveSearchHistory(String keyword, User user) {
        SearchHistory history = SearchHistory.addSearchHistory(keyword, user);
        historyRepository.save(history);
    }

    @Transactional(readOnly = true)
    public PageImpl<ShowForm> workListForGuest(String keyword, List<String> categories, String align, boolean employment, Pageable pageable){
        Page<Work> workList = QDSLRepository.findSearchingByEmploymentAndCategoriesAligned(employment, keyword, categories, align, pageable);
        List<ShowForm> showList = makeShowForm(workList);
        return new PageImpl<>(showList, pageable, workList.getTotalElements());
    }

    private List<ShowForm> makeShowForm(Page<Work> workList) {
        List<ShowForm> showList = new ArrayList<>();
        for (Work work : workList.getContent()) {
            addWorkInJSONFormat(showList, work, false);
        }
        return showList;
    }

    private List<ShowForm> makeShowFormList(List<WorkLikes> userLikesList, List<Work> workList) {
        List<ShowForm> showFormList = new ArrayList<>();
        for (Work work : workList) {
            boolean isLike = checkIsLikes(userLikesList, work);
            addWorkInJSONFormat(showFormList, work, isLike);
        }
        return showFormList;
    }

    private void addWorkInJSONFormat(List<ShowForm> showFormList, Work work, boolean isLike) {
        ShowForm showForm = new ShowForm(work.getId(), work.getTitle(), work.getContent(), work.getAuthor(), work.getPrice(), work.getThumbnail(), work.getCreatedDate().toString(), work.isSuggest(), isLike, work.getLikesCnt(), work.isComplete(), work.getPopularity());
        showFormList.add(showForm);
    }

}

