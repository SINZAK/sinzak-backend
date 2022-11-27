package net.sinzak.server.product;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class ProductImage {

    @Id
    @GeneratedValue
    @Column(name = "image_id")
    private Long id;  //작품 번호

    @Column
    private String title;

    @Column
    private Long imageUrl;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
