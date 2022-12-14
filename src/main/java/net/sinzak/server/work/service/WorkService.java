package net.sinzak.server.work.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sinzak.server.common.PostService;
import net.sinzak.server.common.PropertyUtil;
import net.sinzak.server.common.dto.ActionForm;
import net.sinzak.server.common.error.InstanceNotFoundException;
import net.sinzak.server.common.error.UserNotFoundException;
import net.sinzak.server.image.S3Service;
import net.sinzak.server.common.dto.SuggestDto;
import net.sinzak.server.product.dto.ShowForm;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.user.repository.UserRepository;
import net.sinzak.server.work.domain.*;
import net.sinzak.server.work.dto.DetailWorkForm;
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
    private final WorkSuggestRepository suggestRepository;
    private final WorkQDSLRepositoryImpl QDSLRepository;
    private final S3Service s3Service;

    @Transactional(rollbackFor = {Exception.class})
    public JSONObject makePost(User User, WorkPostDto postDto){
        User user = userRepository.findByEmailFetchWorkPostList(User.getEmail()).orElseThrow(UserNotFoundException::new); //?????? ????????? ?????? ??? ??????????????? fetch?????? ????????????.
                            /** ?????? ?????? ?????? ????????? NullPointer ?????? ?????????, ???????????? ?????? ??? ?????? **/
        Work work = Work.builder()
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .author(user.getNickName())
                .univ(user.getUniv())
                .category(postDto.getCategory())
                .price(postDto.getPay())
                .suggest(postDto.isSuggest())
                .employment(postDto.isEmployment()).build();
        work.setUser(user);
        Long workId = workRepository.save(work).getId();
        return PropertyUtil.response(workId);
    }

    public JSONObject saveImageInS3AndWork(User user, List<MultipartFile> multipartFiles, Long id) {
        Work work = workRepository.findById(id).orElseThrow(InstanceNotFoundException::new);
        if(!user.getId().equals(work.getUser().getId()))
            return PropertyUtil.responseMessage("????????? ???????????????.");
        for (MultipartFile img : multipartFiles) {  /** ????????? ??????, s3??? ?????? **/
            try{
                String url = uploadImageAndSetThumbnail(multipartFiles, work, img);
                saveImageUrl(work, url);
            }
            catch (Exception e){
                return PropertyUtil.responseMessage("????????? ?????? ??????");
            }
        }
        return PropertyUtil.response(true);
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

    @Transactional
    public JSONObject showDetail(Long id, User User){   // ??? ?????? ??????
        User user = userRepository.findByEmailFetchFollowingAndLikesList(User.getEmail()).orElseThrow();
        Work work = workRepository.findByIdFetchWorkWishAndUser(id).orElseThrow();

        DetailWorkForm detailForm = DetailWorkForm.builder()
                .id(work.getId())
                .userId(work.getUser().getId())
                .author(work.getAuthor())
                .author_picture(work.getUser().getPicture())
                .univ(work.getUser().getUniv())
                .cert_uni(work.getUser().isCert_uni())
                .cert_celeb(work.getUser().isCert_celeb())
                .followerNum(work.getUser().getFollowerNum())
                .images(getImages(work))  /** ????????? ??????????????? url??? ????????? **/
                .title(work.getTitle())
                .price(work.getPrice())
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

        boolean isLike = checkIsLikes(user.getWorkLikesList(), work);
        boolean isWish = checkIsWish(user, work.getWorkWishList());
        boolean isFollowing  = checkIsFollowing(user.getFollowingList(), work);

        detailForm.setUserAction(isLike, isWish, isFollowing); /** ????????? ?????????, ???, ??????????????? **/
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

    @Transactional
    public JSONObject showDetail(Long id){   // ??? ?????? ??????
        Work work = workRepository.findByIdFetchWorkWishAndUser(id).orElseThrow();

        DetailWorkForm detailForm = DetailWorkForm.builder()
                .id(work.getId())
                .userId(work.getUser().getId())
                .author(work.getAuthor())
                .author_picture(work.getUser().getPicture())
                .univ(work.getUser().getUniv())
                .cert_uni(work.getUser().isCert_uni())
                .cert_celeb(work.getUser().isCert_celeb())
                .followerNum(work.getUser().getFollowerNum())
                .images(getImages(work))  /** ????????? ??????????????? url??? ????????? **/
                .title(work.getTitle())
                .price(work.getPrice())
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

        detailForm.setUserAction(false,false,false);
        work.addViews();
        return PropertyUtil.response(detailForm);
    }

    @Transactional
    public JSONObject wish(User User, @RequestBody ActionForm form){   // ???
        JSONObject obj = new JSONObject();
        User user = userRepository.findByEmailFetchWorkWishList(User.getEmail()).orElseThrow(UserNotFoundException::new); // ?????? ????????? ?????? ??????
        List<WorkWish> wishList = user.getWorkWishList(); //wishList == ????????? ?????? ??? ?????????
        boolean isWish=false;
        Work work = workRepository.findById(form.getId()).orElseThrow(InstanceNotFoundException::new);

        if(wishList.size()!=0){ /** ????????? ?????? ?????? ?????? ????????? ?????? ?????? ???????????? ?????? **/
            for (WorkWish wish : wishList) { //????????? ???????????? ?????? ?????? ????????? ?????? ??????
                if(work.equals(wish.getWork())) {  //????????? ?????? ??? ?????? ??????
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
            for (WorkWish wish : wishList) { //????????? ???????????? ?????? ?????? ????????? ?????? ??????
                if(work.equals(wish.getWork())) {  //????????? ?????? ??? ?????? ??????
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
    public JSONObject likes(User User, @RequestBody ActionForm form){   // ?????????
        JSONObject obj = new JSONObject();
        User user = userRepository.findByEmailFetchLikesList(User.getEmail()).orElseThrow(); // ?????? ??????????????? ?????? ??????
        List<WorkLikes> userLikesList = user.getWorkLikesList(); //userLikesList == ????????? ????????? ?????????
        boolean isLike=false;
        Work work = workRepository.findById(form.getId()).orElseThrow(InstanceNotFoundException::new);

        if(userLikesList.size()!=0){ /** ????????? ???????????? ?????? ?????? ????????? ?????? ?????? ???????????? ?????? **/
            for (WorkLikes like : userLikesList) { //????????? ???????????? ?????? ?????? ????????? ?????? ??????
                if(work.equals(like.getWork())) {  //????????? ?????? ??? ?????? ??????
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
            for (WorkLikes like : userLikesList) { //????????? ???????????? ?????? ?????? ????????? ?????? ??????
                if(work.equals(like.getWork())) {  //????????? ?????? ??? ?????? ??????
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
    public JSONObject suggest(User User, @RequestBody SuggestDto dto){   // ???????????????
        User user = userRepository.findByEmail(User.getEmail()).orElseThrow(UserNotFoundException::new);
        if(suggestRepository.findByUserIdAndWorkId(user.getId(),dto.getId()).isPresent())
            return PropertyUtil.responseMessage("?????? ????????? ?????? ???????????????.");
        Work work = workRepository.findById(dto.getId()).orElseThrow();
        WorkSuggest connect = WorkSuggest.createConnect(work, user);
        work.setTopPrice(dto.getPrice());
        suggestRepository.save(connect);
        return PropertyUtil.response(true);
    }

    public List<String> getImages(Work work) {
        List<String> imagesUrl = new ArrayList<>();
        for (WorkImage image : work.getImages()) {
            imagesUrl.add(image.getImageUrl());  /** ????????? ??????????????? url??? ????????? **/
        }
        return imagesUrl;
    }


    @Transactional(readOnly = true)
    public PageImpl<ShowForm> workListForUser(User User, List<String> categories, String align, boolean employment, Pageable pageable){
        User user  = userRepository.findByEmailFetchLikesList(User.getEmail()).orElseThrow();
        Page<Work> workList;
        if(categories.size()==0)
            workList = workRepository.findAll(employment, pageable);
        else
            workList = QDSLRepository.findNByCategoriesDesc(categories, employment, pageable);  //???????????? ??????????????? ??????
        List<ShowForm> showList = makeShowFormList(user.getWorkLikesList(), workList.getContent());
        standardAlign(align, showList);  /** ????????? ???????????? ?????? **/
        return new PageImpl(showList, pageable, workList.getTotalElements());
    }

    @Transactional(readOnly = true)
    public PageImpl<ShowForm> workListForGuest(List<String> categories, String align, boolean employment, Pageable pageable){
        Page<Work> workList;
        if(categories.size()==0)
            workList = workRepository.findAll(employment, pageable);
        else
            workList = QDSLRepository.findNByCategoriesDesc(categories, employment, pageable);;  //???????????? ??????????????? ??????

        List<ShowForm> showList = new ArrayList<>();
        for (Work work : workList.getContent()) {
            addWorkInJSONFormat(showList, work, false);
        }
        standardAlign(align, showList);  /** ????????? ???????????? ?????? **/
        return new PageImpl<>(showList, pageable, workList.getTotalElements());
    }


    private void standardAlign(String align, List<ShowForm> showList) {
        if (align.equals("recent")) {} //default
        else if (align.equals("recommend"))  /** ????????? **/
            showList.sort((o1, o2) -> o2.getPopularity() - o1.getPopularity());
    }
    private List<ShowForm> makeShowFormList(List<WorkLikes> userLikesList, List<Work> workList) {
        List<ShowForm> showFormList = new ArrayList<>();
        for (Work work : workList) { /** ?????? ?????? ??? ????????? ????????? ?????? ??? ShowForm ?????? ?????? **/
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

