package net.sinzak.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sinzak.server.config.auth.dto.SessionUser;
import net.sinzak.server.domain.User;
import net.sinzak.server.domain.Work;
import net.sinzak.server.dto.WorkPostDto;
import net.sinzak.server.repository.UserRepository;
import net.sinzak.server.repository.WorkRepository;
import org.json.simple.JSONObject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class WorkService {
    private final UserRepository userRepository;
    private final WorkRepository workRepository;

    @Transactional
    public JSONObject makeWorkPost(SessionUser tempUser, WorkPostDto workPost){
        User user = userRepository.findByEmailFetchWP(tempUser.getEmail()).orElseThrow(); //해당 유저의 외주 글 리스트까지 fetch해서 가져오기.
                            /** 존재 하지 않는 유저면 NullPointer 에러 뜰거고, 핸들러가 처리 할 예정 **/
        Work work = Work.builder()
                .title(workPost.getTitle())  //제목
                .content(workPost.getContent()) //내용
                .userName(user.getNickName()) //닉네임
                .univ(user.getUniv()) // 대학
                .pay(workPost.getPay()) // 페이
                .suggest(workPost.isSuggest()) //가격제안여부
                .field(workPost.getField()) //외주분야
                .employment(workPost.isEmployment()) //고용자 or 피고용자
                .photo(workPost.getPhoto())
                .build(); // 사진
        work.setUser(user); // user 연결 및, user의 외주 글 리스트에 글 추가
        workRepository.save(work);
        return PropertyUtil.response(true);
    }


}

