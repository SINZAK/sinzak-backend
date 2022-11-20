package net.sinzak.server.product;

import lombok.Getter;
import net.sinzak.server.user.domain.User;

import javax.persistence.*;

@Entity
@Getter
@SequenceGenerator(name = "Product_SELL_SEQ_GEN",sequenceName = "Product_SELL_SEQ")
public class ProductSell {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Product_SELL_SEQ")
    @Column(name = "sell_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER) // (fetch = FetchType.LAZY)  반드시 product의 정보는 같이 불러와야하기에
    @JoinColumn(name = "product_id")
    private Product product;

    public static ProductSell createConnect(Product product, User user){  //생성메서드
        ProductSell connect = new ProductSell();
        connect.setProduct(product);
        connect.setUser(user);
        return connect;
    }

    private void setUser(User user){
        user.getProductSellList().add(this); //구매목록에 추가
        this.user=user;
    }

    public void setProduct(Product product) {  this.product = product; }

    protected ProductSell() {}
}
