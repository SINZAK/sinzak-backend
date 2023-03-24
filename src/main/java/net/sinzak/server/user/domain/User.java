package net.sinzak.server.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import net.sinzak.server.work.domain.WorkSell;
import net.sinzak.server.work.domain.WorkWish;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.*;

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
    private String nickName="";

    @Column
    @Setter
    private String picture=""; //대표 사진

    @Column
    private String introduction=""; //한 줄 소개

    @Column
    private String univ="";

    @Column
    @JsonIgnore
    private String major;

    @Column
    private String followingNum="0";

    @Column
    private String followerNum="0";

    @Column
    private String portFolioUrl;

    @Column
    private String categoryLike="";  //관심 장르

    @Column
    private boolean cert_uni=false; //대학 인증여부

    @Column
    private boolean cert_celeb=false; //인플루언서 인증여부

    @Column
    private int popularity=0;  //'지금 뜨는 아티스트' 때문에 만듦

    @Column
    private String origin;

    @Column
    @JsonIgnore
    private boolean alarm_receive;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<UserChatRoom> userChatRooms = new ArrayList<>();

    @OneToMany(mappedBy = "user",cascade = CascadeType.MERGE)
    private List<Product> productPostList = new ArrayList<>();

    @OneToMany(mappedBy = "user" ,cascade = CascadeType.MERGE)
    private Set<Work> workPostList = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ProductSell> productSellList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ProductWish> productWishList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ProductLikes> productLikesList = new ArrayList<>();


    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<WorkSell> workSellList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<WorkWish> workWishList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<WorkLikes> workLikesList = new ArrayList<>();

    @OneToMany(mappedBy = "user",cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Report> reportList = new ArrayList<>();

    @OneToMany(mappedBy = "user",cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<SearchHistory> historyList = new HashSet<>();

    public void setFcm(String fcmToken) {
        this.fcm = fcmToken;
    }

    @Column
    private String fcm ="";

    @ElementCollection
    @CollectionTable(name = "FOLLOWING_LIST", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "FOLLOWING_ID")
    private Set<Long> followingList =new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "FOLLOWER_LIST", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "FOLLOWER_ID")
    private Set<Long> followerList =new HashSet<>();

    @Column
    private boolean isDelete= false;
    public void setDelete(boolean delete) {
        isDelete = delete;
    }


    @Builder
    public User(String email, String name, String nickName, String categoryLike, String origin) {
        this.email = email;
        this.name = name;
        this.nickName = nickName;
        this.categoryLike = categoryLike;
        this.origin = origin;
        this.role = Role.USER;
    }

    @Builder
    public User(String email, String name, String picture, String origin) {
        this.email = email;
        this.name = name;
        this.nickName = "";
        this.picture = picture;
        this.origin = origin;
        this.categoryLike = "";
        this.role = Role.USER;
//        this.alarm_receive = false;
    }

//    public void setAlarm_receive(boolean receive){
//        this.alarm_receive = receive;
//    }


    public void saveJoinInfo(String nickName, String categoryLike) {
        this.nickName = nickName;
        this.categoryLike = categoryLike;
    }

    public void setRandomProfileImage() {
        Random ran = new Random();
        int randomNumber = ran.nextInt(10)+1;
        this.picture = "https://sinzakimage.s3.ap-northeast-2.amazonaws.com/static/profile"+randomNumber+".png";
    }

    public void updateProfile(String name, String introduction){
        this.nickName =name;
        this.introduction = introduction;
    }
    public void updateCategoryLike(String categoryLike){
        this.categoryLike = categoryLike;
    }

    @Transactional
    public void updateCertifiedUniv(String univName, String univ_email) {
        this.univ_email = univ_email;
        this.univ = univName;
        this.cert_uni = true;
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

    public void setPortFolioUrl(String portFolioUrl) {
        this.portFolioUrl = portFolioUrl;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> list = new ArrayList<>();
        list.add(new SimpleGrantedAuthority(this.role.getKey()));
        return list;
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