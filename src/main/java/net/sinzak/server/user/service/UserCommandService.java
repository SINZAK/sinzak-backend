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
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;
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
        User user = userRepository.findById(loginUser.getId()).get();
        user.update(dto.getName(),dto.getPicture(),dto.getIntroduction());
        return PropertyUtil.response(true);
    }


    public JSONObject follow(Long userId, User loginUser){
        Optional<User> findUser = userRepository.findById(userId);
        JSONObject userNotExist = checkUsersExist(findUser,loginUser);
        System.out.println((boolean)userNotExist.get(PropertyUtil.SUCCESS_WORD));
        if(!(boolean)userNotExist.get(PropertyUtil.SUCCESS_WORD)){
            return userNotExist;
        }
        User user = userRepository.findById(loginUser.getId()).get();
        user.getFollowingList().add(userId);
        findUser.get().getFollowerList().add(user.getId());
        return PropertyUtil.response(true);
    }
    public JSONObject unFollow(Long userId,User loginUser){
        Optional<User> findUser = userRepository.findById(userId);
        JSONObject userNotExist = checkUsersExist(findUser,loginUser);
        if(!(boolean)userNotExist.get(PropertyUtil.SUCCESS_WORD)){
            return userNotExist;
        }
        User user = userRepository.findById(loginUser.getId()).get();
        user.getFollowingList().remove(userId);
        findUser.get().getFollowerList().remove(user.getId());
        return PropertyUtil.response(true);
    }

    @Transactional(readOnly = true)
    public JSONObject checkUsersExist(Optional<User> findUser,User user){
        if(user ==null){
            return PropertyUtil.responseMessage(UserNotFoundException.USER_NOT_LOGIN);
        }
        if(!findUser.isPresent()){
            return PropertyUtil.responseMessage(UserNotFoundException.USER_NOT_FOUND);
        }
        if(user.getId().equals(findUser.get().getId())){
            return PropertyUtil.responseMessage("본인한테는 팔로우 불가능");
        }
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
