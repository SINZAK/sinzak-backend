package net.sinzak.server.user.domain.follow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.sinzak.server.user.domain.User;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Follow {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="follower_user_id")
    User followerUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="following_user_id")
    User followingUser;
}
