package net.sinzak.server.product.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sinzak.server.alarm.service.AlarmService;
import net.sinzak.server.common.PostService;
import net.sinzak.server.common.SinzakResponse;
import net.sinzak.server.common.redis.RedisService;
import net.sinzak.server.common.UserUtils;
import net.sinzak.server.user.domain.Report;
import net.sinzak.server.user.domain.Role;
import net.sinzak.server.user.domain.SearchHistory;
import net.sinzak.server.common.dto.SuggestDto;
import net.sinzak.server.common.error.UserNotFoundException;
import net.sinzak.server.common.error.PostNotFoundException;
import net.sinzak.server.image.S3Service;
import net.sinzak.server.product.domain.*;
import net.sinzak.server.product.dto.*;
import net.sinzak.server.product.repository.*;
import net.sinzak.server.user.domain.User;
import net.sinzak.server.user.domain.embed.Size;
import net.sinzak.server.common.dto.ActionForm;
import net.sinzak.server.user.domain.follow.Follow;
import net.sinzak.server.user.repository.ReportRepository;
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

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService implements PostService<Product,ProductPostDto,ProductWish,ProductLikes,ProductImage> {
    private final UserUtils userUtils;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final ProductRepository productRepository;
    private final ProductSellRepository productSellRepository;
    private final ProductSuggestRepository suggestRepository;
    private final ProductWishRepository productWishRepository;
    private final ProductImageRepository imageRepository;
    private final ProductLikesRepository likesRepository;
    private final S3Service s3Service;
    private final ProductQDSLRepositoryImpl QDSLRepository;
    private final SearchHistoryRepository historyRepository;
    private final AlarmService alarmService;
    private final RedisService redisService;


    private final int HistoryMaxCount = 10;
    private final int HOME_OBJECTS = 10;
    private final int HOME_DETAIL_OBJECTS = 50;

//    @CacheEvict(value = {"home_user","home_guest"}, allEntries = true)
    @Transactional(rollbackFor = {Exception.class})
    public JSONObject makePost(@Valid ProductPostDto buildDto){   // 글 생성
        User user = userRepository.findByIdFetchProductPostList(userUtils.getCurrentUserId()).orElseThrow(UserNotFoundException::new);
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
        product.setUser(user);
        Long productId = productRepository.save(product).getId();// 미리 저장해야 이미지도 저장가능..
        return SinzakResponse.success(productId);
    }

    public JSONObject saveImageInS3AndProduct(List<MultipartFile> multipartFiles, Long id) {
        Product product = productRepository.findById(id).orElseThrow(PostNotFoundException::new);
        if(multipartFiles.size() == 0)
            return SinzakResponse.error("사진 1개이상 첨부해주세요.");
        if(!userUtils.getCurrentUserId().equals(product.getUser().getId()))
            return SinzakResponse.error("잘못된 접근입니다.");
        for (MultipartFile img : multipartFiles) {
            try{
                String url = uploadImageAndSetThumbnail(multipartFiles, product, img);
                saveImageUrl(product, url);
            }
            catch (Exception e){
                return SinzakResponse.error(multipartFiles.indexOf(img)+"번째 이미지부터 저장 실패");
            }
        }
        return SinzakResponse.success();
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
    public JSONObject deleteImage(Long productId, String url){
        Product product = productRepository.findByIdFetchImages(productId).orElseThrow(PostNotFoundException::new);
        if(!userUtils.getCurrentUserId().equals(product.getUser().getId()))
            return SinzakResponse.error("해당 작품의 작가가 아닙니다.");
        if(product.getImages().size()==1)
            return SinzakResponse.error("최소 1개 이상의 이미지를 보유해야 합니다.");

        for (ProductImage image : product.getImages()) {
            if(image.getImageUrl().equals(product.getThumbnail()))
                return SinzakResponse.error("썸네일은 삭제 불가능합니다.");
            if(image.getImageUrl().equals(url)){
                imageRepository.delete(image);
                product.getImages().remove(image);
                break;
            }
        }
        s3Service.deleteImage(url);
        return SinzakResponse.success(productId);
    }

    @Transactional(rollbackFor = {Exception.class})
//    @CacheEvict(value = {"home_user","home_guest"}, allEntries = true)
    public JSONObject editPost(Long productId, ProductEditDto editDto){
        User user = userUtils.getCurrentUser();
        Product product = productRepository.findById(productId).orElseThrow(PostNotFoundException::new);
        if(!user.getId().equals(product.getUser().getId()) && user.getRole() != Role.ADMIN)
            return SinzakResponse.error("글 작성자가 아닙니다.");

        product.editPost(editDto);
        productRepository.save(product);
        return SinzakResponse.success();
    }

    @Transactional(rollbackFor = {Exception.class})
//    @CacheEvict(value = {"home_user","home_guest"}, allEntries = true)
    public JSONObject deletePost(Long productId){
        User user = userUtils.getCurrentUser();
        Product product = productRepository.findByIdFetchChatRooms(productId).orElseThrow(PostNotFoundException::new);
        if(!user.getId().equals(product.getUser().getId()) && user.getRole() != Role.ADMIN)
            return SinzakResponse.error("글 작성자가 아닙니다.");
        deleteImagesInPost(product);
        product.setDeleted(true);
        return SinzakResponse.success();
    }

    private void deleteImagesInPost(Product product) {
        product.getImages()
                .forEach(img -> s3Service.deleteImage(img.getImageUrl()));
    }


    @Transactional
    public JSONObject showDetailForUser(Long currentUserId, Long id){   // 글 상세 확인
        User user = userRepository.findByIdFetchFollowingAndLikesList(currentUserId).orElseThrow(UserNotFoundException::new);
        Product product = productRepository.findByIdFetchProductWishAndUser(id).orElseThrow(PostNotFoundException::new);
        List<ProductImage> images = imageRepository.findByProductId(product.getId());
        DetailProductForm detailForm = makeProductDetailForm(product, images);
        if(!product.getUser().isDelete()){
            User postUser = product.getUser();
            detailForm.setUserInfo(postUser.getId(),postUser.getNickName(),postUser.getPicture(),postUser.getUniv(),postUser.isCert_uni(),postUser.isCert_author(), postUser.getFollowerNum());
        }
        else
            detailForm.setUserInfo(null, "탈퇴한 회원", null, "??", false, false, "0");

        if(user.getId().equals(product.getUser().getId()))
            detailForm.setMyPost();

        boolean isLike = checkIsLikes(user.getProductLikesList(), product);
        boolean isWish = checkIsWish(user, product.getProductWishes());  /**최적화를 위해 상품의 찜목록과 비교함. (대체적으로 해당 상품 찜개수 < 유저의 찜개수) **/
        boolean isFollowing = false;
        if(!product.getUser().isDelete())
            isFollowing = checkIsFollowing(user.getFollowings(), product);

        detailForm.setUserAction(isLike, isWish, isFollowing);
        product.addViews();
        return SinzakResponse.success(detailForm);
    }

    @Transactional
    public JSONObject showDetailForGuest(Long id){   // 비회원 글 보기
        Product product = productRepository.findByIdFetchImages(id).orElseThrow(PostNotFoundException::new);
        DetailProductForm detailForm = makeProductDetailForm(product, product.getImages());
        if(!product.getUser().isDelete()){
            User postUser = product.getUser();
            detailForm.setUserInfo(postUser.getId(),postUser.getNickName(),postUser.getPicture(),postUser.getUniv(),postUser.isCert_uni(),postUser.isCert_author(), postUser.getFollowerNum());
        }
        else{
            detailForm.setUserInfo(null, "탈퇴한 회원", null, "??", false, false, "0");
        }
        detailForm.setUserAction(false,false,false);
        product.addViews();
        return SinzakResponse.success(detailForm);
    }

    private DetailProductForm makeProductDetailForm(Product product, List<ProductImage> images) {
        return DetailProductForm.builder()
                .id(product.getId())
                .author(product.getAuthor())
                .images(getImages(images))
                .title(product.getTitle())
                .price(product.getPrice())
                .topPrice(product.getTopPrice())
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
                .complete(product.isComplete()).build();
    }

    public boolean checkIsLikes(List<ProductLikes> userLikesList, Product product) {
        return userLikesList.stream().anyMatch(x -> x.getProduct().getId().equals(product.getId()));
    }

    public boolean checkIsWish(User user, List<ProductWish> productWishList) {
        return productWishList.stream().anyMatch(x -> x.getUser().getId().equals(user.getId()));
    }

    public boolean checkIsFollowing(Set<Follow> followings, Product product) {
        return followings.stream()
                .map(Follow::getFollowingUser)
                .map(User::getId)
                .anyMatch(x -> x.equals(product.getUser().getId()));
    }


//    @Cacheable(value ="home_user", key = "#userId", cacheManager ="testCacheManager")
    @Transactional(readOnly = true)
    public JSONObject showHomeForUser(Long userId){
        JSONObject obj = new JSONObject();
        User user = userRepository.findByIdFetchFollowingAndLikesList(userId).orElseThrow(UserNotFoundException::new);
        List<String> userCategories = Arrays.asList(user.getCategoryLike().split(","));
        List<Report> userReports = reportRepository.findByUserId(userId);
        List<Product> productList = productRepository.findAllProductNotDeleted();

        removeBlockPost(userReports, productList);

        obj.put("new", makeHomeShowForms(user.getProductLikesList(), productList));   /** 신작 **/

        List<Product> list = QDSLRepository.findCountByCategoriesDesc(userCategories, HOME_OBJECTS);
        obj.put("recommend", makeHomeShowForms(user.getProductLikesList(), list)); /** 추천목록  **/

        List<Product> followings = getFollowingList(user, productList, HOME_OBJECTS);
        obj.put("following", makeHomeShowForms(user.getProductLikesList(),followings)); /** 팔로잉 **/

        return SinzakResponse.success(obj);
    }

//    @Cacheable(value ="home_guest", cacheManager ="testCacheManager")
    @Transactional(readOnly = true)
    public JSONObject showHomeForGuest(){
        JSONObject obj = new JSONObject();
        List<Product> products = productRepository.findAllProductNotDeleted();

        obj.put("new", makeHomeShowFormsForGuest(products));

        List<Product> tradingList = getTradingList(products, HOME_OBJECTS); /** Trading = 최신 목록 내림차순 중 채팅수가 1이상, 거래 완료되지 않은 것 **/
        obj.put("trading", makeHomeShowFormsForGuest(tradingList));

        products.sort((o1, o2) -> o2.getLikesCnt() - o1.getLikesCnt()); /** hot : 좋아요 순 정렬!! **/
        obj.put("hot", makeHomeShowFormsForGuest(products));

        return SinzakResponse.success(obj);
    }

    private List<Product> getTradingList(List<Product> productList, int limit) {
        List<Product> tradingList = productList.stream()
                .filter(product -> product.getChatCnt()>=1 && !product.isComplete())
                .limit(limit)
                .collect(Collectors.toList());
        return tradingList;
    }

    private List<Product> getFollowingList(User user, List<Product> productList, int limit) {
        List<Product> followingProductList = productList.stream()
                .filter(product -> checkIsFollowing(user.getFollowings(), product))
                .limit(limit)
                .collect(Collectors.toList());
        return followingProductList;
    }

    @Transactional
    public JSONObject wish(@RequestBody ActionForm form){
        JSONObject obj = new JSONObject();
        User user = userRepository.findByIdFetchProductWishList(userUtils.getCurrentUserId()).orElseThrow(UserNotFoundException::new); // 작품 찜까지 페치 조인
        List<ProductWish> wishList = user.getProductWishes(); //wishList == 유저의 찜 리스트
        boolean success = false;
        boolean isWish=false;
        Product product = productRepository.findById(form.getId()).orElseThrow(PostNotFoundException::new);

        if(wishList.size()!=0){
            if(wishList.stream().anyMatch(wish -> wish.getProduct().equals(product)))
                isWish = true;
        }

        if (form.isMode() && !isWish){
            product.plusWishCnt();
            ProductWish connect = ProductWish.createConnect(product, user);
            productWishRepository.save(connect);
            isWish = true;
            success = true;
        }
        else if(!form.isMode() && isWish){
            product.minusWishCnt();
            wishList.stream()
                    .filter(wish -> wish.getProduct().equals(product)).findFirst()
                    .ifPresent(productWishRepository::delete);
            success = true;
        }

        obj.put("isWish", isWish);
        obj.put("success",success);
        return obj;
    }

    @Transactional
//    @CacheEvict(value = {"home_user","home_guest"}, allEntries = true)
    public JSONObject likes(@RequestBody ActionForm form){
        JSONObject obj = new JSONObject();
        User user = userRepository.findByIdFetchLikesList(userUtils.getCurrentUserId()).orElseThrow(UserNotFoundException::new);
        List<ProductLikes> likesList = user.getProductLikesList();
        boolean success = false;
        boolean isLike = false;
        Product product = productRepository.findById(form.getId()).orElseThrow(PostNotFoundException::new);

        if(likesList.size()!=0){
            if(likesList.stream().anyMatch(likes -> likes.getProduct().equals(product)))
                isLike = true;
        }

        if (form.isMode() && !isLike){
            product.plusLikesCnt();
            ProductLikes connect = ProductLikes.createConnect(product, user);
            likesRepository.save(connect);
            isLike = true;
            success = true;
        }
        else if(!form.isMode() && isLike){
            product.minusLikesCnt();
            likesList.stream()
                    .filter(likes -> likes.getProduct().equals(product)).findFirst()
                    .ifPresent(likesRepository::delete);
            success = true;
        }
        obj.put("isLike", isLike);
        obj.put("success", success);
        return obj;
    }

    @Transactional
//    @CacheEvict(value = {"home_user","home_guest"}, allEntries = true)
    public JSONObject sell(@RequestBody SellDto dto){
        User user = userRepository.findByIdFetchProductSellList(userUtils.getCurrentUserId()).orElseThrow(UserNotFoundException::new);
        Product product = productRepository.findById(dto.getPostId()).orElseThrow(PostNotFoundException::new);
        if(product.isComplete())
            return SinzakResponse.error("이미 판매완료된 작품입니다.");
        ProductSell connect = ProductSell.createConnect(product, user);
        productSellRepository.save(connect);
        product.setComplete(true);
        return SinzakResponse.success();
    }

    @Transactional
    public JSONObject suggest(@RequestBody SuggestDto dto){
        User user = userUtils.getCurrentUser();
        if(suggestRepository.findByUserIdAndProductId(user.getId(),dto.getId()).isPresent())
            return SinzakResponse.error("이미 제안을 하신 작품입니다.");
        Product product = productRepository.findByIdFetchUser(dto.getId()).orElseThrow();
        ProductSuggest connect = ProductSuggest.createConnect(product, user);
        product.setTopPrice(dto.getPrice());
//        alarmService.makeAlarm(product.getUser(),product.getThumbnail(),product.getId().toString(),Integer.toString(dto.getPrice()));
        suggestRepository.save(connect);
        return SinzakResponse.success();
    }

    @Transactional
    public PageImpl<ShowForm> productsForUser(String keyword, List<String> categories, String align, boolean complete, Pageable pageable){
        User user  = userRepository.findByIdFetchHistoryAndLikesList(userUtils.getCurrentUserId()).orElseThrow(UserNotFoundException::new);
        List<Report> userReports = reportRepository.findByUserId(user.getId());
        if(!keyword.isEmpty()){
            saveSearchHistory(keyword, user);
            redisService.addWordToRedis(keyword);
        }

        Page<Product> productList = QDSLRepository.findAllByCompleteAndCategoriesAligned(complete, keyword, categories, align, pageable);
        List<Product> products = new ArrayList<>(productList.getContent()); // Page의 content를 필터링 할 수 없어서 재생성.
        removeBlockPost(userReports, products);
        List<ShowForm> showList = makeDetailHomeShowForms(user.getProductLikesList(), products);
        return new PageImpl<>(showList, pageable, products.size());
    }


    private void saveSearchHistory(String keyword, User user) {
        List<SearchHistory> historyList = new ArrayList<>(user.getHistories());
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
    public PageImpl<ShowForm> productsForGuest(String keyword, List<String> categories, String align, boolean complete, Pageable pageable){
        if(!keyword.isEmpty()) redisService.addWordToRedis(keyword);
        Page<Product> productList = QDSLRepository.findAllByCompleteAndCategoriesAligned(complete, keyword, categories, align, pageable);
        List<ShowForm> showList = makeShowFormsForGuest(productList);
        return new PageImpl<>(showList, pageable, productList.getTotalElements());
    }

    @NotNull
    private List<ShowForm> makeShowFormsForGuest(Page<Product> productList) {
        return productList.stream()
                .map(product -> makeShowForm(product, false))
                .collect(Collectors.toList());
    }

    public List<String> getImages(List<ProductImage> images) {
        return images.stream()
                .map(ProductImage::getImageUrl).collect(Collectors.toList());
    }

    private List<ShowForm> makeHomeShowForms(List<ProductLikes> userLikesList, List<Product> productList) {
        return productList.stream()
                .map(product -> makeShowForm(product, checkIsLikes(userLikesList, product))).limit(HOME_OBJECTS)
                .collect(Collectors.toList());
    }

    private List<ShowForm> makeHomeShowFormsForGuest(List<Product> productList) {
        return productList.stream()
                .map(product -> makeShowForm(product, false)).limit(HOME_OBJECTS)
                .collect(Collectors.toList());
    }

    private List<ShowForm> makeDetailHomeShowForms(List<ProductLikes> userLikesList, List<Product> productList) {
        return productList.stream()
                .map(product -> makeShowForm(product, checkIsLikes(userLikesList, product)))
                .collect(Collectors.toList());
    }
    private ShowForm makeShowForm(Product product, boolean isLike) {
        return ShowForm.builder()
                .id(product.getId())
                .title(product.getTitle())
                .content(product.getContent())
                .author(product.getAuthor())
                .price(product.getPrice())
                .thumbnail(product.getThumbnail())
                .date(product.getCreatedDate().toString())
                .suggest(product.isSuggest())
                .like(isLike)
                .likesCnt(product.getLikesCnt())
                .complete(product.isComplete())
                .popularity(product.getPopularity()).build();
    }

    private void removeBlockPost(List<Report> userReportList, List<Product> productList) {
        for (int i = 0; i<productList.size(); i++){
            for (Report report : userReportList) {
                if(productList.get(i).getUser().getId().equals(report.getOpponentUser().getId())){
                    productList.remove(productList.get(i));
                }
            }
        }
    }

    public List<String> getCompleteWord(String keyWord) {
        return redisService.getAutoCompleteWords(keyWord);
    }
}
