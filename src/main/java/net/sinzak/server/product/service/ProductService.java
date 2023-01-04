package net.sinzak.server.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sinzak.server.common.error.UserNotFoundException;
import net.sinzak.server.image.S3Service;
import net.sinzak.server.product.domain.*;
import net.sinzak.server.common.PropertyUtil;
import net.sinzak.server.product.dto.DetailForm;
import net.sinzak.server.product.dto.SellDto;
import net.sinzak.server.product.dto.ShowForm;
import net.sinzak.server.product.repository.*;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.user.domain.embed.Size;
import net.sinzak.server.product.dto.ProductPostDto;
import net.sinzak.server.common.dto.ActionForm;
import net.sinzak.server.user.repository.UserRepository;
import org.json.simple.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductSellRepository productSellRepository;
    private final ProductWishRepository productWishRepository;
    private final ProductImageRepository imageRepository;
    private final LikesRepository likesRepository;
    private final S3Service s3Service;

    private final int HOME_OBJECTS = 3;
    private final int HOME_DETAIL_OBJECTS = 50;

    @Transactional(rollbackFor = {Exception.class})
    public JSONObject makePost(User User, ProductPostDto buildDto, List<MultipartFile> multipartFiles){   // 글 생성
        User user = userRepository.findByEmailFetchProductPostList(User.getEmail()).orElseThrow(); /** 존재 하지 않는 유저면 NullPointer 에러 뜰거고, 핸들러가 처리 할 예정 **/
        Product product = Product.builder()
                    .title(buildDto.getTitle())  //제목
                    .content(buildDto.getContent()) //내용
                    .category(buildDto.getCategory())
                    .author(user.getNickName()) //닉네임
                    .univ(user.getUniv()) // 대학
                    .price(buildDto.getPrice()) // 페이
                    .suggest(buildDto.isSuggest()) //가격제안여부
                    .size(new Size(buildDto.getWidth(), buildDto.getVertical(), buildDto.getHeight()))
                    .build();
        product.setUser(user); // user 연결 및, user의 외주 글 리스트에 글 추가
        productRepository.save(product); // 미리 저장해야 이미지도 저장가능..
        return saveImageInS3AndProduct(multipartFiles, product);
    }

    private JSONObject saveImageInS3AndProduct(List<MultipartFile> multipartFiles, Product product) {
        for (MultipartFile img : multipartFiles) {  /** 이미지 추가, s3에 저장 **/
            try{
                String url = uploadImage(multipartFiles, product, img);
                saveImageUrl(product, url);
            }
            catch (Exception e){
                return PropertyUtil.responseMessage("이미지 저장 실패");
            }
        }
        return PropertyUtil.response(true);
    }


    private String uploadImage(List<MultipartFile> multipartFiles, Product product, MultipartFile img) {
        String url = s3Service.uploadImage(img);
        if(img.equals(multipartFiles.get(0)))
            product.setThumbnail(url);
        return url;
    }

    private void saveImageUrl(Product product, String url) {
        ProductImage image = new ProductImage(url, product);
        product.addImage(image);
        imageRepository.save(image);
    }

    @Transactional
    public DetailForm showDetail(Long id, User User){   // 글 상세 확인
        User user = userRepository.findByEmailFetchFollowingAndLikesList(User.getEmail()).orElseThrow();
        Product product = productRepository.findByIdFetchPWUser(id).orElseThrow();

        DetailForm detailForm = DetailForm.builder()
                .id(product.getId())
                .author(product.getAuthor())
                .author_picture(product.getUser().getPicture())
                .univ(product.getUser().getUniv())
                .cert_uni(product.getUser().isCert_uni())
                .cert_celeb(product.getUser().isCert_celeb())
                .followerNum(product.getUser().getFollowerNum())
                .images(getProductImages(product))  /** 이미지 엔티티에서 url만 빼오기 **/
                .title(product.getTitle())
                .price(product.getPrice())
                .category(product.getCategory())
                .date(product.getCreatedDate().toString())
                .content(product.getContent())
                .suggest(product.isSuggest())
                .likesCnt(product.getLikesCnt())
                .views(product.getViews())
                .wishCnt(product.getWishCnt())
                .chatCnt(product.getChatCnt())
                .width(product.getSize().width)
                .vertical(product.getSize().vertical)
                .height(product.getSize().height)
                .trading(product.isTrading())
                .complete(product.isComplete()).build();

        boolean isLike = checkIsLikes(user.getLikesList(), product);
        boolean isWish = checkIsWish(user, product.getProductWishList());
        boolean isFollowing  = checkIsFollowing(user.getFollowingList(), product);

        detailForm.setUserAction(isLike, isWish, isFollowing); /** 유저의 좋아요, 찜, 팔로우여부 **/
        product.addViews();
        return detailForm;
    }

    private boolean checkIsLikes(List<Likes> userLikesList, Product product) {
        boolean isLike = false;
        for (Likes likes : userLikesList) {
            if (likes.getProduct().getId().equals(product.getId())) {
                isLike = true;
                break;
            }
        }
        return isLike;
    }

    private boolean checkIsWish(User user, List<ProductWish> productWishList) {
        boolean isWish = false;
        for (ProductWish productWish : productWishList) {
            if(productWish.getUser().getId().equals(user.getId())){
                isWish = true;
                break;
            }
        }
        return isWish;
    }

    private boolean checkIsFollowing(Set<Long> userFollowingList, Product product) {
        boolean isFollowing = false;
        for (Long followingId : userFollowingList) {
            if(product.getUser().getId().equals(followingId)){
                isFollowing = true;
                break;
            }
        }
        return isFollowing;
    }

    @Transactional
    public DetailForm showDetail(Long id){   // 비회원 글 보기
        Product product = productRepository.findByIdFetchPWUser(id).orElseThrow();
        List<String> imagesUrl = getProductImages(product);  /** 이미지 엔티티에서 url만 빼오기 **/
        DetailForm detailForm = DetailForm.builder()
                .id(product.getId())
                .author(product.getAuthor())
                .author_picture(product.getUser().getPicture())
                .univ(product.getUser().getUniv())
                .cert_uni(product.getUser().isCert_uni())
                .cert_celeb(product.getUser().isCert_celeb())
                .followerNum(product.getUser().getFollowerNum())
                .images(imagesUrl)
                .title(product.getTitle())
                .price(product.getPrice())
                .category(product.getCategory())
                .date(product.getCreatedDate().toString())
                .content(product.getContent())
                .suggest(product.isSuggest())
                .likesCnt(product.getLikesCnt())
                .views(product.getViews())
                .wishCnt(product.getWishCnt())
                .chatCnt(product.getChatCnt())
                .width(product.getSize().width)
                .vertical(product.getSize().vertical)
                .height(product.getSize().height)
                .trading(product.isTrading())
                .complete(product.isComplete())
                .build();
        detailForm.setUserAction(false,false,false);
        product.addViews();
        return detailForm;
    }


    @Transactional(readOnly = true)
    public JSONObject showHome(User User){
        JSONObject obj = new JSONObject();
        User user = userRepository.findByEmailFetchFollowingAndLikesList(User.getEmail()).orElseThrow();
        List<Product> productList = productRepository.findAll();

        obj.put("new", get3NewList(user.getLikesList(), productList));   /** 신작 3개 **/

        obj.put("recommend", getRecommendListLimitCount(user, HOME_OBJECTS)); /** 추천목록 3개 **/

        obj.put("following", getFollowingListLimitCount(user, productList ,HOME_OBJECTS)); /** 팔로잉 관련 3개 **/
        return obj;
    }

    @Transactional(readOnly = true)
    public JSONObject showHome(){
        JSONObject obj = new JSONObject();

        List<Product> productList = productRepository.findAll();
        List<Product> recentList = productList;

        List<ShowForm> newList = get3ProductForNonUser(productList);
        obj.put("new",newList);

        productList.sort((o1, o2) -> o2.getLikesCnt() - o1.getLikesCnt()); /** hot : 좋아요 순 정렬!! **/
        List<ShowForm> hotList = get3ProductForNonUser(productList);
        obj.put("hot",hotList);


        List<ShowForm> tradingList = get3TradingList(recentList); /** Trading = 최신 목록 내림차순 중 채팅수가 1이상, 거래 완료되지 않은 것 **/
        obj.put("trading",tradingList); // TODO 교체 예정

        return obj;
    }

    private List<ShowForm> get3ProductForNonUser(List<Product> productList) {
        List<ShowForm> newList = new ArrayList<>();  /** 신작 3개 **/
        for (int i = 0; i < productList.size(); i++) {
            if (i >= HOME_OBJECTS)
                break;
            addProductInJSONFormat(newList, productList.get(i), false);
        }
        return newList;
    }

    private List<ShowForm> get3TradingList(List<Product> productList) {
        List<ShowForm> tradingList = new ArrayList<>();   /** 지금 거래 중 (홈) **/
        int cnt = 0;
        for (Product product : productList) {
            if(product.getChatCnt()>=1 && !product.isComplete()){
                addProductInJSONFormat(tradingList, product, false);
                cnt++;
                if(cnt >= HOME_OBJECTS)  /** 홈화면이니까 3개까지만 **/
                    break;
            }
        }
        return tradingList;
    }


    @Transactional(readOnly = true)
    public List<ShowForm> showRecommendDetail(User User){
        User user = userRepository.findByEmailFetchLikesList(User.getEmail()).orElseThrow();
        List<Product> tempRecommendList = getRecommendListLimitCount(user, HOME_DETAIL_OBJECTS);
        List<ShowForm> recommendList = getShowFormCheckIsLikes(user.getLikesList(), tempRecommendList);
        return recommendList;
    }

    @Transactional(readOnly = true)
    public List<ShowForm> showFollowingDetail(User User){
        User user = userRepository.findByEmailFetchFollowingAndLikesList(User.getEmail()).orElseThrow();
        List<Product> productList = productRepository.findAll();

        List<Product> followingList = getFollowingListLimitCount(user, productList, HOME_DETAIL_OBJECTS);
        return getShowFormCheckIsLikes(user.getLikesList(), followingList); /** TODO 50개로 추려야됨 **/

    }

    @Transactional
    public JSONObject wish(User User, @RequestBody ActionForm form){   // 찜
        JSONObject obj = new JSONObject();
        User user = userRepository.findByEmailFetchProductWishList(User.getEmail()).orElseThrow(); // 작품 찜까지 페치 조인
        List<ProductWish> userWishList = user.getProductWishList(); //userWishList == 유저의 찜 리스트
        boolean isWish=false;
        Optional<Product> Product = productRepository.findById(form.getId());
        if(Product.isPresent()){
            Product product = Product.get();
            if(userWishList.size()!=0){ /** 유저가 찜이 누른 적이 있다면 이미 누른 작품인지 비교 **/
                for (ProductWish wish : userWishList) { //유저의 찜목록과 현재 누른 작품의 찜과 비교
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
                for (ProductWish wish : userWishList) {
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
        return PropertyUtil.responseMessage("존재하지 않는 작품에 요청된 찜");
    }

    @Transactional
    public JSONObject likes(User User, @RequestBody ActionForm form){   // 좋아요
        JSONObject obj = new JSONObject();
        User user = userRepository.findByEmailFetchLikesList(User.getEmail()).orElseThrow(); // 작품 좋아요까지 페치 조인
        List<Likes> userLikesList = user.getLikesList(); //userLikesList == 유저의 좋아요 리스트
        boolean isLike=false;
        Optional<Product> Product = productRepository.findById(form.getId());
        if(Product.isPresent()){
            Product product = Product.get();
            if(userLikesList.size()!=0){ /** 유저가 좋아요룰 누른 적이 있다면 이미 누른 작품인지 비교 **/
                for (Likes like : userLikesList) { //유저의 찜목록과 현재 누른 작품의 찜과 비교
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
                for (Likes like : userLikesList) { //유저의 찜목록과 현재 누른 작품의 찜과 비교
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
        return PropertyUtil.responseMessage("존재하지 않는 작품에 요청된 좋아요");
    }

    @Transactional
    public JSONObject trading(@RequestBody ActionForm form){   // 거래중
        JSONObject obj = new JSONObject();
        boolean isTrading;
        Optional<Product> Product = productRepository.findById(form.getId());
        if(Product.isPresent()){
            Product product = Product.get();
            isTrading = product.isTrading();
            if (form.isMode() && !isTrading){
                product.setTrading(true);
                isTrading=true;
                obj.put("success",true);
            }
            else if(!form.isMode() && isTrading){
                product.setTrading(false);
                obj.put("success",true);
            }
            else
                obj.put("success",false);
            obj.put("isTrading",isTrading);
            return obj;
        }
        return PropertyUtil.responseMessage("존재하지 않는 작품에 요청된 좋아요");
    }

    @Transactional
    public JSONObject sell(User User, @RequestBody SellDto dto){   // 판매완료시
        User user = userRepository.findByEmailFetchProductSellList(User.getEmail()).orElseThrow();
        Product product = productRepository.findById(dto.getProductId()).orElseThrow();
        ProductSell connect = ProductSell.createConnect(product, user);
        productSellRepository.save(connect);
        return PropertyUtil.response(true);
    }

    @Transactional(readOnly = true)
    public PageImpl<ShowForm> productListForUser(User User, List<String> categories, String align, Pageable pageable){
        User user  = userRepository.findByEmailFetchLikesList(User.getEmail()).orElseThrow();
        Page<Product> productList;
        if(categories.size()==0)
            productList = productRepository.findAllPopularityDesc(pageable);
        else
            productList = categoryFilter(categories, pageable);  //파라미터 입력받았을 경우
        List<ShowForm> showList = getShowFormCheckIsLikes(user.getLikesList(), productList.getContent());
        standardAlign(align, showList);  /** 선택한 기준대로 정렬 **/
        return new PageImpl<>(showList, pageable, productList.getTotalElements());
    }

    @Transactional(readOnly = true)
    public PageImpl<ShowForm> productListForGuest(List<String> stacks, String align, Pageable pageable){
        Page<Product> productList;
        if(stacks.size()==0)
            productList = productRepository.findAllPopularityDesc(pageable);
        else
            productList = categoryFilter(stacks, pageable);  //파라미터 입력받았을 경우

        List<ShowForm> showList = new ArrayList<>();
        for (Product product : productList.getContent()) {
            addProductInJSONFormat(showList, product, false);
        }
        standardAlign(align, showList);  /** 선택한 기준대로 정렬 **/
        return new PageImpl<>(showList, pageable, productList.getTotalElements());
    }

    private List<String> getProductImages(Product product) {
        List<String> imagesUrl = new ArrayList<>();
        for (ProductImage image : product.getImages()) {
            imagesUrl.add(image.getImageUrl());  /** 이미지 엔티티에서 url만 빼오기 **/
        }
        return imagesUrl;
    }

    private Page<Product> categoryFilter(List<String> categories, Pageable pageable) {
        if (categories.size() == 1)
            return productRepository.findBy1StacksDesc(pageable, categories.get(0));
        else if (categories.size() == 2)
            return productRepository.findBy2StacksDesc(pageable, categories.get(0), categories.get(1));
        else if (categories.size() == 3)
            return productRepository.findBy3StacksDesc(pageable, categories.get(0), categories.get(1), categories.get(2));
        else
            return productRepository.findAll(pageable);
    }

    private void standardAlign(String align, List<ShowForm> showList) {
        if (align.equals("recommend")) {
              /** 신작 추천순 popularity 내림차순 **/
        } else if (align.equals("popular")) { /** 인기순 **/
            showList.sort((o1, o2) -> o2.getLikesCnt() - o1.getLikesCnt());
        } else if (align.equals("recent")) {
            showList.sort((o1, o2) -> (int) (o2.getId() - o1.getId()));
        } else if (align.equals("low")) {
            showList.sort((o1, o2) -> o1.getPrice() - o2.getPrice());
        } else if (align.equals("high")) {
            showList.sort((o1, o2) -> o2.getPrice() - o1.getPrice());
        }
    }


    private List<Product> getRecommendListLimitCount(User user, int count) {
        List<Product> tempRecommendList; /** 카테고리 관련 **/
        String[] categories = user.getCategoryLike().split(",");
        if(categories.length == 1)
            tempRecommendList = productRepository.find1RecommendLimit(categories[0], count);
        else if(categories.length == 2)
            tempRecommendList = productRepository.find2RecommendLimit(categories[0],categories[1], count);
        else if(categories.length == 3)
            tempRecommendList = productRepository.find3RecommendLimit(categories[0],categories[1],categories[2], count);
        else
            tempRecommendList = productRepository.findAll();
        return tempRecommendList;
    }

    private List<ShowForm> getShowFormCheckIsLikes(List<Likes> userLikesList, List<Product> productList) {
        List<ShowForm> showFormList = new ArrayList<>();
        for (Product product : productList) { /** 추천 목록 중 좋아요 누른거 체크 후 ShowForm 으로 담기 **/
            boolean isLike = checkIsLikes(userLikesList, product);
            addProductInJSONFormat(showFormList, product, isLike);
        }
        return showFormList;
    }

    private void addProductInJSONFormat(List<ShowForm> showFormList, Product product, boolean isLike) {
        ShowForm showForm = new ShowForm(product.getId(), product.getTitle(), product.getContent(), product.getAuthor(), product.getPrice(), product.getThumbnail(), product.getCreatedDate().toString(), product.isSuggest(), isLike, product.getLikesCnt(), product.isComplete(), product.getPopularity());
        showFormList.add(showForm);
    }

    private List<ShowForm> get3NewList(List<Likes> userLikesList, List<Product> productList) {
        List<ShowForm> newList = new ArrayList<>();
        for (int i = 0; i < productList.size(); i++) {
            if(i == HOME_OBJECTS)
                break;  /** 홈화면이니까 3개까지만 가져오자 **/
            boolean isLike = checkIsLikes(userLikesList, productList.get(i));
            addProductInJSONFormat(newList, productList.get(i), isLike);
        }
        return newList;
    }

    private List<Product> getFollowingListLimitCount(User user, List<Product> productList, int limit) {
        int count = 0;
        List<Product> followingProductList = new ArrayList<>();
        for (Product product : productList) {
            if(checkIsFollowing(user.getFollowingList(), product)){
                followingProductList.add(product);
                count++;
                if(count>=limit) /** 표시할 개수 충족 **/
                    break;
            }
        }
        followingProductList.sort((o1, o2) -> (int) (o2.getId() - o1.getId()));  //내림차순 정렬
        return followingProductList;
    }

}
