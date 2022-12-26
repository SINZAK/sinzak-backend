package net.sinzak.server.user.domain;

import lombok.Builder;
import lombok.Getter;
import net.sinzak.server.BaseTimeEntity;
import net.sinzak.server.chatroom.domain.UserChatRoom;
import net.sinzak.server.product.Likes;
import net.sinzak.server.product.Product;
import net.sinzak.server.product.ProductSell;
import net.sinzak.server.product.ProductWish;
import net.sinzak.server.work.Work;
import net.sinzak.server.work.WorkWish;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Getter
@Entity
@SequenceGenerator(name = "User_SEQ_GEN",sequenceName = "User_SEQ")
public class User extends BaseTimeEntity implements UserDetails {

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
    private String nickName; //얘는 안 쓸 수도있음

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
    private int popularity ;  //'지금 뜨는 아티스트' 때문에 만듦

    @Column
    private String origin; //무슨 로그인인지

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

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<UserChatRoom> userChatRooms = new ArrayList<>();


    @ElementCollection
    @CollectionTable(name = "FOLLOWING_LIST", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "FOLLOWING_ID")
    private Set<Long> followingList =new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "FOLLOWER_LIST", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "FOLLOWER_ID")
    private Set<Long> followerList =new HashSet<>();

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

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


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {  /** email 사용 !!! **/
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    protected User() {}
}