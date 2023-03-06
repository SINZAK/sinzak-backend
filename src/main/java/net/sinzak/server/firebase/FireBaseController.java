package net.sinzak.server.firebase;


import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import net.sinzak.server.common.PropertyUtil;
import net.sinzak.server.user.domain.User;
import org.json.simple.JSONObject;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "파이어베이스")
@RestController
@RequiredArgsConstructor
public class FireBaseController {
    private final FireBaseService fireBaseService;

    @PostMapping(value ="firebase/test")
    public JSONObject pushAlarm(@AuthenticationPrincipal User user){
        fireBaseService.sendIndividualNotification(user,"알림","테스트","/");
        return PropertyUtil.response(true);

    }
}
