package net.sinzak.server.common;

import net.sinzak.server.common.dto.ActionForm;
import net.sinzak.server.product.domain.Likes;
import net.sinzak.server.product.domain.Product;
import net.sinzak.server.product.domain.ProductWish;
import net.sinzak.server.product.dto.DetailForm;
import net.sinzak.server.product.dto.SuggestDto;
import net.sinzak.server.user.domain.User;
import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

public interface PostService<T,R,V> {
    JSONObject makePost(User User, R postDto, List<MultipartFile> multipartFiles);
    DetailForm showDetail(Long id, User User);
    DetailForm showDetail(Long id);
    JSONObject wish(User User, @RequestBody ActionForm form);
    JSONObject likes(User User, @RequestBody ActionForm form);
    JSONObject trading(@RequestBody ActionForm form);
    JSONObject suggest(User User, @RequestBody SuggestDto dto);
    List<String> getProductImages(Product product);

    boolean checkIsLikes(List<Likes> userLikesList, Product product);
    boolean checkIsWish(User user, List<ProductWish> productWishList);
    boolean checkIsFollowing(Set<Long> userFollowingList, Product product);
}
