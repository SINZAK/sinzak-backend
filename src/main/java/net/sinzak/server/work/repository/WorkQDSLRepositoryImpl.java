package net.sinzak.server.work.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import net.sinzak.server.common.QDSLRepository;
import net.sinzak.server.work.domain.Work;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import static net.sinzak.server.work.domain.QWork.work;


@Repository
@RequiredArgsConstructor
public class WorkQDSLRepositoryImpl implements QDSLRepository<Work> {

    private final JPAQueryFactory queryFactory;

    public Page<Work> findSearchingByEmploymentAndCategoriesAligned(boolean employment, String keyword, List<String> categories, String align, Pageable pageable) {
        List<Work> result = queryFactory
                .selectFrom(work)
                .where(work.employment.eq(employment), eqCategories(categories), eqSearch(keyword),work.isDeleted.eq(false))
                .orderBy(standardAlign(align))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        JPAQuery<Long> countQuery = queryFactory
                .select(work.count())
                .from(work)
                .where(work.employment.eq(employment), eqCategories(categories), eqSearch(keyword));
        return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne) ;
        //PageableExecutionUtils.getPage로 맨 첫 페이지 content 개수가 size 미달이거나, 마지막 page인 경우 count query 실행 X하여 최적화
    }


    private BooleanBuilder eqCategories(List<String> categories) {
        BooleanBuilder builder = new BooleanBuilder();

        for (String category : categories) {
            if(category != null)
                builder.or(work.category.contains(category));
        }

        return builder;
    }


    private BooleanBuilder eqSearch(String keyword) { //complete 가 true면   where complete = false 로 가져온다.
        BooleanBuilder builder = new BooleanBuilder();
        if (keyword.isEmpty()){
            return null;
        }
        return builder.or(work.title.contains(keyword)).or(work.content.contains(keyword));
    }

    private OrderSpecifier<? extends Number> standardAlign(String align) {
        if (align.equals("recommend"))
            return work.popularity.desc();
        else if (align.equals("recent"))
            return work.id.desc();

        return work.id.desc();
    }

//    @Override
//    public Page<Work> findAllByCompletePopularityDesc(boolean complete, List<String> categories, String keyword, Pageable pageable) {return null;}
//
//    @Override
//    public Page<Work> findNByCategoriesDesc(List<String> categories, String keyword, Pageable pageable) {
//        return null;
//    }

}