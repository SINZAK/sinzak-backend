package net.sinzak.server.user.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.sinzak.server.BaseTimeEntity;
import net.sinzak.server.chatroom.domain.UserChatRoom;
import net.sinzak.server.product.domain.ProductLikes;
import net.sinzak.server.product.domain.Product;
import net.sinzak.server.product.domain.ProductSell;
import net.sinzak.server.product.domain.ProductWish;
import net.sinzak.server.work.domain.Work;
import net.sinzak.server.work.domain.WorkLikes;
import net.sinzak.server.work.domain.WorkWish;
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
    private static final int hundredMillion = 100000000;
    private static final int tenThousand =10000;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "User_SEQ")
    @Column(name = "user_id")
    private Long id;

    @Column
    private String email;

    @Column
    private String univ_email="";

    @Column
    private String name;

    @Column
    private String nickName=""; //얘는 안 쓸 수도있음

    @Column
    @Setter
    private String picture=""; //대표 사진

    @Column
    private String introduction=""; //한 줄 소개

    @Column
    private String univ="";

    @Column
    private String major;

    @Column
    private String followingNum="0";

    @Column
    private String followerNum="0";

    @Column
    private String stack;  //전문 분야

    @Column
    private String categoryLike="";  //관심 장르

    @Column
    private boolean cert_uni=false; //대학 인증여부

    @Column
    private boolean cert_celeb=false; //인플루언서 인증여부

    @Column
    private int popularity=0;  //'지금 뜨는 아티스트' 때문에 만듦

    @Column
    private String origin; //무슨 로그인인지

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user",cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<UserChatRoom> userChatRooms = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Product> productPostList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ProductSell> productSellList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ProductWish> productWishList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ProductLikes> productLikesList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Work> workPostList = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<WorkWish> workWishList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<WorkLikes> workLikesList = new ArrayList<>();

    @OneToMany(mappedBy = "user",cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Report> reportList = new ArrayList<>();

    @OneToMany(mappedBy = "user",cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<SearchHistory> historyList = new HashSet<>();


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
    public User(String email, String name, String nickName, String categoryLike, String origin) {
        this.email = email;
        this.name = name;
        this.nickName = nickName;
        this.categoryLike = categoryLike;
        this.origin = origin;
        this.roles = Collections.singletonList("ROLE_USER");
        this.role = Role.GUEST;
    }

    @Builder
    public User(String email, String name, String picture, String origin) {
        this.email = email;
        this.name = name;
        this.picture = picture;
        this.origin = origin;
        this.categoryLike = "";
        this.roles = Collections.singletonList("ROLE_USER");
        this.role = Role.GUEST;
    }


    public void saveJoinInfo(String nickName, String categoryLike) {
        this.nickName = nickName;
        this.categoryLike = categoryLike;
    }


    public User update(String name, String introduction){
        this.name =name;

        this.introduction = introduction;
        return this;
    }
    public void updateCategoryLike(String categoryLike){
        this.categoryLike = categoryLike;
    }
    public String getRoleKey(){
        return this.role.getKey();
    }

    public void updateCertifiedUniv(String univName, String univ_email) {
        this.univ_email = univ_email;
        this.univ = univName;
        this.cert_uni = true;
    }

    public void updateEmailForAppleUser(String email) {
        this.email = email;
    }

    public void updateFollowNumber(){
        this.followerNum = followNumberTrans(this.getFollowerList().size());
        this.followingNum = followNumberTrans(this.getFollowingList().size());
    }
    public String followNumberTrans(int number){
        String unit =getUnit(number);
        if(number>=hundredMillion){
            number /= hundredMillion;
        }
        if(number>=tenThousand){
            number /= tenThousand;
        }
        String transNumber = Integer.toString(number);
        if(transNumber.length()>=4){
            transNumber = transNumber.charAt(0)+","+transNumber.substring(1);
        }
        transNumber +=unit;
        return transNumber;
    }
    public String getUnit(int number){
        if(number>=hundredMillion){
            return "억";
        }
        if(number>=tenThousand){
            return "만";
        }
        return "";
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
        return String.valueOf(id);
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