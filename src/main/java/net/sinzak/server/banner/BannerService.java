package net.sinzak.server.banner;

import lombok.RequiredArgsConstructor;
import net.sinzak.server.common.PropertyUtil;
import net.sinzak.server.common.error.InstanceNotFoundException;
import net.sinzak.server.image.S3Service;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
public class BannerService {
    private final BannerRepository bannerRepository;
    private final S3Service s3Service;
    @Transactional(rollbackFor = {Exception.class})
    public JSONObject make(BannerDto dto){   // 글 생성
        Banner banner = Banner.builder()
                        .title(dto.getTitle())
                        .content(dto.getContent()).build();
        Long id = bannerRepository.save(banner).getId();
        return PropertyUtil.response(id);
    }

    @Transactional(rollbackFor = {Exception.class})
    public JSONObject saveImage(Long id, MultipartFile file){   // 글 생성
        Banner banner = bannerRepository.findById(id).orElseThrow(InstanceNotFoundException::new);
        uploadImageAndSetThumbnail(banner, file);
        return PropertyUtil.response(id);
    }

    private String uploadImageAndSetThumbnail(Banner banner, MultipartFile file) {
        String url = s3Service.uploadImage(file);
        banner.setImageUrl(url);
        return url;
    }
}
