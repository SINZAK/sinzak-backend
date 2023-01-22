package net.sinzak.server.user.service;


import lombok.RequiredArgsConstructor;

import net.sinzak.server.user.domain.Report;
import net.sinzak.server.user.dto.request.ReportDto;
import net.sinzak.server.user.dto.request.UpdateUserDto;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.common.error.UserNotFoundException;
import net.sinzak.server.user.repository.ReportRepository;

import net.sinzak.server.user.repository.UserRepository;
import net.sinzak.server.common.PropertyUtil;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCommandService {
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;


    public JSONObject updateUser(UpdateUserDto dto, User loginUser){
        if(loginUser ==null){
            return PropertyUtil.responseMessage(UserNotFoundException.USER_NOT_LOGIN);
        }
        User user = userRepository.findById(loginUser.getId()).orElseThrow(UserNotFoundException::new);
        user.update(dto.getName(),dto.getPicture(),dto.getIntroduction());
        return PropertyUtil.response(true);
    }


    public JSONObject follow(Long userId, User loginUser){
        User findUser = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        if(loginUser ==null){
            return PropertyUtil.responseMessage(UserNotFoundException.USER_NOT_LOGIN);
        }
        if(loginUser.getId().equals(findUser.getId())){
            return PropertyUtil.responseMessage("본인한테는 팔로우 불가능");
        }
        return addFollow(findUser,loginUser);
    }
    public JSONObject unFollow(Long userId,User loginUser){
        User findUser = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        if(loginUser ==null){
            return PropertyUtil.responseMessage(UserNotFoundException.USER_NOT_LOGIN);
        }
        if(loginUser.getId().equals(findUser.getId())){
            return PropertyUtil.responseMessage("본인한테는 팔로우 불가능");
        }
        return removeFollow(findUser,loginUser);
    }

    public JSONObject removeFollow(User findUser, User loginUser){
        User user = userRepository.findByIdFetchFollowingList(loginUser.getId()).orElseThrow(UserNotFoundException::new);
        user.getFollowingList().remove(findUser.getId());
        findUser.getFollowerList().remove(loginUser.getId());
        user.updateFollowNumber();
        findUser.updateFollowNumber();
        return PropertyUtil.response(true);
    }
    public JSONObject addFollow(User findUser, User loginUser){
        User user = userRepository.findByIdFetchFollowingList(loginUser.getId()).orElseThrow(UserNotFoundException::new);
        user.getFollowingList().add(findUser.getId());
        findUser.getFollowerList().add(loginUser.getId());
        user.updateFollowNumber();
        findUser.updateFollowNumber();
        return PropertyUtil.response(true);
    }

    public JSONObject report(ReportDto dto, User User){
        Long opponentUserId = dto.getUserId();
        User loginUser = userRepository.findByEmailFetchReportList(User.getEmail()).orElseThrow(UserNotFoundException::new);
        if(loginUser.getId().equals(opponentUserId))
            return PropertyUtil.responseMessage("본인을 신고할 수 없습니다.");
        if(checkAlreadyReport(opponentUserId, loginUser))
            return PropertyUtil.responseMessage("이미 신고한 회원입니다.");
        User opponentUser = userRepository.findById(opponentUserId).orElseThrow(UserNotFoundException::new);

        Report connect = Report.createConnect(loginUser, opponentUser);
        reportRepository.save(connect);
        return PropertyUtil.response(true);
    }



    private boolean checkAlreadyReport(Long id, User loginUser) {
        for (Report report : loginUser.getReportList()) {
            if(report.getOpponentUser().getId().equals(id))
                return true;
        }
        return false;
    }

}
