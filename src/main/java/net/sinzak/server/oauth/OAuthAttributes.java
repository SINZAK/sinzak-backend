package net.sinzak.server.oauth;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;

import java.util.Map;

@Getter
@Slf4j
@ToString
public class OAuthAttributes {
    private String name;
    private String email;
    private String picture="";
    private String origin;

    @Builder
    public OAuthAttributes(String name, String email, String picture, String origin) {
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.origin = origin;
    }

    public static OAuthAttributes of(String registrationId, JSONObject attributes) {
        // 여기서 네이버와 카카오 등 구분 (ofNaver, ofKakao)
        if ("naver".equals(registrationId)) return ofNaver(attributes);
        else if ("kakao".equals(registrationId)) return ofKakao(attributes);
        else if ("apple".equals(registrationId)) return ofApple(attributes);
        return ofGoogle(attributes);
    }


    private static OAuthAttributes ofKakao(JSONObject attributes) {
        log.warn(attributes.toJSONString());
        JSONObject kakao_account = (JSONObject) attributes.get("kakao_account");
        JSONObject profile = (JSONObject) kakao_account.get("profile");
        return OAuthAttributes.builder()
                .name((String) profile.get("nickname"))
                .email((String) kakao_account.get("email"))
                .picture("")
//                .picture((String) profile.get("profile_image_url"))
                .origin("Kakao")
                .build();
    }

    private static OAuthAttributes ofNaver(JSONObject attributes) {
        System.out.println(attributes.toJSONString());
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return OAuthAttributes.builder()
                .name("")
                .email((String) response.get("email"))
                .picture("")
//                .picture((String) response.get("profile_image"))
                .origin("Naver")
                .build();

    }

    private static OAuthAttributes ofGoogle(JSONObject attributes) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture("")
                .origin("Google")
                .build();
    }

    private static OAuthAttributes ofApple(JSONObject attributes) {
        log.warn(attributes.toJSONString());
        return OAuthAttributes.builder()
                .name("미정")
                .email((String) attributes.get("sub"))
                .origin("Apple")
                .build();
    }
}
