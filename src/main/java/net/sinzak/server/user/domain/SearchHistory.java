package net.sinzak.server.user.domain;

import lombok.Getter;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Getter
@Entity
public class SearchHistory {
    @Id
    @GeneratedValue
    @Column(name = "history_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private String word;

    public static SearchHistory addSearchHistory(String word, User user) {  //생성메서드
        SearchHistory connect = new SearchHistory();
        connect.setUser(user);
        connect.setWord(word);
        return connect;
    }

    public void setUser(User user) {
        this.user = user;
        user.getHistories()
                .add(this);
    }

    public void setWord(String word) {
        this.word = word;
    }
}
