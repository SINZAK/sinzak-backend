package net.sinzak.server.banner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sinzak.server.common.SinzakResponse;
import net.sinzak.server.common.error.InstanceNotFoundException;
import net.sinzak.server.image.S3Service;
import net.sinzak.server.user.service.UserQueryService;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class BannerService {
    private final UserQueryService userQueryService;
    private final BannerRepository bannerRepository;
    private final S3Service s3Service;

    @Transactional(rollbackFor = {Exception.class})
    public JSONObject make(BannerDto dto) {   // 배너 생성
        Banner banner = Banner.builder()
                .pcImageUrl("")
                .imageUrl("")
                .content(dto.getContent())
                .build();
        Long id = bannerRepository.save(banner)
                .getId();
        return SinzakResponse.success(id);
    }

    @Transactional(rollbackFor = {Exception.class})
    public JSONObject saveImage(Long id, MultipartFile file) {   // 글 생성
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(InstanceNotFoundException::new);
        uploadImageAndSetThumbnail(banner, file);
        return SinzakResponse.success(true);
    }

    private String uploadImageAndSetThumbnail(Banner banner, MultipartFile file) {
        String url = s3Service.uploadImage(file);
        banner.setImageUrl(url);
        return url;
    }

    @Transactional(readOnly = true)
    public JSONObject getList() {
        return SinzakResponse.success(bannerRepository.findAll());
    }

    @Transactional
    public JSONObject pick(Long id) {
        List<Banner> banners = bannerRepository.findAuthorBanner();
        userQueryService.getUserNickName(id)
                .ifPresent(name -> banners.forEach((banner) -> banner.setUserInfo(id, name)));
        return SinzakResponse.success(true);
    }

    @Transactional
    public void randomPick() throws NoSuchAlgorithmException {
        List<Banner> banners = bannerRepository.findAuthorBanner();
        userQueryService.getCertifiedRandomUser()
                .ifPresent(user -> banners.forEach((banner) -> banner.setUserInfo(user.getId(), user.getNickName())));
        log.warn("{} 떠오르는 작가 재설정", LocalDateTime.now());
    }
}
