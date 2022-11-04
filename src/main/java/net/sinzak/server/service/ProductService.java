package net.sinzak.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sinzak.server.config.auth.dto.SessionUser;
import net.sinzak.server.domain.Product;
import net.sinzak.server.domain.User;
import net.sinzak.server.domain.embed.Size;
import net.sinzak.server.dto.ProductPostDto;
import net.sinzak.server.repository.ProductRepository;
import net.sinzak.server.repository.UserRepository;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    @Transactional
    public JSONObject makeProductPost(SessionUser tempUser, ProductPostDto productPost){
        User user = userRepository.findByEmailFetchPP(tempUser.getEmail()).orElseThrow();
                            /** 존재 하지 않는 유저면 NullPointer 에러 뜰거고, 핸들러가 처리 할 예정 **/
        Product product = Product.builder()
                    .title(productPost.getTitle())  //제목
                    .content(productPost.getContent()) //내용
                    .category(productPost.getCategory())
                    .userName(user.getNickName()) //닉네임
                    .univ(user.getUniv()) // 대학
                    .price(productPost.getPrice()) // 페이
                    .suggest(productPost.isSuggest()) //가격제안여부
                    .field(productPost.getField()) //외주분야
                    .size(new Size(productPost.getWidth(), productPost.getVertical(), productPost.getHeight())) //고용자 or 피고용자
                    .photo(productPost.getPhoto())
                    .build();
        product.setUser(user); // user 연결 및, user의 외주 글 리스트에 글 추가
        productRepository.save(product);
        return PropertyUtil.response(true);
    }
}
