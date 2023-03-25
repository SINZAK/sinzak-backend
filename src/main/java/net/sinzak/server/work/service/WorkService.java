package net.sinzak.server.work.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sinzak.server.chatroom.domain.ChatRoom;
import net.sinzak.server.common.PostService;
import net.sinzak.server.common.PropertyUtil;
import net.sinzak.server.common.UserUtils;
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
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class WorkService implements PostService<Work, WorkPostDto, WorkWish, WorkLikes> {
    private final UserUtils userUtils;
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


    @Transactional(rollbackFor = {Exception.class})
    public JSONObject makePost(WorkPostDto postDto){
        User user = userRepository.findByIdFetchWorkPostList(userUtils.getCurrentUserId()).orElseThrow(UserNotFoundException::new);
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

    public JSONObject saveImageInS3AndWork(List<MultipartFile> multipartFiles, Long id) {
        Work work = workRepository.findByIdNotDeleted(id).orElseThrow(PostNotFoundException::new);
        if(!userUtils.getCurrentUserId().equals(work.getUser().getId()))
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
    public JSONObject deleteImage(Long workId, String url){   // 글 생성
        Work work = workRepository.findByIdNotDeleted(workId).orElseThrow(PostNotFoundException::new);
        if(!userUtils.getCurrentUserId().equals(work.getUser().getId()))
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
    public JSONObject editPost(Long workId, WorkEditDto editDto){
        Work work = workRepository.findByIdNotDeleted(workId).orElseThrow(PostNotFoundException::new);
        if(!userUtils.getCurrentUserId().equals(work.getUser().getId()))
            return PropertyUtil.responseMessage("글 작성자가 아닙니다.");

        work.editPost(editDto);
        workRepository.save(work);
        return PropertyUtil.response(true);
    }

    @Transactional(rollbackFor = {Exception.class})
    public JSONObject deletePost(Long workId){   // 글 생성
        Work work = workRepository.findByIdFetchChatRooms(workId).orElseThrow(PostNotFoundException::new);
        if(!userUtils.getCurrentUserId().equals(work.getUser().getId()))
            return PropertyUtil.responseMessage("글 작성자가 아닙니다.");
        deleteImagesInPost(work);
        work.setDeleted(true);
        return PropertyUtil.response(true);
    }


    private void deleteImagesInPost(Work work) {
        work.getImages()
                .forEach(img -> s3Service.deleteImage(img.getImageUrl()));
    }

    @Transactional
    public JSONObject showDetailForUser(Long id){   // 글 상세 확인
        User user = userRepository.findByIdFetchFollowingAndLikesList(userUtils.getCurrentUserId()).orElseThrow(UserNotFoundException::new);
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
    public JSONObject showDetailForGuest(Long id){   // 글 상세 확인
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
        return userLikesList.stream().anyMatch(x -> x.getWork().getId().equals(work.getId()));
    }

    public boolean checkIsWish(User user, List<WorkWish> workWishList) {
        return workWishList.stream().anyMatch(x -> x.getUser().getId().equals(user.getId()));
    }

    public boolean checkIsFollowing(Set<Long> userFollowingList, Work work) {
        return userFollowingList.stream().anyMatch(x -> x.equals(work.getUser().getId()));
    }


    public List<String> getImages(Work work) {
        List<String> imagesUrl = new ArrayList<>();
        work.getImages()
                .forEach(img -> imagesUrl.add(img.getImageUrl()));
        return imagesUrl;
    }

    @Transactional
    public JSONObject wish(@RequestBody ActionForm form){
        JSONObject obj = new JSONObject();
        User user = userRepository.findByIdFetchWorkWishList(userUtils.getCurrentUserId()).orElseThrow(UserNotFoundException::new);
        List<WorkWish> wishList = user.getWorkWishList();
        boolean isWish = false;
        boolean success = false;
        Work work = workRepository.findByIdNotDeleted(form.getId()).orElseThrow(PostNotFoundException::new);

        if(wishList.size()!=0){
            if(wishList.stream().anyMatch(wish -> wish.getWork().equals(work)))
                isWish = true;
        }

        if (form.isMode() && !isWish){
            work.plusWishCnt();
            WorkWish connect = WorkWish.createConnect(work, user);
            workWishRepository.save(connect);
            isWish=true;
            success = true;
        }
        else if(!form.isMode() && isWish){
            work.minusWishCnt();
            wishList.stream()
                    .filter(wish -> wish.getWork().equals(work)).findFirst()
                    .ifPresent(workWishRepository::delete);
            success = true;
        }
        obj.put("isWish", isWish);
        obj.put("success", success);
        return obj;

    }

    @Transactional
    public JSONObject likes(@RequestBody ActionForm form){
        JSONObject obj = new JSONObject();
        User user = userRepository.findByIdFetchLikesList(userUtils.getCurrentUserId()).orElseThrow(UserNotFoundException::new);
        List<WorkLikes> likesList = user.getWorkLikesList();
        boolean isLike = false;
        boolean success = false;
        Work work = workRepository.findByIdNotDeleted(form.getId()).orElseThrow(PostNotFoundException::new);

        if(likesList.size()!=0){
            if(likesList.stream().anyMatch(likes -> likes.getWork().equals(work)))
                isLike = true;
        }

        if (form.isMode() && !isLike){
            work.plusLikesCnt();
            WorkLikes connect = WorkLikes.createConnect(work, user);
            likesRepository.save(connect);
            isLike = true;
            success = true;
        }
        else if(!form.isMode() && isLike){
            work.minusLikesCnt();
            likesList.stream()
                    .filter(likes -> likes.getWork().equals(work)).findFirst()
                    .ifPresent(likesRepository::delete);
            success = true;
        }
        obj.put("isLike", isLike);
        obj.put("success", success);
        return obj;
    }

    @Transactional
    public JSONObject sell(@RequestBody SellDto dto){
        User user = userRepository.findByIdFetchProductSellList(userUtils.getCurrentUserId()).orElseThrow(UserNotFoundException::new);
        Work work = workRepository.findByIdNotDeleted(dto.getPostId()).orElseThrow(PostNotFoundException::new);
        if(work.isComplete())
            return PropertyUtil.responseMessage("이미 판매완료된 작품입니다.");
        WorkSell connect = WorkSell.createConnect(work, user);
        workSellRepository.save(connect);
        work.setComplete(true);
        return PropertyUtil.response(true);
    }

    @Transactional
    public JSONObject suggest(@RequestBody SuggestDto dto){
        User user = userUtils.getCurrentUser();
        if(suggestRepository.findByUserIdAndWorkId(user.getId(),dto.getId()).isPresent())
            return PropertyUtil.responseMessage("이미 제안을 하신 작품입니다.");
        Work work = workRepository.findByIdNotDeleted(dto.getId()).orElseThrow();
        WorkSuggest connect = WorkSuggest.createConnect(work, user);
        work.setTopPrice(dto.getPrice());
        suggestRepository.save(connect);
        return PropertyUtil.response(true);
    }

    @Transactional
    public PageImpl<ShowForm> workListForUser(String keyword, List<String> categories, String align, boolean employment, Pageable pageable){
        User user  = userRepository.findByIdFetchLikesList(userUtils.getCurrentUserId()).orElseThrow(UserNotFoundException::new);
        if(!keyword.isEmpty())
            saveSearchHistory(keyword, user);
        Page<Work> workList = QDSLRepository.findSearchingByEmploymentAndCategoriesAligned(employment, keyword, categories, align, pageable);
        List<ShowForm> showList = makeShowForms(user.getWorkLikesList(), workList.getContent());
        return new PageImpl<>(showList, pageable, workList.getTotalElements());
    }
    public void saveSearchHistory(String keyword, User user) {
        SearchHistory history = SearchHistory.addSearchHistory(keyword, user);
        historyRepository.save(history);
    }

    @Transactional(readOnly = true)
    public PageImpl<ShowForm> workListForGuest(String keyword, List<String> categories, String align, boolean employment, Pageable pageable){
        Page<Work> workList = QDSLRepository.findSearchingByEmploymentAndCategoriesAligned(employment, keyword, categories, align, pageable);
        List<ShowForm> showList = makeShowForms(workList);
        return new PageImpl<>(showList, pageable, workList.getTotalElements());
    }


    private List<ShowForm> makeShowForms(Page<Work> workList) {
        return workList.stream()
                .map(work -> makeShowForm(work, false))
                .collect(Collectors.toList());
    }


    private List<ShowForm> makeShowForms(List<WorkLikes> userLikesList, List<Work> workList) {
        return workList.stream()
                .map(work -> makeShowForm(work, checkIsLikes(userLikesList, work)))
                .collect(Collectors.toList());
    }

    private ShowForm makeShowForm(Work work, boolean isLike) {
        return new ShowForm(work.getId(), work.getTitle(), work.getContent(), work.getAuthor(), work.getPrice(), work.getThumbnail(), work.getCreatedDate().toString(), work.isSuggest(), isLike, work.getLikesCnt(), work.isComplete(), work.getPopularity());
    }

}

