package net.sinzak.server.work.domain;

import lombok.Builder;
import lombok.Getter;
import net.sinzak.server.BaseTimeEntity;
import net.sinzak.server.chatroom.domain.ChatRoom;
import net.sinzak.server.product.dto.ProductEditDto;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.work.dto.WorkEditDto;
import org.json.simple.JSONArray;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Getter
@Entity
@SequenceGenerator(name = "Work_SEQ_GEN",sequenceName = "Work_SEQ")
public class Work extends BaseTimeEntity { /** 외주 **/

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Work_SEQ")
    @Column(name = "work_id")
    private Long id;  //작품 번호

    @Column
    private String title;

    @Column
    private String content;

    @Column
    private String author;

    @Column
    private int price;

    @Column
    private boolean suggest;

    @Column
    private int topPrice=0;

    @Column
    private String univ="";

    @Column
    private String category; //분류

    @Column
    private int views = 2;

    @Column
    private int likesCnt = 0;

    @Column
    private int wishCnt = 0;

    @Column
    private int chatCnt = 0;

    @Column
    private int popularity = 0;

    @Column
    private String thumbnail;

    @Column
    private boolean employment; //고용글인지 피고용글인지

    @Column
    private boolean complete = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;  //수취인

    @OneToMany(mappedBy = "work", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER) /** 사진은 무조건 EAGER로 같이 불러오기 **/
    private List<WorkImage> images = new ArrayList<>();  //수취인

    @OneToMany(mappedBy = "work", cascade = CascadeType.REMOVE)
    private List<WorkWish> workWishList = new ArrayList<>();

    @OneToMany(mappedBy = "work")
    private List<ChatRoom> chatRooms = new ArrayList<>();

    @Builder
    public Work(String title, String content, String category, int price, boolean suggest, String author, String univ, boolean employment) {
        this.title = title;
        this.content = content;
        this.category = category;
        this.price = price;
        this.suggest = suggest;
        this.author = author;
        this.univ = univ;
        this.employment = employment;
    }

    public void setUser(User user) {
        user.getWorkPostList().add(this);
        this.user = user;
    }

    public void deleteUser(){
        this.user =null;
    }
    public void editPost(WorkEditDto dto){
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.price = dto.getPrice();
        this.suggest = dto.isSuggest();
    }

    public void plusWishCnt() { this.wishCnt++; }
    public void minusWishCnt() {
        if(wishCnt>0) wishCnt--;
    }
    public void plusLikesCnt() {this.likesCnt++;this.popularity+=10;}
    public void minusLikesCnt() {if(likesCnt>0)this.likesCnt--;this.popularity-=10;}
    protected Work() {}

    public void addChatRoom(ChatRoom chatRoom){
        chatRoom.setWork(this);
        this.chatRooms.add(chatRoom);
        this.chatCnt++;
    }
    public void makeChatRoomNull(){
        for(ChatRoom chatRoom :this.getChatRooms()){
            chatRoom.setWork(null);
        }
    }
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
    public void addViews() {this.views++;this.popularity++;}
    public void addImage(WorkImage image) {
        this.getImages().add(image);
    }

    public void setTopPrice(int topPrice) {
        this.topPrice = topPrice;
    }

    public void setComplete(boolean complete) {this.complete = complete;}
}

