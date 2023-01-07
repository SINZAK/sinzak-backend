package net.sinzak.server.cert;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import net.sinzak.server.common.resource.ApiDocumentResponse;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.user.dto.request.UnivDto;
import org.json.simple.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = "인증")
@RestController
@RequiredArgsConstructor
public class CertController {

    private final CertService certService;

    @ApiOperation(value = "대학 메일 인증 시작", notes = "유저 토큰은 필요없고, address, univ만 주시면 됩니다  1000~9999의 인증번호 메일전송예정")
    @PostMapping("/certify/mail/send")
    public JSONObject sendMail(@RequestBody MailDto mailDto) {
        return certService.sendMail(mailDto);
    }

    @ApiOperation(value = "인증코드 확인", notes = "\"success\" : false 를 받았다면 학생증 인증도 있다는 걸 안내해야됩니다.\n ")
    @PostMapping("/certify/mail/receive")
    public JSONObject receiveMail(@AuthenticationPrincipal User user, @RequestBody MailDto mailDto) {
        return certService.receiveMail(user, mailDto);
    }

    @ApiDocumentResponse
    @ApiOperation(value = "대학교 학생증 인증")
    @PostMapping(value = "/certify/univ", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "univDto", dataType = "json", value = "{\n" +
                    "\"univ\": \"대학명\",\n" +
                    "\"univ_email\": \"대학 메일\"\n" +
                    "}\n" +
                    "주의사항 : Content-Type = application/json"),
            @ApiImplicitParam(name = "multipartFile", dataType = "multipartFile",
                    value = "파일 보내주시면 파일 s3서버에 저장 및, 해당 파일이 저장되어 있는 URL을 디비에 저장합니다")
    })
    public JSONObject certifyUniv(@AuthenticationPrincipal User user, @RequestPart UnivDto univDto, @RequestPart MultipartFile multipartFile) {
        return certService.certifyUniv(user, univDto, multipartFile);
    }
}
