package net.sinzak.server.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import net.sinzak.server.user.dto.respond.GetFollowDto;
import net.sinzak.server.user.dto.respond.QGetFollowDto;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static net.sinzak.server.user.domain.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserQDSLRepositoryImpl {

    private final JPAQueryFactory queryFactory;

    public Optional<GetFollowDto> findByIdForFollow(Long userId) {
        return Optional.ofNullable(queryFactory
                .select(new QGetFollowDto(user.id, user.nickName, user.picture))
                .from(user)
                .where(user.id.eq(userId)
                        .and(user.isDelete.eq(false)))
                .fetchFirst());
    }
}
