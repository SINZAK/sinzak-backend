package net.sinzak.server.product;

import lombok.Builder;
import lombok.Getter;
import net.sinzak.server.user.domain.embed.Size;
import net.sinzak.server.user.domain.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@SequenceGenerator(name = "Product_SEQ_GEN",sequenceName = "Product_SEQ")
public class Product { /** 작품 **/

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Product_SEQ")
    @Column(name = "product_id")
    private Long id;  //작품 번호

    @Column
    private String title;

    @Column
    private String content;

    @Column
    private String userName; //닉네임

    @Column
    private int price;

    @Column
    private boolean suggest = false;

    @Column
    private String univ="";

    @Column
    private String category; //분류

    @Column
    private String field;  //분야

    @Column
    private int views = 2;

    @Column
    private int wishCnt = 0;

    @Column
    private int chatCnt = 0;

    @Column
    private String photo;

    @Embedded
    private Size size;

    @Column
    private boolean complete = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;  //수취인

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE)
    private List<ProductWish> productWishList = new ArrayList<>();  //프로젝트-회원 엮여있는 리스트  스크랩!!!!

    @Builder
    public Product(String title, String content, String category, int price, boolean suggest, String userName, String univ, String field, String photo, Size size) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.price = price;
        this.suggest = suggest;
        this.userName = userName;
        this.univ = univ;
        this.field = field;
        this.photo = photo;
        this.size = size;
    }

    public void setUser(User user) {
        user.getProductPostList().add(this);
        this.user = user;
    }


    public void plusWishCnt() {
        this.wishCnt++;
    }
    public void minusWishCnt() {
        if(wishCnt>0) wishCnt--;
    }
    protected Product() {
    }

}
