package net.sinzak.server.chatroom.domain;

import lombok.Getter;
import net.sinzak.server.BaseTimeEntity;
import net.sinzak.server.common.PostType;
import net.sinzak.server.product.domain.Product;
import net.sinzak.server.work.domain.Work;

import javax.persistence.*;
import java.util.*;

@Getter
@Entity
public class ChatRoom extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer participantsNumber;
    private String roomUuid;
    private String roomName;
    @Enumerated(EnumType.STRING)
    private PostType postType;

    private boolean blocked =false;

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public ChatRoom(){
        this.participantsNumber = 0;
        this.roomUuid = UUID.randomUUID().toString();
    }
    public ChatRoom(String roomName) {
        this.roomName = roomName;
        this.participantsNumber = 0;
        this.roomUuid = UUID.randomUUID().toString();
    }

    @OneToMany(cascade = CascadeType.ALL)
    private List<ChatMessage> chatMessages = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom",cascade = CascadeType.ALL)
    private Set<UserChatRoom> userChatRooms = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="work_id")
    private Work work;
    public void setProduct(Product product){
        this.product = product;
        this.postType = PostType.PRODUCT;
    }
    public void setWork(Work work){
        this.work = work;
        this.postType = PostType.WORK;
    }
    public void addUserChatRoom(UserChatRoom userChatRoom){;
        userChatRoom.setChatRoom(this);
        this.userChatRooms.add(userChatRoom);
        this.participantsNumber++;
    }
    public void addChatMessage(ChatMessage chatMessage){
        this.chatMessages.add(chatMessage);
        for(UserChatRoom userChatRoom :this.userChatRooms){
            userChatRoom.updateLatestMessage(chatMessage.getMessage());
        }
    }

    public UserChatRoom leaveChatRoom(String userEmail){
        for(UserChatRoom userChatRoom :this.userChatRooms){
            if(userChatRoom.getUser().getEmail().equals(userEmail)){
                this.participantsNumber--;
                return userChatRoom;
            }
        }
        return null;
    }

//    @OneToMany(mappedBy = "chatRoom",cascade = CascadeType.ALL)
//    private List<ChatMessage> chatMessages = new ArrayList<>();
//    @OneToMany(mappedBy = "chatRoom",cascade = CascadeType.ALL)
//    private Set<UserChatRoom> userChatRooms = new HashSet<>();

//    public void addUserChatRoom(User user,User inviteUser){
//        UserChatRoom userChatRoom = new UserChatRoom(inviteUser.getName(),inviteUser.getPicture());
//        userChatRoom.setChatRoom(this);
//        userChatRoom.setUser(user);
//        this.participantsNumber++;
//        this.userChatRooms.add(userChatRoom);
//    }

}
