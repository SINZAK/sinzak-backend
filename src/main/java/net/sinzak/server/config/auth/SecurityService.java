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
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.NoSuchElementException;
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
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("가입되지 않은 ID 입니다."));
        TokenDto tokenDto = jwtProvider.createToken(user.getEmail(), user.getId(), user.getRoles());
        //리프레시 토큰 저장
        if(user.getNickName().isEmpty())
            tokenDto.setIsJoined(false);

        RefreshToken refreshToken = RefreshToken.builder()
                .key(user.getId())
                .token(tokenDto.getRefreshToken())
                .build();
        refreshTokenRepository.save(refreshToken);
        return tokenDto;
    }

    @Transactional
    public TokenDto login(User user) {
        TokenDto tokenDto = jwtProvider.createToken(user.getEmail(), user.getId(), user.getRoles());
        //리프레시 토큰 저장
        RefreshToken refreshToken = RefreshToken.builder()
                .key(user.getId())
                .token(tokenDto.getRefreshToken())
                .build();
        refreshTokenRepository.save(refreshToken);
        return tokenDto;
    }



    @Transactional(rollbackFor = Exception.class)
    public JSONObject join(User User, @RequestBody JoinDto dto) {
        if(!User.getNickName().isBlank())
            return PropertyUtil.response("이미 회원가입된 유저입니다.");
        JSONObject obj = new JSONObject();
        User user = userRepository.findByEmail(User.getEmail()).orElseThrow(UserNotFoundException::new);
        user.saveJoinInfo(dto.getNickName(), dto.getCategory_like());
//        if(user.getOrigin().equals("apple"))
//            user.updateEmailForAppleUser();  /** 애플로그인은 이메일을 토큰 ID로써야함  --> 왜냐면 이후에 애플 로그인시 프론트에서 이메일 못받아옴 **/
        JoinTerms terms = new JoinTerms(dto.isTerm());
        terms.setUser(user);
        JoinTerms saveTerms = joinTermsRepository.save(terms);
        if(user.getId() == null || saveTerms.getId() == null)
            throw new InstanceNotFoundException("서버 오류로 저장되지 않았습니다.");
        TokenDto tokenDto = jwtProvider.createToken(user.getUsername(), user.getId(), user.getRoles());
        tokenDto.setIsJoined(true);

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
    public TokenDto reissue(User User,TokenRequestDto tokenRequestDto) {
        // 만료된 refresh token 에러
        if (!jwtProvider.validateToken(tokenRequestDto.getRefreshToken())) {
            throw new NoSuchElementException();
        }

        // AccessToken 에서 Username (pk) 가져오기
        String accessToken = tokenRequestDto.getAccessToken();
        Authentication authentication = jwtProvider.getAuthentication(accessToken);

        // user pk로 유저 검색 / repo 에 저장된 Refresh Token 이 없음

        if(!authentication.getName().equals(User.getEmail()))
            throw new NoSuchElementException();
        List<RefreshToken> refreshTokens = refreshTokenRepository.findByKey(User.getId());
        RefreshToken refreshToken = refreshTokens.get(refreshTokens.size()-1); //마지막꺼가 가장 최신반영된 토큰
        // 리프레시 토큰 불일치 에러
        if (!refreshToken.getToken().equals(tokenRequestDto.getRefreshToken()))
            throw new NoSuchElementException();

        // AccessToken, RefreshToken 토큰 재발급, 리프레쉬 토큰 저장
        TokenDto newCreatedToken = jwtProvider.createToken(User.getEmail(), User.getId(), User.getRoles());
        RefreshToken updateRefreshToken = refreshToken.updateToken(newCreatedToken.getRefreshToken());
        refreshTokenRepository.save(updateRefreshToken);

        return newCreatedToken;
    }


    @Transactional(readOnly = true)
    public JSONObject checkEmail(EmailDto dto) {
        Optional<User> existUser = userRepository.findByEmail(dto.getEmail());
        if (existUser.isPresent()){
            User user = existUser.get();
            if(!user.getNickName().isBlank())
                return PropertyUtil.responseMessage("이미 가입된 이메일입니다.");
        }
        return PropertyUtil.response(true);
    }
}
