package net.sinzak.server.cert;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import lombok.RequiredArgsConstructor;
import net.sinzak.server.config.auth.LoginUser;
import net.sinzak.server.config.auth.dto.SessionUser;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@Api(tags = "인증")
@RestController
@RequiredArgsConstructor
public class CertController {

    private final CertService certService;

    @ApiOperation(value = "대학 메일 인증 시작", notes = "address, univ만 주시면 됩니다  1000~9999의 인증번호 메일전송예정")
    @PostMapping("/mail/send")
    public JSONObject sendMail(@RequestBody MailDto mailDto) {
        return certService.sendMail(mailDto);
    }

    @ApiOperation(value = "인증코드 확인", notes = "\"success\" : false 가 오더라도 학생증 인증도 있다는 걸 안내해야됩니다.\n " +
            "join 시 cert_univ = false 로 전달바랍니다")
    @PostMapping("/mail/receive")
    public JSONObject receiveMail(@ApiIgnore @LoginUser SessionUser user, @RequestBody MailDto mailDto) {
        return certService.receiveMail(user, mailDto);
    }
}
