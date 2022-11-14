package net.sinzak.server.cert;

import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CertController {

    private final CertService certService;

    @PostMapping("/mail/send")
    public JSONObject sendMail(@RequestBody MailDto mailDto) {
        return certService.sendMail(mailDto);
    }

    @PostMapping("/mail/receive")
    public JSONObject receiveMail(@RequestBody MailDto mailDto) {
        return certService.receiveMail(mailDto);
    }
}
