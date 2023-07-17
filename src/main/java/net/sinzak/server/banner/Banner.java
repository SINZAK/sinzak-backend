package net.sinzak.server.banner;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
@SequenceGenerator(name = "Banner_SEQ_GEN", sequenceName = "Banner_SEQ")
public class Banner {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Banner_SEQ")
    @Column(name = "banner_id")
    private Long id;  //작품 번호

    private String content;

    private String imageUrl;

    private String pcImageUrl;

    private String href;

    @Builder
    public Banner(String content, String imageUrl, String pcImageUrl) {
        this.content = content;
        this.imageUrl = imageUrl;
        this.pcImageUrl = pcImageUrl;
        this.href = "";
    }

    protected Banner() {
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        this.pcImageUrl = imageUrl;
    }

    public void setUserInfo(Long userId, String nickName) {
        this.href = "/profile/" + userId.toString();
        this.content = nickName;
    }


}
