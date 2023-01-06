package net.sinzak.server.config.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sinzak.server.common.PropertyUtil;
import net.sinzak.server.common.error.ErrorResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

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

    @Transactional
    public TokenDto login(EmailDto dto) {

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UserNotFoundException("가입되지 않은 ID 입니다."));
        TokenDto tokenDto = jwtProvider.createToken(user.getEmail(), user.getRoles());
        //리프레시 토큰 저장
        RefreshToken refreshToken = RefreshToken.builder()
                .key(user.getId())
                .token(tokenDto.getRefreshToken())
                .build();
        log.warn("access token = "+tokenDto.getAccessToken());
        refreshTokenRepository.save(refreshToken);
        return tokenDto;
    }

    private final JoinTermsRepository joinTermsRepository;

    @Transactional(rollbackFor = Exception.class)
    public JSONObject join(@RequestBody JoinDto dto) {
        JSONObject obj = new JSONObject();
        Optional<User> existUser = userRepository.findByEmail(dto.getEmail());
        if(existUser.isPresent())
            return PropertyUtil.responseMessage("이미 가입된 이메일입니다.");
        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .univ_email(dto.getUniv_email())
                .origin(dto.getOrigin())
                .categoryLike(dto.getCategory_like())
                .nickName(dto.getNickName())
                .univ(dto.getUniv())
                .cert_uni(dto.isCert_univ()).build();
        if(dto.getOrigin().equals("apple"))
            user.updateEmail(dto.getTokenId());  /** 애플로그인은 이메일을 토큰 ID로써야함  --> 왜냐면 이후에 애플 로그인시 프론트에서 이메일 못받아옴 **/
        User savedUser = userRepository.save(user);
        JoinTerms terms = new JoinTerms(dto.isTerm());
        terms.setUser(savedUser);
        JoinTerms saveTerms = joinTermsRepository.save(terms);
        if(savedUser.getId() == null || saveTerms.getId() == null)
            throw new InstanceNotFoundException("서버 오류로 저장되지 않았습니다.");
        TokenDto tokenDto = jwtProvider.createToken(user.getUsername(), user.getRoles());
        //리프레시 토큰 저장
        RefreshToken refreshToken = RefreshToken.builder()
                .key(user.getId())
                .token(tokenDto.getRefreshToken())
                .build();
        log.warn("회원가입 완료 access token = "+tokenDto.getAccessToken());
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
        TokenDto newCreatedToken = jwtProvider.createToken(User.getEmail(), User.getRoles());
        RefreshToken updateRefreshToken = refreshToken.updateToken(newCreatedToken.getRefreshToken());
        refreshTokenRepository.save(updateRefreshToken);

        return newCreatedToken;
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected ErrorResponse handleException1() {
        return ErrorResponse.of(HttpStatus.BAD_REQUEST, "유효하지 않은 토큰입니다.");
    }

    @Transactional(readOnly = true)
    public JSONObject checkEmail(EmailDto dto) {
        Optional<User> existUser = userRepository.findByEmail(dto.getEmail());
        if (existUser.isPresent())
            return PropertyUtil.responseMessage("이미 가입된 이메일입니다.");
        return PropertyUtil.response(true);
    }
}
