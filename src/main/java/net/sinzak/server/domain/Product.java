package net.sinzak.server.domain;

import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
@SequenceGenerator(name = "Product_SEQ_GEN",sequenceName = "Product_SEQ")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Product_SEQ")
    @Column(name = "product_id")
    private Long id;  //작품 번호

    @Column
    private String title;
    @Column
    private String name; //아마 닉네임이 들어갈 예정
    @Column
    private String univ;

    @Column
    private int views;
    @Column
    private int wishCnt;
    @Column
    private int price;
    @Column
    private boolean complete;

}
