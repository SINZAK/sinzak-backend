package net.sinzak.server.common;

import net.sinzak.server.common.dto.ActionForm;
import net.sinzak.server.common.dto.SuggestDto;
import net.sinzak.server.user.domain.User;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Set;

public interface PostService<T,R,V,N,I> {
    JSONObject makePost(R postDto);
    JSONObject showDetailForUser(Long currentUserId, Long id);
    JSONObject showDetailForGuest(Long id);
    JSONObject wish(@RequestBody ActionForm form);
    JSONObject likes(@RequestBody ActionForm form);
    JSONObject suggest(@RequestBody SuggestDto dto);
    List<String> getImages(List<I> i);

    boolean checkIsLikes(List<N> userLikesList, T t);
    boolean checkIsWish(User user, List<V> wishList);
    boolean checkIsFollowing(Set<Long> userFollowingList, T t);
}
