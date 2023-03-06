package net.sinzak.server.user.service;


import lombok.RequiredArgsConstructor;

import net.sinzak.server.chatroom.service.ChatRoomCommandService;
import net.sinzak.server.common.error.InstanceNotFoundException;
import net.sinzak.server.image.S3Service;
import net.sinzak.server.user.domain.Report;
import net.sinzak.server.user.domain.SearchHistory;
import net.sinzak.server.user.dto.request.CategoryDto;
import net.sinzak.server.user.dto.request.FcmDto;
import net.sinzak.server.user.dto.request.ReportDto;
import net.sinzak.server.user.dto.request.UpdateUserDto;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.common.error.UserNotFoundException;
import net.sinzak.server.user.repository.ReportRepository;

import net.sinzak.server.user.repository.SearchHistoryRepository;
import net.sinzak.server.user.repository.UserRepository;
import net.sinzak.server.common.PropertyUtil;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCommandService {
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final SearchHistoryRepository historyRepository;
    private final ChatRoomCommandService chatRoomCommandService;
    private final S3Service s3Service;

    public User saveTempUser(User user){
        return userRepository.save(user);
    }

    public JSONObject updateUser(UpdateUserDto dto, User loginUser){
        if(loginUser ==null){
            return PropertyUtil.responseMessage(UserNotFoundException.USER_NOT_LOGIN);
        }
        User user = userRepository.findById(loginUser.getId()).orElseThrow(UserNotFoundException::new);
        user.update(dto.getName(),dto.getIntroduction());
        return PropertyUtil.response(true);
    }

    public JSONObject updateUserImage(User loginUser, MultipartFile multipartFile){
        User findUser = userRepository.findById(loginUser.getId()).orElseThrow(UserNotFoundException::new);
        try{
            String url = s3Service.uploadImage(multipartFile);
            findUser.setPicture(url);
        }
        catch (Exception e){
            return PropertyUtil.responseMessage("이미지 저장 실패");
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("picture", findUser.getPicture());
        return PropertyUtil.response(jsonObject);
    }

    public JSONObject updateCategoryLike(User user, CategoryDto categoryDto){
        User findUser = userRepository.findById(user.getId()).orElseThrow(UserNotFoundException::new);
        findUser.updateCategoryLike(categoryDto.getCategoryLike());
        return PropertyUtil.response(true);
    }
    public JSONObject setToken(FcmDto fcmDto){
        User loginUser = userRepository.findById(fcmDto.getUserId()).orElseThrow(UserNotFoundException::new);
        loginUser.setFcmToken(fcmDto.getFcmToken());
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
            return PropertyUtil.responseMessage("본인한테는 언팔로우 불가능");
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

    public JSONObject checkNickName(String nickName){
        if(userRepository.findByNickName(nickName).isPresent())
            return PropertyUtil.responseMessage("이미 존재하는 닉네임입니다.");
        return PropertyUtil.response(true);
    }


    public JSONObject report(ReportDto dto, User User){
        Long opponentUserId = dto.getUserId();
        User loginUser = userRepository.findByIdFetchReportList(User.getId()).orElseThrow(UserNotFoundException::new);
        if(loginUser.getId().equals(opponentUserId))
            return PropertyUtil.responseMessage("본인을 신고할 수 없습니다.");
        if(checkReportHistory(opponentUserId, loginUser).isPresent())
            return PropertyUtil.responseMessage("이미 신고한 회원입니다.");
        User opponentUser = userRepository.findById(opponentUserId).orElseThrow(UserNotFoundException::new);
        chatRoomCommandService.makeChatRoomBlocked(loginUser,opponentUser);
        Report connect = Report.createConnect(loginUser, opponentUser);
        reportRepository.save(connect);
        return PropertyUtil.response(true);
    }

    public JSONObject reportCancel(ReportDto dto, User User){
        Long opponentUserId = dto.getUserId();
        User loginUser = userRepository.findByIdFetchReportList(User.getId()).orElseThrow(UserNotFoundException::new);
        if(loginUser.getId().equals(opponentUserId))
            return PropertyUtil.responseMessage("본인을 신고 취소 할 수 없습니다.");
        Report report = checkReportHistory(opponentUserId, loginUser).orElseThrow(InstanceNotFoundException::new);
        User opponentUser = userRepository.findById(opponentUserId).orElseThrow(UserNotFoundException::new);
        chatRoomCommandService.makeChatRoomBlocked(loginUser,opponentUser);
        reportRepository.delete(report);
        return PropertyUtil.response(true);
    }

    private Optional<Report> checkReportHistory(Long id, User loginUser) {
        for (Report report : loginUser.getReportList()) {
            if(report.getOpponentUser().getId().equals(id))
                return Optional.of(report);
        }
        return Optional.empty();
    }

    public JSONObject showReportList(User User){
        User loginUser = userRepository.findByIdFetchReportList(User.getId()).orElseThrow(UserNotFoundException::new);
        return PropertyUtil.response(loginUser.getReportList());
    }

    @Transactional
    public JSONObject deleteSearchHistory(Long id, User User){
        User user = historyRepository.findByEmailFetchHistoryList(User.getEmail()).orElseThrow(InstanceNotFoundException::new);
        for (SearchHistory history : user.getHistoryList()) {
            if(history.getId().equals(id))
                historyRepository.delete(history);
        }
        return PropertyUtil.response(true);
    }

    @Transactional
    public JSONObject deleteSearchHistory(User User){
        User user = historyRepository.findByEmailFetchHistoryList(User.getEmail()).orElseThrow(InstanceNotFoundException::new);
        historyRepository.deleteAll(user.getHistoryList());
        return PropertyUtil.response(true);
    }

    @Transactional(rollbackFor = Exception.class)
    public JSONObject resign(User user){
        try{
            userRepository.delete(user);
        }
        catch (Exception e){
            return PropertyUtil.responseMessage("오류로 인해 탈퇴되지 않았습니다.");
        }
        return PropertyUtil.response(true);
    }
}
