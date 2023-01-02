package net.sinzak.server.product.domain;

import lombok.Builder;
import lombok.Getter;
import net.sinzak.server.BaseTimeEntity;
import net.sinzak.server.user.domain.embed.Size;
import net.sinzak.server.user.domain.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@SequenceGenerator(name = "Product_SEQ_GEN",sequenceName = "Product_SEQ")
public class Product extends BaseTimeEntity { /** 작품 **/

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Product_SEQ")
    @Column(name = "product_id")
    private Long id;  //작품 번호

    @Column
    private String title;

    @Column
    private String content;

    @Column
    private String author; //닉네임

    @Column
    private int price;

    @Column
    private boolean suggest = false;

    @Column
    private String univ="";

    @Column
    private String category; //분류

    @Column
    private int views = 2;

    @Column
    private int likesCnt = 0;

    @Column
    private int wishCnt = 0;

    @Column
    private int chatCnt = 0;

    @Column
    private int popularity = 0;

    @Column
    private String thumbnail;

    @Embedded
    private Size size;

    @Column
    private boolean complete = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;  //수취인

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER) /** 사진은 무조건 EAGER로 같이 불러오기 **/
    private List<ProductImage> images = new ArrayList<>();  //수취인

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE)
    private List<ProductWish> productWishList = new ArrayList<>();  //찜

    @Builder
    public Product(String title, String content, String category, int price, boolean suggest, String author, String univ, Size size) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.price = price;
        this.suggest = suggest;
        this.author = author;
        this.univ = univ;
        this.size = size;
    }

    public void setUser(User user) {
        user.getProductPostList().add(this);
        this.user = user;
    }

    public void addImage(ProductImage images) {
        this.getImages().add(images);
    }

    public void plusWishCnt() {this.wishCnt++;this.popularity+=20;}
    public void minusWishCnt() {if(wishCnt>0) this.wishCnt--;this.popularity-=10;    }
    public void plusLikesCnt() {this.likesCnt++;this.popularity+=10;}
    public void minusLikesCnt() {if(likesCnt>0)this.likesCnt--;this.popularity-=10;}
    protected Product() {}

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void addViews() {this.views++;this.popularity++;}
}
