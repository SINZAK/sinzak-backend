package net.sinzak.server.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.sinzak.server.BaseTimeEntity;
import net.sinzak.server.alarm.domain.Alarm;
import net.sinzak.server.chatroom.domain.UserChatRoom;
import net.sinzak.server.product.domain.ProductLikes;
import net.sinzak.server.product.domain.Product;
import net.sinzak.server.product.domain.ProductSell;
import net.sinzak.server.product.domain.ProductWish;
import net.sinzak.server.user.domain.follow.Follower;
import net.sinzak.server.user.domain.follow.Following;
import net.sinzak.server.work.domain.Work;
import net.sinzak.server.work.domain.WorkLikes;
import net.sinzak.server.work.domain.WorkSell;
import net.sinzak.server.work.domain.WorkWish;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

@Getter
@Entity
@SequenceGenerator(name = "User_SEQ_GEN",sequenceName = "User_SEQ")
@DynamicUpdate
@Table(indexes = {@Index(name = "CoveringIndexUser", columnList = "user_id, role")})
public class User extends BaseTimeEntity{
    private static final int hundredMillion = 100000000;
    private static final int tenThousand =10000;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "User_SEQ")
    @Column(name = "user_id")
    private Long id;

    @Column
    private String email;

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
    private String followingNum="0";

    @Column
    private String followerNum="0";

    @Column
    private String portFolioUrl;

    @Column
    private String categoryLike="";  //관심 장르

    @Column
    private boolean cert_uni =false; //대학 인증여부

    @Column
    private boolean cert_author =false; //인증작가 인증여부

    @Column
    private int popularity=0;  //'지금 뜨는 아티스트' 때문에 만듦

    @Column
    private String origin;

    @Column
    @JsonIgnore
    private boolean alarm_receive;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column
    private String fcm ="";

    @Column
    private boolean isDelete= false;

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

    @OneToMany(mappedBy = "user",cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Alarm> alarms = new HashSet<>();


    @OneToMany(mappedBy = "user",cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Follower> followers = new HashSet<>();

    @OneToMany(mappedBy = "user",cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Following> followings = new HashSet<>();


    @ElementCollection
    @CollectionTable(name = "FOLLOWING_LIST", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "FOLLOWING_ID")
    private Set<Long> followingList =new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "FOLLOWER_LIST", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "FOLLOWER_ID")
    private Set<Long> followerList =new HashSet<>();


    public void setFcm(String fcmToken) {
        this.fcm = fcmToken;
    }
    public void setDelete() {
        isDelete = true;
    }

    public void setUniv(String univName) {
        this.univ = univName;
    }

    public void setCertifiedUniv() {
        this.cert_uni = true;
    }

    public void setCertifiedAuthor() {
        this.cert_author = true;
    }

    @Builder
    public User(String email, String nickName, String categoryLike, String origin) throws NoSuchAlgorithmException {
        this.email = email;
        this.nickName = nickName;
        this.categoryLike = categoryLike;
        this.origin = origin;
        this.cert_uni = false;
        this.cert_author = false;
        this.role = Role.USER;
    }

    @Builder
    public User(String email, String picture, String origin) throws NoSuchAlgorithmException {
        this.email = email;
        this.nickName = "";
        this.picture = picture;
        this.origin = origin;
        this.categoryLike = "";
        this.cert_uni = false;
        this.cert_author = false;
        this.role = Role.USER;
//        this.alarm_receive = false;
    }



    public void saveJoinInfo(String nickName, String categoryLike) {
        this.nickName = nickName;
        this.categoryLike = categoryLike;
    }

    @Transient
    private Random random = SecureRandom.getInstanceStrong();

    public void setRandomProfileImage() {
        int randomNumber = random.nextInt(10)+1;
        this.picture = "https://sinzakimage.s3.ap-northeast-2.amazonaws.com/static/profile"+randomNumber+".png";
    }

    public void updateProfile(String name, String introduction){
        this.nickName =name;
        this.introduction = introduction;
    }
    public void updateCategoryLike(String categoryLike){
        this.categoryLike = categoryLike;
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

    protected User() throws NoSuchAlgorithmException {}
}