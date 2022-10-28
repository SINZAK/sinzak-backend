package net.sinzak.server.domain;

import lombok.Getter;

import javax.persistence.*;

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
    private boolean suggest;

    @Column
    private String univ="";

    @Column
    private String field;

    @Column
    private int views = 2;

    @Column
    private int wishCnt = 0;

    @Column
    private int chatCnt = 0;

    @Column
    private String photo;

    @Column
    private boolean complete = false;

}
