package net.sinzak.server.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sinzak.server.common.PostService;
import net.sinzak.server.user.domain.SearchHistory;
import net.sinzak.server.common.dto.SuggestDto;
import net.sinzak.server.common.error.InstanceNotFoundException;
import net.sinzak.server.common.error.UserNotFoundException;
import net.sinzak.server.image.S3Service;
import net.sinzak.server.product.domain.*;
import net.sinzak.server.common.PropertyUtil;
import net.sinzak.server.product.dto.*;
import net.sinzak.server.product.repository.*;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.user.domain.embed.Size;
import net.sinzak.server.common.dto.ActionForm;
import net.sinzak.server.user.repository.SearchHistoryRepository;
import net.sinzak.server.user.repository.UserRepository;
import org.jetbrains.annotations.NotNull;
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
public class ProductService implements PostService<Product,ProductPostDto,ProductWish,ProductLikes> {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductSellRepository productSellRepository;
    private final ProductSuggestRepository suggestRepository;
    private final ProductWishRepository productWishRepository;
    private final ProductImageRepository imageRepository;
    private final ProductLikesRepository likesRepository;
    private final S3Service s3Service;
    private final ProductQDSLRepositoryImpl QDSLRepository;
    private final SearchHistoryRepository historyRepository;

    private final static int HistoryMaxCount = 10;
    private final int HOME_OBJECTS = 10;
    private final int HOME_DETAIL_OBJECTS = 50;

    @Transactional(rollbackFor = {Exception.class})
    public JSONObject makePost(User User, ProductPostDto buildDto){   // 글 생성
        User user = userRepository.findByEmailFetchProductPostList(User.getEmail()).orElseThrow(UserNotFoundException::new);
        Product product = Product.builder()
                    .title(buildDto.getTitle())
                    .content(buildDto.getContent())
                    .category(buildDto.getCategory())
                    .author(user.getNickName())
                    .univ(user.getUniv())
                    .price(buildDto.getPrice())
                    .suggest(buildDto.isSuggest())
                    .size(new Size(buildDto.getWidth(), buildDto.getVertical(), buildDto.getHeight()))
                    .build();
        product.setUser(user); // user 연결 및, user의 외주 글 리스트에 글 추가
        Long productId = productRepository.save(product).getId();// 미리 저장해야 이미지도 저장가능..
        return PropertyUtil.response(productId);
    }

    @Transactional(rollbackFor = {Exception.class})
    public JSONObject deleteImage(User User, Long productId, String url){   // 글 생성
        Product product = productRepository.findById(productId).orElseThrow(InstanceNotFoundException::new);
        if(!User.getId().equals(product.getUser().getId()))
            return PropertyUtil.responseMessage("해당 작품의 작가가 아닙니다.");
        if(product.getImages().size()==1)
            return PropertyUtil.responseMessage("최소 1개 이상의 이미지를 보유해야 합니다.");

        for (ProductImage image : product.getImages()) {
//            if(image.getImageUrl().equals(product.getThumbnail()))
//                return PropertyUtil.responseMessage("썸네일은 삭제 불가능합니다."); //TODO 프론트가 어쩔지 보자.
            if(image.getImageUrl().equals(url)){
                imageRepository.delete(image);
                product.getImages().remove(image);
                break;
            }
        }
        s3Service.deleteImage(url);
        return PropertyUtil.response(productId);
    }

    public JSONObject saveImageInS3AndProduct(User user, List<MultipartFile> multipartFiles, Long id) {
        Product product = productRepository.findById(id).orElseThrow(InstanceNotFoundException::new);
        if(!user.getId().equals(product.getUser().getId()))
            return PropertyUtil.responseMessage("잘못된 접근입니다.");
        for (MultipartFile img : multipartFiles) {
            try{
                String url = uploadImageAndSetThumbnail(multipartFiles, product, img);
                saveImageUrl(product, url);
            }
            catch (Exception e){
                return PropertyUtil.responseMessage("이미지 저장 실패");
            }
        }
        return PropertyUtil.response(true);
    }


    private String uploadImageAndSetThumbnail(List<MultipartFile> multipartFiles, Product product, MultipartFile img) {
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

    @Transactional(rollbackFor = {Exception.class})
    public JSONObject editPost(User User, Long productId, ProductEditDto editDto){   // 글 생성
        User user = userRepository.findByEmail(User.getEmail()).orElseThrow(UserNotFoundException::new);
        Product product = productRepository.findById(productId).orElseThrow(InstanceNotFoundException::new);
        if(!user.getId().equals(product.getUser().getId()))
            return PropertyUtil.responseMessage("글 작성자가 아닙니다.");

        product.editPost(editDto);
        productRepository.save(product);
        return PropertyUtil.response(true);
    }

    @Transactional(rollbackFor = {Exception.class})
    public JSONObject deletePost(User User, Long productId){   // 글 생성
        User user = userRepository.findByEmail(User.getEmail()).orElseThrow(UserNotFoundException::new);
        Product product = productRepository.findById(productId).orElseThrow(InstanceNotFoundException::new);
        if(!user.getId().equals(product.getUser().getId()))
            return PropertyUtil.responseMessage("글 작성자가 아닙니다.");
        deleteImagesInPost(product);
        productRepository.delete(product);
        return PropertyUtil.response(true);
    }

    private void deleteImagesInPost(Product product) {
        for (ProductImage image : product.getImages()) {
            s3Service.deleteImage(image.getImageUrl());
        }
    }

    @Transactional
    public JSONObject showDetail(Long id, User User){   // 글 상세 확인
        User user = userRepository.findByEmailFetchFollowingAndLikesList(User.getEmail()).orElseThrow(UserNotFoundException::new);
        Product product = productRepository.findByIdFetchProductWishAndUser(id).orElseThrow(InstanceNotFoundException::new);
        DetailProductForm detailForm = DetailProductForm.builder()
                .id(product.getId())
                .userId(product.getUser().getId())
                .author(product.getAuthor())
                .author_picture(product.getUser().getPicture())
                .univ(product.getUser().getUniv())
                .cert_uni(product.getUser().isCert_uni())
                .cert_celeb(product.getUser().isCert_celeb())
                .followerNum(product.getUser().getFollowerNum())
                .images(getImages(product))
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

        boolean isLike = checkIsLikes(user.getProductLikesList(), product);
        boolean isWish = checkIsWish(user, product.getProductWishList());
        boolean isFollowing  = checkIsFollowing(user.getFollowingList(), product);
        detailForm.setUserAction(isLike, isWish, isFollowing);
        product.addViews();
        return PropertyUtil.response(detailForm);
    }

    public boolean checkIsLikes(List<ProductLikes> userLikesList, Product product) {
        boolean isLike = false;
        for (ProductLikes likes : userLikesList) {
            if (likes.getProduct().getId().equals(product.getId())) {
                isLike = true;
                break;
            }
        }
        return isLike;
    }

    public boolean checkIsWish(User user, List<ProductWish> productWishList) {
        boolean isWish = false;
        for (ProductWish productWish : productWishList) {
            if(productWish.getUser().getId().equals(user.getId())){
                isWish = true;
                break;
            }
        }
        return isWish;
    }

    public boolean checkIsFollowing(Set<Long> userFollowingList, Product product) {
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
    public JSONObject showDetail(Long id){   // 비회원 글 보기
        Product product = productRepository.findByIdFetchProductWishAndUser(id).orElseThrow(InstanceNotFoundException::new);
        List<String> imagesUrl = getImages(product);
        DetailProductForm detailForm = DetailProductForm.builder()
                .id(product.getId())
                .userId(product.getUser().getId())
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
        return PropertyUtil.response(detailForm);
    }


    @Transactional(readOnly = true)
    public JSONObject showHome(User User){
        JSONObject obj = new JSONObject();
        User user = userRepository.findByEmailFetchFollowingAndLikesList(User.getEmail()).orElseThrow(UserNotFoundException::new);
        List<String> userCategories = Arrays.asList(user.getCategoryLike().split(","));

        List<Product> productList = productRepository.findAll();
        obj.put("new", makeHomeShowFormList(user.getProductLikesList(), productList));   /** 신작 3개 **/


        List<Product> list = QDSLRepository.findCountByCategoriesDesc(userCategories, HOME_OBJECTS);
        obj.put("recommend", makeHomeShowFormList(user.getProductLikesList(), list)); /** 추천목록 3개 **/


        List<Product> followingList = getFollowingList(user, productList, HOME_OBJECTS);
        obj.put("following", makeHomeShowFormList(user.getProductLikesList(),followingList)); /** 팔로잉 관련 3개 **/

        return PropertyUtil.response(obj);
    }


    @Transactional(readOnly = true)
    public JSONObject showHome(){
        JSONObject obj = new JSONObject();
        List<Product> productList = productRepository.findAll();

        obj.put("new",makeHomeShowFormListForGuest(productList));

        List<Product> tradingList = getTradingList(productList, HOME_OBJECTS); /** Trading = 최신 목록 내림차순 중 채팅수가 1이상, 거래 완료되지 않은 것 **/
        obj.put("trading", makeHomeShowFormListForGuest(tradingList));

        productList.sort((o1, o2) -> o2.getLikesCnt() - o1.getLikesCnt()); /** hot : 좋아요 순 정렬!! **/
        obj.put("hot", makeHomeShowFormListForGuest(productList));

        return PropertyUtil.response(obj);
    }

    private List<Product> getTradingList(List<Product> productList, int limit) {
        int count = 0;
        List<Product> tradingList = new ArrayList<>();   /** 지금 거래 중 (홈) **/
        for (Product product : productList) {
            if(product.getChatCnt()>=1 && !product.isComplete()){
                tradingList.add(product);
                count++;
                if(count >= limit)  /** 홈화면이니까 3개까지만 **/
                    break;
            }
        }
        return tradingList;
    }

    @Transactional(readOnly = true)
    public JSONObject showRecommendDetail(User User){

        User user = userRepository.findByEmailFetchLikesList(User.getEmail()).orElseThrow(UserNotFoundException::new);
        List<String> userCategories = Arrays.asList(user.getCategoryLike().split(","));

        List<Product> recommendList = QDSLRepository.findCountByCategoriesDesc(userCategories, HOME_DETAIL_OBJECTS);
        List<ShowForm> data = makeDetailHomeShowFormList(user.getProductLikesList(), recommendList);

        return PropertyUtil.response(data);
    }

    @Transactional(readOnly = true)
    public JSONObject showFollowingDetail(User User){
        User user = userRepository.findByEmailFetchFollowingAndLikesList(User.getEmail()).orElseThrow(UserNotFoundException::new);
        List<Product> productList = productRepository.findAll();

        List<Product> followingList = getFollowingList(user, productList, HOME_DETAIL_OBJECTS);
        List<ShowForm> data = makeDetailHomeShowFormList(user.getProductLikesList(), followingList);

        return PropertyUtil.response(data);
    }
    private List<Product> getFollowingList(User user, List<Product> productList, int limit) {
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
        return followingProductList;
    }

    @Transactional
    public JSONObject wish(User User, @RequestBody ActionForm form){   // 찜
        JSONObject obj = new JSONObject();
        User user = userRepository.findByEmailFetchProductWishList(User.getEmail()).orElseThrow(UserNotFoundException::new); // 작품 찜까지 페치 조인
        List<ProductWish> wishList = user.getProductWishList(); //wishList == 유저의 찜 리스트
        boolean isWish=false;
        Product product = productRepository.findById(form.getId()).orElseThrow(InstanceNotFoundException::new);

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
            for (ProductWish wish : wishList) {
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

    @Transactional
    public JSONObject likes(User User, @RequestBody ActionForm form){
        JSONObject obj = new JSONObject();
        User user = userRepository.findByEmailFetchLikesList(User.getEmail()).orElseThrow(UserNotFoundException::new);
        List<ProductLikes> userLikesList = user.getProductLikesList();
        boolean isLike=false;
        Optional<Product> Product = productRepository.findById(form.getId());
        if(Product.isPresent()){
            Product product = Product.get();
            if(userLikesList.size()!=0){
                for (ProductLikes like : userLikesList) {
                    if(product.equals(like.getProduct())) {
                        isLike = true;
                        break;
                    }
                }
            }

            if (form.isMode() && !isLike){
                product.plusLikesCnt();
                ProductLikes connect = ProductLikes.createConnect(product, user);
                likesRepository.save(connect);
                isLike=true;
                obj.put("success",true);
            }
            else if(!form.isMode() && isLike){
                product.minusLikesCnt();
                for (ProductLikes like : userLikesList) {
                    if(product.equals(like.getProduct())) {
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
    public JSONObject trading(@RequestBody ActionForm form){
        JSONObject obj = new JSONObject();
        boolean isTrading;
        Product product = productRepository.findById(form.getId()).orElseThrow(InstanceNotFoundException::new);
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

    @Transactional
    public JSONObject sell(User User, @RequestBody SellDto dto){
        User user = userRepository.findByEmailFetchProductSellList(User.getEmail()).orElseThrow(UserNotFoundException::new);
        Product product = productRepository.findById(dto.getProductId()).orElseThrow(InstanceNotFoundException::new);
        ProductSell connect = ProductSell.createConnect(product, user);
        productSellRepository.save(connect);
        return PropertyUtil.response(true);
    }

    @Transactional
    public JSONObject suggest(User User, @RequestBody SuggestDto dto){
        User user = userRepository.findByEmail(User.getEmail()).orElseThrow(UserNotFoundException::new);
        if(suggestRepository.findByUserIdAndProductId(user.getId(),dto.getId()).isPresent())
            return PropertyUtil.responseMessage("이미 제안을 하신 작품입니다.");
        Product product = productRepository.findById(dto.getId()).orElseThrow();
        ProductSuggest connect = ProductSuggest.createConnect(product, user);
        product.setTopPrice(dto.getPrice());
        suggestRepository.save(connect);
        return PropertyUtil.response(true);
    }

    @Transactional
    public PageImpl<ShowForm> productListForUser(User User, String keyword, List<String> categories, String align, boolean complete, Pageable pageable){
        User user  = userRepository.findByEmailFetchHistoryAndLikesList(User.getEmail()).orElseThrow(UserNotFoundException::new);
        if(!keyword.isEmpty())
            saveSearchHistory(keyword, user);
        Page<Product> productList = QDSLRepository.findAllByCompleteAndCategoriesAligned(complete, keyword, categories, align, pageable);

        List<ShowForm> showList = makeDetailHomeShowFormList(user.getProductLikesList(), productList.getContent());
        return new PageImpl<>(showList, pageable, productList.getTotalElements());
    }


    private void saveSearchHistory(String keyword, User user) {
        List<SearchHistory> historyList = new ArrayList<>(user.getHistoryList());
        if(historyList.size() >= HistoryMaxCount)
            historyRepository.delete(historyList.get(0));
        for (SearchHistory history : historyList) {
            if(history.getWord().equals(keyword))
                historyRepository.delete(history);
        }
        SearchHistory history = SearchHistory.addSearchHistory(keyword, user);
            historyRepository.save(history);
    }

    @Transactional(readOnly = true)
    public PageImpl<ShowForm> productListForGuest(String keyword, List<String> categories, String align, boolean complete, Pageable pageable){
        Page<Product> productList = QDSLRepository.findAllByCompleteAndCategoriesAligned(complete, keyword, categories, align, pageable);
        List<ShowForm> showList = makeShowForm(productList);
        return new PageImpl<>(showList, pageable, productList.getTotalElements());
    }

    @NotNull
    private List<ShowForm> makeShowForm(Page<Product> productList) {
        List<ShowForm> showList = new ArrayList<>();
        for (Product product : productList) {
            addProductInJSONFormat(showList, product, false);
        }
        return showList;
    }

    public List<String> getImages(Product product) {
        List<String> imagesUrl = new ArrayList<>();
        for (ProductImage image : product.getImages()) {
            imagesUrl.add(image.getImageUrl());  /** 이미지 엔티티에서 url만 빼오기 **/
        }
        return imagesUrl;
    }


    private void addProductInJSONFormat(List<ShowForm> showFormList, Product product, boolean isLike) {
        ShowForm showForm = new ShowForm(product.getId(), product.getTitle(), product.getContent(), product.getAuthor(), product.getPrice(), product.getThumbnail(), product.getCreatedDate().toString(), product.isSuggest(), isLike, product.getLikesCnt(), product.isComplete(), product.getPopularity());
        showFormList.add(showForm);
    }

    private List<ShowForm> makeHomeShowFormList(List<ProductLikes> userLikesList, List<Product> productList) {
        List<ShowForm> showFormList = new ArrayList<>();
        for (int i = 0; i < productList.size(); i++) {
            if(i == HOME_OBJECTS)
                break;  /** 홈화면이니까 10개까지만 가져오자 **/
            boolean isLike = checkIsLikes(userLikesList, productList.get(i));
            addProductInJSONFormat(showFormList, productList.get(i), isLike);
        }
        return showFormList;
    }

    private List<ShowForm> makeHomeShowFormListForGuest(List<Product> productList) {
        List<ShowForm> newList = new ArrayList<>();  /** 신작 3개 **/
        for (int i = 0; i < productList.size(); i++) {
            if (i >= HOME_OBJECTS)
                break;
            addProductInJSONFormat(newList, productList.get(i), false);
        }
        return newList;
    }

    private List<ShowForm> makeDetailHomeShowFormList(List<ProductLikes> userLikesList, List<Product> productList) {
        List<ShowForm> showFormList = new ArrayList<>();
        for (Product product : productList) { /** 추천 목록 중 좋아요 누른거 체크 후 ShowForm 으로 담기 **/
            boolean isLike = checkIsLikes(userLikesList, product);
            addProductInJSONFormat(showFormList, product, isLike);
        }
        return showFormList;
    }


}
