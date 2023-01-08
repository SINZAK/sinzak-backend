package net.sinzak.server.common;

import net.sinzak.server.common.dto.ActionForm;
import net.sinzak.server.common.dto.DetailForm;
import net.sinzak.server.common.dto.SuggestDto;
import net.sinzak.server.user.domain.User;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Set;

public interface PostService<T,R,V,N> {
    JSONObject makePost(User User, R postDto);
    DetailForm showDetail(Long id, User User);
    DetailForm showDetail(Long id);
    JSONObject wish(User User, @RequestBody ActionForm form);
    JSONObject likes(User User, @RequestBody ActionForm form);
    JSONObject suggest(User User, @RequestBody SuggestDto dto);
    List<String> getImages(T t);

    boolean checkIsLikes(List<N> userLikesList, T t);
    boolean checkIsWish(User user, List<V> wishList);
    boolean checkIsFollowing(Set<Long> userFollowingList, T t);
}
