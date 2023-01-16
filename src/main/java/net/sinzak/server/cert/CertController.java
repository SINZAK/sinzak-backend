package net.sinzak.server.cert;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import net.sinzak.server.common.PropertyUtil;
import net.sinzak.server.common.error.InstanceNotFoundException;
import net.sinzak.server.common.error.UserNotFoundException;
import net.sinzak.server.common.resource.ApiDocumentResponse;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.user.dto.request.UnivDto;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
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
    @ApiOperation(value = "대학교 학생증 인증",notes = "{\"success\":true, \"id\":3}\n해당 유저의 id를 전해드리니 이 /certify/{id}/univ 에 넘겨주세요)")
    @PostMapping(value = "/certify/univ", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public JSONObject certifyUniv(@AuthenticationPrincipal User user, @RequestBody UnivDto univDto) {
        return certService.certifyUniv(user, univDto);
    }

    @ApiDocumentResponse
    @ApiOperation(value = "대학교 학생증 사진 업로드")
    @PostMapping(value = "/certify/{id}/univ", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiImplicitParam(name = "multipartFile", dataType = "multipartFile",
            value = "파일 보내주시면 파일 s3서버에 저장 및, 해당 파일이 저장되어 있는 URL을 디비에 저장합니다")
    public JSONObject uploadUnivCard(@AuthenticationPrincipal User user, @PathVariable("id") Long certId, @RequestPart MultipartFile multipartFile) {
        return certService.uploadUnivCard(certId, multipartFile);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.OK)
    protected JSONObject handleUserNotFoundException() {
        return PropertyUtil.responseMessage("존재하지 않는 유저입니다.");
    }

    @ExceptionHandler(InstanceNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected JSONObject handleInstanceNotFoundException() {return PropertyUtil.responseMessage("존재하지 않는 객체입니다.");}
}
