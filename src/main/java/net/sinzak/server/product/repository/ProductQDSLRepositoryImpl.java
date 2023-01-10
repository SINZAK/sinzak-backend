package net.sinzak.server.product.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import net.sinzak.server.product.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static net.sinzak.server.product.domain.QProduct.product;



@Repository
@RequiredArgsConstructor
public class ProductQDSLRepositoryImpl implements QDSLRepository {

    private final JPAQueryFactory queryFactory;

    public Page<Product> findAllByCompletePopularityDesc(boolean complete, Pageable pageable) {
        List<Product> result = queryFactory
                .selectFrom(product)
                .where(eqComplete(complete))
                .orderBy(product.popularity.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
//        long count = queryFactory.select(product.count())
//                .from(product)
//                .where(eqComplete(complete))
//                .fetchOne();
        JPAQuery<Long> countQuery = queryFactory
                .select(product.count())
                .from(product)
                .where(eqComplete(complete));
        return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne) ;
        //PageableExecutionUtils.getPage로 맨 첫 페이지 content 개수가 size 미달이거나, 마지막 page인 경우 count query 실행 X하여 최적화
    }

    private BooleanExpression eqComplete(boolean complete) { //complete 가 true면   where complete = false 로 가져온다.
        if (!complete){
            return null;
        }
        return product.complete.eq(false);
    }


    public Page<Product> findNByCategoriesDesc(List<String> categories, Pageable pageable) {
        List<Product> result = queryFactory
                .selectFrom(product)
                .where(eqCategories(categories))
                .orderBy(product.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(product.count())
                .from(product)
                .where(eqCategories(categories));
        return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne) ;
        //PageableExecutionUtils.getPage로 맨 첫 페이지 content 개수가 size 미달이거나, 마지막 page인 경우 count query 실행 X하여 !최적화!
    }

    private BooleanBuilder eqCategories(List<String> categories) {
        BooleanBuilder builder = new BooleanBuilder();

        if(categories.get(0) != null){
            builder.or(product.category.contains(categories.get(0)));
        }
        if(categories.get(1) != null){
            builder.or(product.category.contains(categories.get(1)));
        }
        if(categories.get(2) != null){
            builder.or(product.category.contains(categories.get(2)));
        }

        return builder;
    }

}
