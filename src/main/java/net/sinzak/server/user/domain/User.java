package net.sinzak.server.user.domain;

import lombok.Builder;
import lombok.Getter;
import net.sinzak.server.BaseTimeEntity;
import net.sinzak.server.product.Likes;
import net.sinzak.server.product.Product;
import net.sinzak.server.product.ProductSell;
import net.sinzak.server.product.ProductWish;
import net.sinzak.server.work.Work;
import net.sinzak.server.work.WorkWish;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Entity
@SequenceGenerator(name = "User_SEQ_GEN",sequenceName = "User_SEQ")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "User_SEQ")
    @Column(name = "user_id")
    private Long id;

    @Column
    private String email;

    @Column
    private String univ_email;

    @Column
    private String name;

    @Column
    private String nickName;

    @Column
    private String picture; //대표 사진

    @Column
    private String introduction; //한 줄 소개

    @Column
    private String univ;

    @Column
    private String major;

    @Column
    private String followingNum;

    @Column
    private String followerNum;

    @Column
    private String stack;  //전문 분야

    @Column
    private String categoryLike;  //관심 장르

    @Column
    private boolean cert_uni; //대학 인증여부

    @Column
    private boolean cert_celeb; //인플루언서 인증여부

    @Column
    private String origin;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Work> workPostList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<WorkWish> workWishList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Product> productPostList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<ProductSell> productSellList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<ProductWish> productWishList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Likes> likesList = new ArrayList<>();


    @ElementCollection
    @CollectionTable(name = "FOLLOWING_LIST", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "FOLLOWING_ID")
    private Set<Long> followingList =new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "FOLLOWER_LIST", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "FOLLOWER_ID")
    private Set<Long> followerList =new HashSet<>();

    @Builder
    public User(String name, String email, String picture, String origin, Role role) {
        this.email = email;
        this.picture = picture;
        this.origin = origin;
        this.role = role;
        this.name = name;
        this.nickName = name;
    }

    public User(String email, String name, String picture) {
        this.email = email;
        this.name = name;
        this.picture = picture;
    }

    protected User() {
    }
    public void updateFollowNumber(String followingNumber,String followerNumber){
        this.followerNum = followerNumber;
        this.followingNum = followingNumber;
    }
    public User update(String name, String picture,String introduction){
        this.name =name;
        this.picture = picture;
        this.introduction = introduction;
        return this;
    }
    public String getRoleKey(){
        return this.role.getKey();
    }

    public void updateUniv(String univ, String univ_email) {
        this.univ_email = univ_email;
        this.univ = univ;
        this.cert_uni = true;
    }
}