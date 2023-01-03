package net.sinzak.server.work.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sinzak.server.config.auth.dto.SessionUser;
import net.sinzak.server.common.dto.ActionForm;
import net.sinzak.server.common.PropertyUtil;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.user.repository.UserRepository;
import net.sinzak.server.work.Work;
import net.sinzak.server.work.WorkWish;
import net.sinzak.server.work.repository.WorkRepository;
import net.sinzak.server.work.repository.WorkWishRepository;
import net.sinzak.server.work.dto.WorkPostDto;
import org.json.simple.JSONObject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class WorkService {
    private final UserRepository userRepository;
    private final WorkRepository workRepository;
    private final WorkWishRepository workWishRepository;

    @Transactional
    public JSONObject makeWorkPost(SessionUser tempUser, WorkPostDto workPost){
        User user = userRepository.findByEmailFetchWP(tempUser.getEmail()).orElseThrow(); //해당 유저의 외주 글 리스트까지 fetch해서 가져오기.
                            /** 존재 하지 않는 유저면 NullPointer 에러 뜰거고, 핸들러가 처리 할 예정 **/
        Work work = Work.builder()
                .title(workPost.getTitle())  //제목
                .content(workPost.getContent()) //내용
                .userName(user.getNickName()) //닉네임
                .univ(user.getUniv()) // 대학
                .category(workPost.getCategory()) //카테고리
                .pay(workPost.getPay()) // 페이
                .suggest(workPost.isSuggest()) //가격제안여부
                .employment(workPost.isEmployment()) //고용자 or 피고용자
                .build(); // 사진
        work.setUser(user); // user 연결 및, user의 외주 글 리스트에 글 추가
        workRepository.save(work);
        return PropertyUtil.response(true);
    }

    @Transactional
    public JSONObject wish(SessionUser tempUser, @RequestBody ActionForm form){   // 좋아요
        JSONObject obj = new JSONObject();
        User user = userRepository.findByEmailFetchWW(tempUser.getEmail()).orElseThrow(); // 외주 찜까지 페치 조인
        List<WorkWish> wishList = user.getWorkWishList(); //wishList == 유저의 외주 찜 리스트
        boolean isWish=false;
        Optional<Work> Work = workRepository.findById(form.getId());
        if(Work.isPresent()){
            Work work = Work.get();
            if(wishList.size()!=0){ /** 유저가 찜이 누른 적이 있다면 이미 누른 작품인지 비교 **/
                for (WorkWish wish : wishList) { //유저의 찜목록과 현재 누른 작품의 찜과 비교
                    if(work.equals(wish.getWork())) {  //같으면 이미 찜 누른 항목
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
                for (WorkWish wish : wishList) { //유저의 찜목록과 현재 누른 작품의 찜과 비교
                    if(work.equals(wish.getWork())) {  //같으면 이미 찜 누른 항목
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
        return PropertyUtil.responseMessage("존재하지 않는 외주 글에 요청된 찜");
    }


}

