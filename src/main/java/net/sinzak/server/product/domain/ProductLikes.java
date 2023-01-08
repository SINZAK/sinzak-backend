package net.sinzak.server.product.domain;

import lombok.Getter;
import net.sinzak.server.user.domain.User;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Getter
@Entity
public class ProductLikes {

    @Id
    @GeneratedValue
    @Column(name = "likes_id")
    private Long id;  //작품 번호

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="product_id")
    private Product product;


    public static ProductLikes createConnect(Product product, User user){  //생성메서드
        ProductLikes connect = new ProductLikes();
        connect.setProduct(product);
        connect.setUser(user);
        return connect;
    }

    private void setUser(User user){
        user.getProductLikesList().add(this); //스크랩!
        this.user=user;
    }
    public void setProduct(Product product) {
        this.product = product;
    }
}