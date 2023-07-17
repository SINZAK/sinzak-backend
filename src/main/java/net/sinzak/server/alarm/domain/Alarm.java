package net.sinzak.server.alarm.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.sinzak.server.BaseTimeEntity;
import net.sinzak.server.product.domain.Product;
import net.sinzak.server.user.domain.User;

import javax.persistence.*;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Alarm extends BaseTimeEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String opponentUserName;

    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    private String thumbnail;
    private String route;

}
