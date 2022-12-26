package net.sinzak.server.config.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sinzak.server.config.auth.jwt.*;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public TokenDto login(@RequestBody Map<String, String> User) {

        User user = userRepository.findByEmail(User.get("email"))
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 ID 입니다."));
        TokenDto tokenDto = jwtProvider.createToken(user.getUsername(), user.getRoleKey());
        //리프레시 토큰 저장
        RefreshToken refreshToken = RefreshToken.builder()
                .key(user.getId())
                .token(tokenDto.getRefreshToken())
                .build();
        log.warn("access token = "+tokenDto.getAccessToken());
        refreshTokenRepository.save(refreshToken);
        return tokenDto;
    }

    @Transactional
    public TokenDto reissue(TokenRequestDto tokenRequestDto) {
        // 만료된 refresh token 에러
        if (!jwtProvider.validateToken(tokenRequestDto.getRefreshToken())) {
            throw new NoSuchElementException();
        }

        // AccessToken 에서 Username (pk) 가져오기
        String accessToken = tokenRequestDto.getAccessToken();
        Authentication authentication = jwtProvider.getAuthentication(accessToken);

        // user pk로 유저 검색 / repo 에 저장된 Refresh Token 이 없음
        User user = userRepository.findById(Long.parseLong(authentication.getName()))
                .orElseThrow(NullPointerException::new);
        RefreshToken refreshToken = refreshTokenRepository.findByKey(user.getId())
                .orElseThrow(NullPointerException::new);

        // 리프레시 토큰 불일치 에러
        if (!refreshToken.getToken().equals(tokenRequestDto.getRefreshToken()))
            throw new NoSuchElementException();

        // AccessToken, RefreshToken 토큰 재발급, 리프레쉬 토큰 저장
        TokenDto newCreatedToken = jwtProvider.createToken(user.getUsername(), user.getRoleKey());
        RefreshToken updateRefreshToken = refreshToken.updateToken(newCreatedToken.getRefreshToken());
        refreshTokenRepository.save(updateRefreshToken);

        return newCreatedToken;
    }
}
