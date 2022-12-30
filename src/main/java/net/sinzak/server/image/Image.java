package net.sinzak.server.image;

import lombok.Getter;
import net.sinzak.server.BaseTimeEntity;
import net.sinzak.server.product.image.Product;

import javax.persistence.*;

@Getter
@Entity
public class Image extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public Image(String imgUrl, Product product) {
        this.imageUrl = imgUrl;
        this.product = product;
    }

    protected Image() {}
}
