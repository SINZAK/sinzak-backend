package net.sinzak.server.cert;

import com.univcert.api.UnivCert;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import net.sinzak.server.common.PropertyUtil;
import net.sinzak.server.common.resource.ApiDocumentResponse;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.user.dto.request.UnivDto;
import org.json.simple.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Api(tags = "인증")
@RestController
@RequiredArgsConstructor
public class CertController {
    private final CertService certService;
    private final static String API_KEY ="df6ea145-4134-40a3-a298-764cd7d5d7bb";

    @ApiDocumentResponse
    @ApiOperation(value = "대학 메일 인증 시작", notes = "인증코드는 아예 생략하시고, univ_email, univName 주시면 됩니다  1000~9999의 인증번호 메일전송 예정 \n" +
            "success : true 로 올 경우 메일 발송된 것.")
    @PostMapping("/certify/mail/send")
    public JSONObject sendUnivCertMail(@RequestBody MailDto mailDto) throws IOException {
        boolean univ_check = false;
        Map<String, Object> check = UnivCert.check(mailDto.getUnivName());
        if((boolean) check.get("success"))
            univ_check = true;
        Map<String, Object> response = UnivCert.certify(API_KEY, mailDto.getUniv_email(), mailDto.getUnivName(), univ_check);

        return new JSONObject(response);
    }

    @ApiDocumentResponse
    @ApiOperation(value = "인증코드 입력", notes = "인증코드 필수, 1000~9999의 인증번호 양식준수 \n" +
            "success : true 면 끝이고 아니면 학생증 인증이나 나중에 하기 버튼 클릭 유도")
    @PostMapping("/certify/mail/receive")
    public JSONObject receiveUnivCertMail(@AuthenticationPrincipal User user, @RequestBody MailDto mailDto) throws IOException {
        PropertyUtil.checkHeader(user);
        Map<String, Object> response = UnivCert.certifyCode(API_KEY, mailDto.getUniv_email(), mailDto.getUnivName(), mailDto.getCode());
        boolean success = (boolean) response.get("success");
        if(success)
            certService.updateCertifiedUniv(user, mailDto);

        return new JSONObject(response);
    }


    @ApiDocumentResponse
    @ApiOperation(value = "대학교 학생증 인증", notes = "대학명만 보내주세요. 메일은 무시.\n" +
            "{\"success\":true, \"id\":3}\n해당 유저의 id를 전해드리니 이 /certify/{id}/univ 에 넘겨주세요)")
    @PostMapping(value = "/certify/univ", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public JSONObject certifyUniv(@AuthenticationPrincipal User user, @RequestBody UnivDto univDto) {
        PropertyUtil.checkHeader(user);
        return certService.certifyUniv(user, univDto);
    }

    @ApiDocumentResponse
    @ApiOperation(value = "대학교 학생증 사진 업로드")
    @PostMapping(value = "/certify/{id}/univ", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiImplicitParam(name = "multipartFile", dataType = "multipartFile",
            value = "파일 보내주시면 파일 s3서버에 저장 및, 해당 파일이 저장되어 있는 URL을 디비에 저장합니다")
    public JSONObject uploadUnivCard(@PathVariable("id") Long certId, @RequestPart MultipartFile multipartFile) {
        return certService.uploadUnivCard(certId, multipartFile);
    }

    @ApiDocumentResponse
    @ApiOperation(value = "인증작가 신청", notes = "대학인증 마친 상태에서 요청할 수 있도록 해주세요. 대학인증 여부는 /my-profile에서 받았던거 그대로 됩니다 \n" +
            "테스트 할 때는  my-profile에서 port_folio_url이 바뀌는 지 체킹 하면 됩니다. cert_celeb은 저희가 직접 확인하기 전까지 false")
    @PostMapping(value = "/certify/author")
    public JSONObject updateUser(@AuthenticationPrincipal User user, @RequestBody PortFolioDto dto) {
        return certService.applyCertifiedAuthor(user, dto.getPortFolio());
    }

}
