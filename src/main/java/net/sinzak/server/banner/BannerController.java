package net.sinzak.server.banner;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import net.sinzak.server.common.dto.IdDto;
import net.sinzak.server.common.resource.ApiDocumentResponse;
import org.json.simple.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.NoSuchAlgorithmException;


@Api(tags = "배너")
@RestController
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    @ApiDocumentResponse
    @ApiOperation(value = "배너 생성",notes = "{\"success\":true, \"id\":2}\n해당 글의 id를 전해드리니 이 /banner/{id}/image 에 넘겨주세요\n")
    @PostMapping(value = "/banner/build", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public JSONObject makeProductPost(@RequestBody BannerDto buildDto) {
        return bannerService.make(buildDto);
    }

    @ApiDocumentResponse
    @ApiOperation(value = "배너 이미지 등록")
    @PostMapping(value = "/banner/{id}/image", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiImplicitParam(name = "multipartFile", dataType = "multipartFile",
            value = "파일 보내주시면 파일 s3서버에 저장 및, 해당 파일이 저장되어 있는 URL을 디비에 저장합니다")
    public JSONObject makeProductPost(@PathVariable("id") Long bannerId, @RequestPart MultipartFile multipartFile) {
        return bannerService.saveImage(bannerId, multipartFile);
    }

    @ApiDocumentResponse
    @ApiOperation(value = "배너 정보 출력", notes = "떠오르는 작가 배너는 user_id까지 같이 드립니다 해당 유저 프로필로 이동할 수 있게 프로필 링크해주세요")
    @GetMapping(value = "/banner")
    public JSONObject showBannerList() {
        return bannerService.getList();
    }

    @ApiDocumentResponse
    @ApiOperation(value = "떠오르는 작가 임의 설정",notes = "이거는 추후에 수익창출을 위해 필요할 듯 프론트단은 신경 안쓰셔도됩니다")
    @PostMapping(value = "/banner/pick", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public JSONObject pickUser(@RequestBody IdDto idDto) {
        return bannerService.pick(idDto.getId());
    }

    @Scheduled(fixedDelay = 21600000, initialDelay = 100000) /** 6시간 **/
    @ApiOperation(value = "무시", hidden = true)
    @PostMapping("/banner/reset")
    public void resetBanner() throws NoSuchAlgorithmException {
        bannerService.randomPick();
    }
}
