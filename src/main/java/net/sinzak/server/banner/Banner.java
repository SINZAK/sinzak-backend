package net.sinzak.server.banner;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
@SequenceGenerator(name = "Banner_SEQ_GEN",sequenceName = "Banner_SEQ")
public class Banner {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Banner_SEQ")
    @Column(name = "banner_id")
    private Long id;  //작품 번호

    private String title;

    private String content;

    private String imageUrl;

    private Long userId;

    @Builder
    public Banner(String title, String content, String imageUrl) {
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.userId = 0L;
    }

    protected Banner() {}

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setUserInfo(Long userId, String nickName) {
        this.userId = userId;
        this.content = nickName;
    }


}
