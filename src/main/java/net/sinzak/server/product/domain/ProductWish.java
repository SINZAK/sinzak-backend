package net.sinzak.server.product.domain;

import lombok.Getter;
import net.sinzak.server.BaseTimeEntity;
import net.sinzak.server.user.domain.User;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
public class ProductWish extends BaseTimeEntity {

    @Id
    @GeneratedValue
    @Column(name = "product_wish_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id")
    private Product product;


    public static ProductWish createConnect(Product product, User user) {  //생성메서드
        ProductWish connect = new ProductWish();
        connect.setProduct(product);
        connect.setUser(user);
        return connect;
    }

    private void setUser(User user) {
        user.getProductWishList().add(this); //스크랩!
        this.user = user;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    protected ProductWish() {
    }

}
