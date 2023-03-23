package net.sinzak.server.config.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sinzak.server.common.PropertyUtil;
import net.sinzak.server.common.error.InstanceNotFoundException;
import net.sinzak.server.common.error.UserNotFoundException;
import net.sinzak.server.config.auth.jwt.*;
import net.sinzak.server.user.domain.JoinTerms;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.user.dto.request.EmailDto;
import net.sinzak.server.user.dto.request.JoinDto;
import net.sinzak.server.user.repository.JoinTermsRepository;
import net.sinzak.server.user.repository.UserRepository;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JoinTermsRepository joinTermsRepository;

    @Transactional
    public TokenDto login(String email) {
        User user = userRepository.findByEmailNotDeleted(email)
                .orElseThrow(() -> new UserNotFoundException(UserNotFoundException.USER_NOT_FOUND));
        TokenDto tokenDto = jwtProvider.createToken(user.getId().toString(), user.getId(), user.getRoles());
        //리프레시 토큰 저장
        log.error(user.getNickName());
        if(user.getNickName().isEmpty())
            tokenDto.setIsJoined(false);
        tokenDto.setOrigin(user.getOrigin());
        RefreshToken refreshToken = RefreshToken.builder()
                .key(user.getId())
                .token(tokenDto.getRefreshToken())
                .build();
        refreshTokenRepository.save(refreshToken);
        return tokenDto;
    }

    @Transactional
    public TokenDto login(User user) {
        PropertyUtil.checkHeader(user);
        TokenDto tokenDto = jwtProvider.createToken(user.getId().toString(), user.getId(), user.getRoles());
        //리프레시 토큰 저장
        tokenDto.setIsJoined(false);
        tokenDto.setOrigin(user.getOrigin());
        RefreshToken refreshToken = RefreshToken.builder()
                .key(user.getId())
                .token(tokenDto.getRefreshToken())
                .build();
        refreshTokenRepository.save(refreshToken);
        return tokenDto;
    }



    @Transactional(rollbackFor = Exception.class)
    public JSONObject join(User User, @Valid @RequestBody JoinDto dto) {
        if(!User.getNickName().isBlank())
            return PropertyUtil.responseMessage("이미 회원가입된 유저입니다.");
        JSONObject obj = new JSONObject();
        User user = userRepository.findByEmailNotDeleted(User.getEmail()).orElseThrow(UserNotFoundException::new);
        user.saveJoinInfo(dto.getNickName(), dto.getCategory_like());
        user.setRandomProfileImage();
        JoinTerms terms = new JoinTerms(dto.isTerm());
        terms.setUser(user);
        JoinTerms saveTerms = joinTermsRepository.save(terms);
        if(user.getId() == null || saveTerms.getId() == null)
            throw new InstanceNotFoundException("서버 오류로 저장되지 않았습니다.");
        TokenDto tokenDto = jwtProvider.createToken(user.getUsername(), user.getId(), user.getRoles());
        tokenDto.setIsJoined(true);
        tokenDto.setOrigin(user.getOrigin());
        RefreshToken refreshToken = RefreshToken.builder()
                .key(user.getId())
                .token(tokenDto.getRefreshToken())
                .build();
        refreshTokenRepository.save(refreshToken);
        obj.put("token",tokenDto);
        obj.put("success",true);
        return obj;
    }

    @Transactional
    public TokenDto reissue(User User) {
        List<RefreshToken> refreshTokens = refreshTokenRepository.findByKey(User.getId());
        RefreshToken refreshToken = refreshTokens.get(refreshTokens.size()-1); //마지막꺼가 가장 최신반영된 토큰

        // AccessToken, RefreshToken 토큰 재발급, 리프레쉬 토큰 저장
        TokenDto newCreatedToken = jwtProvider.createToken(User.getId().toString(), User.getId(), User.getRoles());
        RefreshToken updateRefreshToken = refreshToken.updateToken(newCreatedToken.getRefreshToken());
        refreshTokenRepository.save(updateRefreshToken);

        return newCreatedToken;
    }


    @Transactional(readOnly = true)
    public JSONObject checkEmail(EmailDto dto) {
        Optional<User> existUser = userRepository.findByEmailNotDeleted(dto.getEmail());
        if (existUser.isPresent()){
            User user = existUser.get();
            if(!user.getNickName().isBlank())
                return PropertyUtil.responseMessage("이미 가입된 이메일입니다.");
        }
        return PropertyUtil.response(true);
    }
}
