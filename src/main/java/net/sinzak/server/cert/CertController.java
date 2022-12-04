package net.sinzak.server.cert;

import lombok.RequiredArgsConstructor;
import net.sinzak.server.config.auth.LoginUser;
import net.sinzak.server.config.auth.dto.SessionUser;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequiredArgsConstructor
public class CertController {

    private final CertService certService;

    @PostMapping("/mail/send")
    public JSONObject sendMail(@RequestBody MailDto mailDto) {
        return certService.sendMail(mailDto);
    }

    @PostMapping("/mail/receive")
    public JSONObject receiveMail(@ApiIgnore @LoginUser SessionUser user, @RequestBody MailDto mailDto) {
        return certService.receiveMail(user, mailDto);
    }
}
