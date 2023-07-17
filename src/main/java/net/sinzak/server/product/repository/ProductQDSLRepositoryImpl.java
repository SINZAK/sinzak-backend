package net.sinzak.server.product.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import net.sinzak.server.product.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static net.sinzak.server.product.domain.QProduct.product;


@Repository
@RequiredArgsConstructor
public class ProductQDSLRepositoryImpl {

    private final JPAQueryFactory queryFactory;

    public Page<Product> findAllByCompleteAndCategoriesAligned(boolean complete, String keyword, List<String> categories, String align, Pageable pageable) {
        List<Product> result = queryFactory
                .selectFrom(product)
                .where(eqComplete(complete), eqCategories(categories), eqSearch(keyword), product.isDeleted.eq(false))
                .orderBy(standardAlign(align))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        JPAQuery<Long> countQuery = queryFactory
                .select(product.count())
                .from(product)
                .where(eqComplete(complete));
        return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
        //PageableExecutionUtils.getPage로 맨 첫 페이지 content 개수가 size 미달이거나, 마지막 page인 경우 count query 실행 X하여 최적화
    }

    private BooleanExpression eqComplete(boolean complete) { //complete 가 true면   where complete = false 로 가져온다.
        if (!complete) {
            return null;
        }
        return product.complete.eq(false);
    }

    private OrderSpecifier<? extends Number> standardAlign(String align) {
        if (align.equals("recommend"))
            return product.popularity.desc();
        else if (align.equals("popular"))
            return product.likesCnt.desc();
        else if (align.equals("recent"))
            return product.id.desc();
        else if (align.equals("low"))
            return product.price.asc();
        else if (align.equals("high"))
            return product.price.desc();

        return product.id.desc();
    }

    private BooleanBuilder eqSearch(String keyword) {
        BooleanBuilder builder = new BooleanBuilder();
        if (keyword.isEmpty())
            return null;
        return builder.or(product.title.contains(keyword))
                .or(product.content.contains(keyword));
    }

    private BooleanBuilder eqCategories(List<String> categories) {
        BooleanBuilder builder = new BooleanBuilder();

        for (String category : categories) {
            if (category != null)
                builder.or(product.category.contains(category));
        }
        return builder;
    }

    public List<Product> findCountByCategoriesDesc(List<String> categories, int count) {
        return queryFactory
                .selectFrom(product)
                .where(eqCategories(categories)
                        .and(product.isDeleted.eq(false)))
                .orderBy(product.id.desc())
                .limit(count)
                .fetch();
    }

}
