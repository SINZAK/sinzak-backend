package net.sinzak.server.config.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sinzak.server.common.SinzakResponse;
import net.sinzak.server.common.UserUtils;
import net.sinzak.server.common.error.InstanceNotFoundException;
import net.sinzak.server.common.error.UserNotFoundException;
import net.sinzak.server.config.auth.jwt.JwtTokenProvider;
import net.sinzak.server.config.auth.jwt.RefreshToken;
import net.sinzak.server.config.auth.jwt.RefreshTokenRepository;
import net.sinzak.server.config.auth.jwt.TokenDto;
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

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityService {
    private final UserUtils userUtils;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JoinTermsRepository joinTermsRepository;

    @Transactional
    public TokenDto login(String email) {
        User user = userRepository.findByEmailNotDeleted(email)
                .orElseThrow(() -> new UserNotFoundException(UserNotFoundException.USER_NOT_FOUND));
        TokenDto tokenDto = jwtProvider.createToken(user.getId().toString(), user.getId(), user.getRole());

        if (user.getNickName() == null || user.getNickName().isEmpty())
            tokenDto.setIsJoined(false);
        tokenDto.setOrigin(user.getOrigin());
        RefreshToken refreshToken = RefreshToken.builder()
                .key(user.getId())
                .token(tokenDto.getRefreshToken())
                .build();
        refreshTokenRepository.save(refreshToken);
        log.error(user.getNickName() + " 로그인!");
        return tokenDto;
    }

    @Transactional
    public TokenDto login(User user) {
        SinzakResponse.checkHeader(user);
        TokenDto tokenDto = jwtProvider.createToken(user.getId().toString(), user.getId(), user.getRole());

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
    public JSONObject join(@RequestBody JoinDto dto) {
        User user = userUtils.getCurrentUser();
        if (!user.getNickName().isBlank())
            return SinzakResponse.error("이미 회원가입된 유저입니다.");
        JSONObject obj = new JSONObject();
        user.saveJoinInfo(dto.getNickName(), dto.getCategory_like());
        user.setRandomProfileImage();
        JoinTerms terms = new JoinTerms(dto.isTerm());
        terms.setUser(user);
        JoinTerms saveTerms = joinTermsRepository.save(terms);
        Long savedId = userRepository.save(user).getId();
        if (savedId == null || saveTerms.getId() == null)
            throw new InstanceNotFoundException("서버 오류로 저장되지 않았습니다.");
        TokenDto tokenDto = jwtProvider.createToken(String.valueOf(user.getId()), user.getId(), user.getRole());
        tokenDto.setIsJoined(true);
        tokenDto.setOrigin(user.getOrigin());
        RefreshToken refreshToken = RefreshToken.builder()
                .key(user.getId())
                .token(tokenDto.getRefreshToken())
                .build();
        refreshTokenRepository.save(refreshToken);
        obj.put("token", tokenDto);
        obj.put("success", true);
        return obj;
    }

    @Transactional
    public TokenDto reissue() {
        User user = userUtils.getCurrentUser();
        List<RefreshToken> refreshTokens = refreshTokenRepository.findByKey(user.getId());
        RefreshToken refreshToken = refreshTokens.get(refreshTokens.size() - 1); //마지막꺼가 가장 최신반영된 토큰

        TokenDto newCreatedToken = jwtProvider.createToken(user.getId().toString(), user.getId(), user.getRole());
        RefreshToken updateRefreshToken = refreshToken.updateToken(newCreatedToken.getRefreshToken());
        refreshTokenRepository.save(updateRefreshToken);

        return newCreatedToken;
    }


    @Transactional(readOnly = true)
    public JSONObject checkEmail(EmailDto dto) {
        Optional<User> existUser = userRepository.findByEmailNotDeleted(dto.getEmail());
        if (existUser.isPresent()) {
            User user = existUser.get();
            if (!user.getNickName().isBlank())
                return SinzakResponse.error("이미 가입된 이메일입니다.");
        }
        return SinzakResponse.success(true);
    }
}
