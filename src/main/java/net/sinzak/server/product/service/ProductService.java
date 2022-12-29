package net.sinzak.server.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sinzak.server.config.auth.dto.SessionUser;
import net.sinzak.server.product.Likes;
import net.sinzak.server.product.Product;
import net.sinzak.server.product.ProductSell;
import net.sinzak.server.product.ProductWish;
import net.sinzak.server.common.PropertyUtil;
import net.sinzak.server.product.dto.DetailForm;
import net.sinzak.server.product.dto.SellDto;
import net.sinzak.server.product.dto.ShowForm;
import net.sinzak.server.product.repository.LikesRepository;
import net.sinzak.server.product.repository.ProductSellRepository;
import net.sinzak.server.product.repository.ProductWishRepository;
import net.sinzak.server.product.repository.ProductRepository;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.user.domain.embed.Size;
import net.sinzak.server.product.dto.ProductPostDto;
import net.sinzak.server.common.dto.WishForm;
import net.sinzak.server.user.repository.UserRepository;
import org.json.simple.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductSellRepository productSellRepository;
    private final ProductWishRepository productWishRepository;
    private final LikesRepository likesRepository;

    @Transactional
    public JSONObject makePost(SessionUser tempUser, ProductPostDto productPost){   // 글 생성
        User user = userRepository.findByEmailFetchPP(tempUser.getEmail()).orElseThrow();
                            /** 존재 하지 않는 유저면 NullPointer 에러 뜰거고, 핸들러가 처리 할 예정 **/
        Product product = Product.builder()
                    .title(productPost.getTitle())  //제목
                    .content(productPost.getContent()) //내용
                    .category(productPost.getCategory())
                    .author(user.getNickName()) //닉네임
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

    @Transactional(readOnly = true)
    public DetailForm showDetail(Long id, User User){   // 글 생성
        User user = userRepository.findByEmailFetchFLandLL(User.getEmail()).orElseThrow();
        Set<Long> userFollowingList = user.getFollowingList();
        List<Likes> userLikesList = user.getLikesList();  /** 유저가 좋아요 누른 작품 목록 **/
        Product product = productRepository.findByEmailFetchPWUser(id).orElseThrow();
        List<ProductWish> productWishList = product.getProductWishList();  /** 작품 찜 누른 사람 목록 **/
        DetailForm detailForm = DetailForm.builder()
                .id(product.getId())
                .author(product.getAuthor())
                .author_picture(product.getUser().getPicture())
                .univ(product.getUser().getUniv())
                .cert_uni(product.getUser().isCert_uni())
                .cert_celeb(product.getUser().isCert_celeb())
                .followerNum(product.getUser().getFollowerNum())
                .photo(product.getPhoto())
                .title(product.getTitle())
                .price(product.getPrice())
                .category(product.getCategory())
                .date(product.getPhoto())
                .content(product.getContent())
                .suggest(product.isSuggest())
                .likesCnt(product.getLikesCnt())
                .views(product.getViews())
                .wishCnt(product.getWishCnt())
                .chatCnt(product.getChatCnt())
                .width(product.getSize().width)
                .vertical(product.getSize().vertical)
                .height(product.getSize().height)
                .complete(product.isComplete())
                .build();

        boolean isLike = false;
        for (Likes likes : userLikesList) {
            if(likes.getProduct().getId().equals(product.getId())){
                isLike = true;
                break;
            }
        }
        boolean isWish = false;
        for (ProductWish productWish : productWishList) {
            if(productWish.getUser().getId().equals(user.getId())){
                isWish = true;
                break;
            }
        }
        boolean isFollowing = false;
        for (Long followingId : userFollowingList) {
            if(product.getUser().getId().equals(followingId)){
                isFollowing = true;
                break;
            }
        }
        detailForm.setLikeAndWish(isLike,isWish, isFollowing);

        return detailForm;
    }

    @Transactional(readOnly = true)
    public DetailForm showDetail(Long id){   // 글 생성
        Product product = productRepository.findByEmailFetchPWUser(id).orElseThrow();
        DetailForm detailForm = DetailForm.builder()
                .id(product.getId())
                .author(product.getAuthor())
                .author_picture(product.getUser().getPicture())
                .univ(product.getUser().getUniv())
                .cert_uni(product.getUser().isCert_uni())
                .cert_celeb(product.getUser().isCert_celeb())
                .followerNum(product.getUser().getFollowerNum())
                .photo(product.getPhoto())
                .title(product.getTitle())
                .price(product.getPrice())
                .category(product.getCategory())
                .date(product.getPhoto())
                .content(product.getContent())
                .suggest(product.isSuggest())
                .likesCnt(product.getLikesCnt())
                .views(product.getViews())
                .wishCnt(product.getWishCnt())
                .chatCnt(product.getChatCnt())
                .width(product.getSize().width)
                .vertical(product.getSize().vertical)
                .height(product.getSize().height)
                .complete(product.isComplete())
                .build();
        detailForm.setLikeAndWish(false,false,false);

        return detailForm;
    }

    @Transactional
    public JSONObject wish(User User, @RequestBody WishForm form){   // 찜
        JSONObject obj = new JSONObject();
        User user = userRepository.findByEmailFetchPW(User.getEmail()).orElseThrow(); // 작품 찜까지 페치 조인
        List<ProductWish> wishList = user.getProductWishList(); //wishList == 유저의 찜 리스트
        boolean isWish=false;
        Optional<Product> Product = productRepository.findById(form.getId());
        if(Product.isPresent()){
            Product product = Product.get();
            if(wishList.size()!=0){ /** 유저가 찜이 누른 적이 있다면 이미 누른 작품인지 비교 **/
                for (ProductWish wish : wishList) { //유저의 찜목록과 현재 누른 작품의 찜과 비교
                    if(product.equals(wish.getProduct())) {  //같으면 이미 찜 누른 항목
                        isWish = true;
                        break;
                    }
                }
            }

            if (form.isMode() && !isWish){
                product.plusWishCnt();
                ProductWish connect = ProductWish.createConnect(product, user);
                productWishRepository.save(connect);
                isWish=true;
                obj.put("success",true);
            }
            else if(!form.isMode() && isWish){
                product.minusWishCnt();
                for (ProductWish wish : wishList) { //유저의 찜목록과 현재 누른 작품의 찜과 비교
                    if(product.equals(wish.getProduct())) {  //같으면 이미 찜 누른 항목
                        productWishRepository.delete(wish);
                        isWish = false;
                        break;
                    }
                }
                obj.put("success",true);
            }
            else
                obj.put("success",false);
            obj.put("isWish",isWish);
            return obj;
        }
        return PropertyUtil.responseMessage(HttpStatus.NOT_FOUND,"존재하지 않는 작품에 요청된 찜");
    }

    @Transactional
    public JSONObject likes(User User, @RequestBody WishForm form){   // 좋아요
        JSONObject obj = new JSONObject();
        User user = userRepository.findByEmailFetchLL(User.getEmail()).orElseThrow(); // 작품 좋아요까지 페치 조인
        List<Likes> likesList = user.getLikesList(); //likesList == 유저의 좋아요 리스트
        boolean isLike=false;
        Optional<Product> Product = productRepository.findById(form.getId());
        if(Product.isPresent()){
            Product product = Product.get();
            if(likesList.size()!=0){ /** 유저가 좋아요룰 누른 적이 있다면 이미 누른 작품인지 비교 **/
                for (Likes like : likesList) { //유저의 찜목록과 현재 누른 작품의 찜과 비교
                    if(product.equals(like.getProduct())) {  //같으면 이미 찜 누른 항목
                        isLike = true;
                        break;
                    }
                }
            }

            if (form.isMode() && !isLike){
                product.plusLikesCnt();
                Likes connect = Likes.createConnect(product, user);
                likesRepository.save(connect);
                isLike=true;
                obj.put("success",true);
            }
            else if(!form.isMode() && isLike){
                product.minusLikesCnt();
                for (Likes like : likesList) { //유저의 찜목록과 현재 누른 작품의 찜과 비교
                    if(product.equals(like.getProduct())) {  //같으면 이미 찜 누른 항목
                        likesRepository.delete(like);
                        isLike = false;
                        break;
                    }
                }
                obj.put("success",true);
            }
            else
                obj.put("success",false);
            obj.put("isLike",isLike);
            return obj;
        }
        return PropertyUtil.responseMessage(HttpStatus.NOT_FOUND,"존재하지 않는 작품에 요청된 좋아요");
    }

    @Transactional
    public JSONObject sell(User User, @RequestBody SellDto dto){   // 판매완료시
        User user = userRepository.findByEmailFetchPS(User.getEmail()).orElseThrow();
        Product product = productRepository.findById(dto.getProductId()).orElseThrow();
        ProductSell connect = ProductSell.createConnect(product, user);
        productSellRepository.save(connect);
        return PropertyUtil.response(true);
    }

    @Transactional(readOnly = true)
    public JSONObject showHome(User User){
        JSONObject obj = new JSONObject();

        User user = userRepository.findByEmailFetchFLandLL(User.getEmail()).orElseThrow();
        List<Likes> userLikesList = user.getLikesList();  /** 유저 좋아요 목록 **/
        List<Product> productList = productRepository.findAll();
        List<ShowForm> newList = getNewList(userLikesList, productList);  /** 신작 3개 **/
        obj.put("new",newList);

        int count = 3;
        List<ShowForm> recommendList = getRecommendList(user, userLikesList, productList,count);  /** 추천목록 3개 **/
        obj.put("recommend",recommendList);

        Set<Long> followingIdList = user.getFollowingList();  /** 팔로잉 관련 3개 **/
        List<ShowForm> followingList = getFollowingList(userLikesList, productList, followingIdList);
        obj.put("following",followingList);

        return obj;
    }

    @Transactional(readOnly = true)
    public JSONObject showHome(){  /**비회원 시 recommend 를 어떻게할지? **/
        JSONObject obj = new JSONObject();

        List<Product> productList = productRepository.findAll();
        List<ShowForm> newList = new ArrayList<>();  /** 신작3개 **/
        for (int i = 0; i < productList.size(); i++) {
            if(i==3)
                break;  /** 홈화면이니까 3개까지만 가져오자 **/
            ShowForm showForm = new ShowForm(productList.get(i).getId(), productList.get(i).getTitle(), productList.get(i).getContent(), productList.get(i).getAuthor(), productList.get(i).getPrice(), productList.get(i).getPhoto(), productList.get(i).getCreatedDate().toString(), productList.get(i).isSuggest(), false, productList.get(i).getLikesCnt(), productList.get(i).isComplete());;
            newList.add(showForm);
        }
        obj.put("new",newList);

        List<ShowForm> recommendList = newList;
        obj.put("recommend",recommendList);

        List<ShowForm> followingList = newList;
        obj.put("following",followingList);

        return obj;
    }

    @Transactional(readOnly = true)
    public List<ShowForm> showRecommendDetail(User User){
        User user = userRepository.findByEmailFetchLL(User.getEmail()).orElseThrow();
        List<Likes> userLikesList = user.getLikesList();  /** 유저 좋아요 목록 **/
        List<Product> productList = productRepository.findAll();

        int count = 50;
        List<ShowForm> recommendList = getRecommendList(user, userLikesList, productList, count);  /** 추천목록 50개까지만 **/

        return recommendList;
    }

    @Transactional(readOnly = true)
    public List<ShowForm> showFollowingDetail(User User){
        User user = userRepository.findByEmailFetchFLandLL(User.getEmail()).orElseThrow();
        List<Likes> userLikesList = user.getLikesList();  /** 유저 좋아요 목록 **/
        List<Product> productList = productRepository.findAll();

        Set<Long> followingIdList = user.getFollowingList();  /** 팔로잉 관련 3개 **/

        List<ShowForm> followingList = getFollowingList(userLikesList, productList, followingIdList);

        return followingList;
    }

    private List<ShowForm> getRecommendList(User user, List<Likes> userLikesList, List<Product> productList, int count) {
        List<Product> tempRecommendList; /** 카테고리 관련 **/
        String[] categories = user.getCategoryLike().split(",");
        if(categories.length == 1)
            tempRecommendList = productRepository.find1Recommend3(categories[0], count);
        else if(categories.length == 2)
            tempRecommendList = productRepository.find2Recommend3(categories[0],categories[1], count);
        else if(categories.length == 3)
            tempRecommendList = productRepository.find3Recommend3(categories[0],categories[1],categories[2], count);
        else
            tempRecommendList = productList;
        List<ShowForm> recommendList = new ArrayList<>();
        for (Product product : tempRecommendList) { /** 추천 목록 중 좋아요 누른거 체크 후 ShowForm 으로 담기 **/
            boolean isLike = false;
            for (Likes likes : userLikesList) {
                if(likes.getProduct().getId().equals(product.getId())){
                    isLike = true;
                    break;
                }
            }
            ShowForm showForm = new ShowForm(product.getId(), product.getTitle(), product.getContent(), product.getAuthor(), product.getPrice(), product.getPhoto(),product.getCreatedDate().toString(),product.isSuggest(), isLike, product.getLikesCnt(), product.isComplete());
            recommendList.add(showForm);
        }
        return recommendList;
    }

    private List<ShowForm> getNewList(List<Likes> userLikesList, List<Product> productList) {
        List<ShowForm> newList = new ArrayList<>();
        for (int i = 0; i < productList.size(); i++) {
            if(i==3)
                break;  /** 홈화면이니까 3개까지만 가져오자 **/
            boolean isLike = false;
            for (Likes likes : userLikesList) {
                if (likes.getProduct().getId().equals(productList.get(i).getId())) {
                    isLike = true;
                    break;
                }
            }
            ShowForm showForm = new ShowForm(productList.get(i).getId(), productList.get(i).getTitle(), productList.get(i).getContent(), productList.get(i).getAuthor(), productList.get(i).getPrice(), productList.get(i).getPhoto(), productList.get(i).getCreatedDate().toString(), productList.get(i).isSuggest(), isLike, productList.get(i).getLikesCnt(), productList.get(i).isComplete());;
            newList.add(showForm);
        }
        return newList;
    }


    private List<ShowForm> getFollowingList(List<Likes> userLikesList, List<Product> productList, Set<Long> followingIdList) {
        List<Product> followingProductList = new ArrayList<>();
        for (Long followingId : followingIdList) {
            for (Product product : productList) {
                if(product.getUser().getId().equals(followingId)){
                    followingProductList.add(product);
                }
            }
        }
        followingProductList.sort((o1, o2) -> (int) (o2.getId() - o1.getId()));  //내림차순 정렬
        List<ShowForm> followingList = new ArrayList<>();
        for (int i = 0; i < followingProductList.size(); i++) {
            if(i==3)
                break;  /** 홈화면이니까 3개까지만 가져오자 **/
            boolean isLike = false;
            for (Likes likes : userLikesList) {
                if(likes.getProduct().getId().equals(followingProductList.get(i).getId())){
                    isLike = true;
                    break;
                }
            }
            ShowForm showForm = new ShowForm(followingProductList.get(i).getId(), followingProductList.get(i).getTitle(), followingProductList.get(i).getContent(), followingProductList.get(i).getAuthor(), followingProductList.get(i).getPrice(), followingProductList.get(i).getPhoto(), followingProductList.get(i).getCreatedDate().toString(), followingProductList.get(i).isSuggest(), isLike, followingProductList.get(i).getLikesCnt(), followingProductList.get(i).isComplete());
            followingList.add(showForm);
        }

        return followingList;
    }

    public PageImpl<ShowForm> productListForUser(User user, List<String> categories, Pageable pageable){
        Page<Product> productList;
        List<Likes> userLikesList = user.getLikesList();
        if(categories.size()==0)
            productList = productRepository.findAll(pageable);
        else
            productList = filter(categories, pageable);  //파라미터 입력받았을 경우
        List<ShowForm> showList = new ArrayList<>();
        for (Product product : productList.getContent()) {
            boolean isLike = false;
            for (Likes likes : userLikesList) {
                if(likes.getProduct().getId().equals(product.getId())){
                    isLike = true;
                    break;
                }
            }
            ShowForm showForm = new ShowForm(product.getId(), product.getTitle(), product.getContent(), product.getAuthor(), product.getPrice(), product.getPhoto(),product.getCreatedDate().toString(),product.isSuggest(),isLike, product.getLikesCnt(), product.isComplete());
            showList.add(showForm);
        }
        return new PageImpl<>(showList, pageable, productList.getTotalElements());
    }

    public PageImpl<ShowForm> productListForGuest(List<String> stacks, Pageable pageable){
        Page<Product> projectList;
        if(stacks.size()==0)
            projectList = productRepository.findAll(pageable);
        else
            projectList = filter(stacks, pageable);  //파라미터 입력받았을 경우
        List<ShowForm> showList = new ArrayList<>();

        for (Product product : projectList.getContent()) {
            ShowForm showForm = new ShowForm(product.getId(), product.getTitle(), product.getContent(), product.getAuthor(), product.getPrice(), product.getPhoto(),product.getCreatedDate().toString(),product.isSuggest(),false, product.getLikesCnt(), product.isComplete());
            showList.add(showForm);
        }
        return new PageImpl<>(showList, pageable, projectList.getTotalElements());
    }

    private Page<Product> filter(List<String> categories, Pageable pageable) {
        if (categories.size() == 1)
            return productRepository.findBy1StacksDesc(pageable, categories.get(0));
        else if (categories.size() == 2)
            return productRepository.findBy2StacksDesc(pageable, categories.get(0), categories.get(1));
        else if (categories.size() == 3)
            return productRepository.findBy3StacksDesc(pageable, categories.get(0), categories.get(1), categories.get(2));
        else
            return productRepository.findAll(pageable);
    }



}
