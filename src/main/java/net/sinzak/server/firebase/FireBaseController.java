package net.sinzak.server.firebase;


import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import net.sinzak.server.common.PropertyUtil;
import net.sinzak.server.common.UserUtils;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "파이어베이스")
@RestController
@RequiredArgsConstructor
public class FireBaseController {
    private final UserUtils userUtils;
    /**
     * 이건 pushAlarm이 종속적이라 컨트롤러에 어쩔수없이 선언.
     **/
    private final FireBaseService fireBaseService;

    @PostMapping(value = "firebase/test")
    public JSONObject pushAlarm() {
        fireBaseService.sendIndividualNotification(userUtils.getCurrentUser(), "알림", "테스트", "/");
        return PropertyUtil.response(true);
    }
}
