package net.sinzak.server.user.service;


import lombok.RequiredArgsConstructor;

import net.sinzak.server.alarm.domain.AlarmType;
import net.sinzak.server.alarm.service.AlarmService;
import net.sinzak.server.chatroom.service.ChatRoomCommandService;
import net.sinzak.server.common.UserUtils;
import net.sinzak.server.common.error.InstanceNotFoundException;
import net.sinzak.server.firebase.FireBaseService;
import net.sinzak.server.image.S3Service;
import net.sinzak.server.user.domain.Report;
import net.sinzak.server.user.dto.request.CategoryDto;
import net.sinzak.server.user.dto.request.FcmDto;
import net.sinzak.server.user.dto.request.ReportRequestDto;
import net.sinzak.server.user.dto.request.UpdateUserDto;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.common.error.UserNotFoundException;
import net.sinzak.server.user.repository.ReportRepository;

import net.sinzak.server.user.repository.SearchHistoryRepository;
import net.sinzak.server.user.repository.UserRepository;
import net.sinzak.server.common.PropertyUtil;
import org.json.simple.JSONObject;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserCommandService {
    private final UserUtils userUtils;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final SearchHistoryRepository historyRepository;
    private final ChatRoomCommandService chatRoomCommandService;
    private final S3Service s3Service;
    private final FireBaseService fireBaseService;
    private final AlarmService alarmService;

    public User saveTempUser(User user){
        return userRepository.save(user);
    }

    public JSONObject updateUser(UpdateUserDto dto){
        User user = userUtils.getCurrentUser();
        user.updateProfile(dto.getName(),dto.getIntroduction());
        return PropertyUtil.response(true);
    }

    public JSONObject updateUserImage(MultipartFile multipartFile){
        User loginUser = userUtils.getCurrentUser();
        try{
            String url = s3Service.uploadImage(multipartFile);
            loginUser.setPicture(url);
            userRepository.save(loginUser);
        }
        catch (Exception e){
            return PropertyUtil.responseMessage("이미지 저장 실패");
        }
        JSONObject obj = new JSONObject();
        obj.put("picture", loginUser.getPicture());
        return PropertyUtil.response(obj);
    }


    public JSONObject updateCategoryLike(CategoryDto categoryDto){
        User user = userUtils.getCurrentUser();
        user.updateCategoryLike(categoryDto.getCategoryLike());
        return PropertyUtil.response(true);
    }

    public JSONObject setToken(FcmDto fcmDto){
        User loginUser = userRepository.findByIdNotDeleted(fcmDto.getUserId()).orElseThrow(UserNotFoundException::new);
        loginUser.setFcm(fcmDto.getFcmToken());
        return PropertyUtil.response(true);
    }


    @CacheEvict(value = {"home_user"}, key = "#currentUserId", cacheManager = "testCacheManager")
    public JSONObject follow(Long currentUserId, Long userId){
        User findUser = userRepository.findByIdNotDeleted(userId).orElseThrow(UserNotFoundException::new);
        if(currentUserId.equals(findUser.getId()))
            return PropertyUtil.responseMessage("본인한테는 팔로우 불가능");
        alarmService.makeAlarm(userUtils.getCurrentUser(),findUser.getPicture(),findUser.getId().toString(), AlarmType.FOLLOW, findUser.getNickName());
        return addFollow(findUser, currentUserId);
    }

    @CacheEvict(value = {"home_user"}, key = "#currentUserId", cacheManager = "testCacheManager")
    public JSONObject unFollow(Long currentUserId, Long userId){
        User findUser = userRepository.findByIdNotDeleted(userId).orElseThrow(UserNotFoundException::new);
        if(currentUserId.equals(findUser.getId()))
            return PropertyUtil.responseMessage("본인한테는 언팔로우 불가능");

        return removeFollow(findUser, currentUserId);
    }

    public JSONObject removeFollow(User findUser, Long loginUserId){
        User user = userRepository.findByIdFetchFollowingList(loginUserId).orElseThrow(UserNotFoundException::new);
        user.getFollowingList().remove(findUser.getId());
        findUser.getFollowerList().remove(loginUserId);
        user.updateFollowNumber();
        findUser.updateFollowNumber();
        return PropertyUtil.response(true);
    }

    public JSONObject addFollow(User findUser, Long loginUserId){
        User user = userRepository.findByIdFetchFollowingList(loginUserId).orElseThrow(UserNotFoundException::new);
        user.getFollowingList().add(findUser.getId());
        findUser.getFollowerList().add(loginUserId);
        fireBaseService.sendIndividualNotification(findUser,"팔로우 알림",findUser.getNickName(),findUser.getId().toString());

        user.updateFollowNumber();
        findUser.updateFollowNumber();
        return PropertyUtil.response(true);
    }

    @Transactional(readOnly = true)
    public JSONObject checkNickName(String nickName){
        if(userRepository.findByNickName(nickName).isPresent())
            return PropertyUtil.responseMessage("이미 존재하는 닉네임입니다.");
        return PropertyUtil.response(true);
    }


    public JSONObject report(ReportRequestDto dto){
        Long opponentUserId = dto.getUserId();
        User loginUser = userUtils.getCurrentUser();
        if(loginUser.getId().equals(opponentUserId))
            return PropertyUtil.responseMessage("본인을 신고할 수 없습니다.");
        if(checkReportHistory(opponentUserId, loginUser).isPresent())
            return PropertyUtil.responseMessage("이미 신고한 회원입니다.");
        User opponentUser = userRepository.findByIdNotDeleted(opponentUserId).orElseThrow(UserNotFoundException::new);
        chatRoomCommandService.makeChatRoomBlocked(loginUser,opponentUser,true);
        Report connect = Report.createConnect(loginUser, opponentUser);
        reportRepository.save(connect);
        return PropertyUtil.response(true);
    }

    public JSONObject reportCancel(ReportRequestDto dto){
        Long opponentUserId = dto.getUserId();
        User loginUser = userUtils.getCurrentUser();
        if(loginUser.getId().equals(opponentUserId))
            return PropertyUtil.responseMessage("본인을 신고 취소 할 수 없습니다.");
        Report report = checkReportHistory(opponentUserId, loginUser).orElseThrow(InstanceNotFoundException::new);
        User opponentUser = userRepository.findByIdNotDeleted(opponentUserId).orElseThrow(UserNotFoundException::new);
        chatRoomCommandService.makeChatRoomBlocked(loginUser,opponentUser,false);
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



    public JSONObject deleteSearchHistory(Long id){
        User user = userUtils.getCurrentUser();
        user.getHistoryList().stream()
                .filter(history -> history.getId().equals(id))
                .findFirst()
                .ifPresent(historyRepository::delete);
        return PropertyUtil.response(true);
    }

    public JSONObject deleteSearchHistory(){
        User user = userUtils.getCurrentUser();
        historyRepository.deleteAll(user.getHistoryList());
        return PropertyUtil.response(true);
    }

    @Transactional(rollbackFor = Exception.class)
    public JSONObject resign(){
        try{
            User loginUser = userUtils.getCurrentUser();
            loginUser.setDelete(true);
            return PropertyUtil.response(true);
        }
        catch (Exception e){
            return PropertyUtil.responseMessage("탈퇴 처리가 되지 않았습니다. sinzakofficial@gmail.com 으로 문의주세요.");
        }

    }
}
