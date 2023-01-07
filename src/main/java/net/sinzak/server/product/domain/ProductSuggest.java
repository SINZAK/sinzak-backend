package net.sinzak.server.product.domain;

import lombok.Getter;
import net.sinzak.server.user.domain.User;

import javax.persistence.*;

@Entity
@Getter
@SequenceGenerator(name = "Product_SELL_SEQ_GEN",sequenceName = "Product_SELL_SEQ")
public class ProductSuggest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "suggest_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER) // (fetch = FetchType.LAZY)  반드시 product의 정보는 같이 불러와야하기에
    @JoinColumn(name = "product_id")
    private Product product;

    public static ProductSuggest createConnect(Product product, User user){  //생성메서드
        ProductSuggest connect = new ProductSuggest(user,product);
        return connect;
    }


    public void setProduct(Product product) {  this.product = product; }

    protected ProductSuggest() {}

    public ProductSuggest(User user, Product product) {
        this.user = user;
        this.product = product;
    }
}
