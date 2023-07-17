package net.sinzak.server.product.domain;

import lombok.Getter;
import net.sinzak.server.BaseTimeEntity;

import javax.persistence.*;

@Getter
@Entity
public class ProductImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public ProductImage(String imgUrl, Product product) {
        this.imageUrl = imgUrl;
        this.product = product;
    }

    protected ProductImage() {
    }
}
