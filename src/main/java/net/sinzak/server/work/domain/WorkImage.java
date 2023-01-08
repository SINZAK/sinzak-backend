package net.sinzak.server.work.domain;

import lombok.Getter;
import net.sinzak.server.BaseTimeEntity;
import net.sinzak.server.product.domain.Product;

import javax.persistence.*;

@Getter
@Entity
public class WorkImage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_id", nullable = false)
    private Work work;

    public WorkImage(String imgUrl, Work work) {
        this.imageUrl = imgUrl;
        this.work = work;
    }

    protected WorkImage() {}
}

