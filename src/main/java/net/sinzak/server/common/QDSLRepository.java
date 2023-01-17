package net.sinzak.server.common;

import net.sinzak.server.product.domain.Product;
import net.sinzak.server.work.domain.Work;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface QDSLRepository<T> {

//    Page<T> findAllByCompletePopularityDesc(boolean complete, List<String> categories, String keyword, Pageable pageable);
//    Page<T> findNByCategoriesDesc(List<String> categories, String keyword, Pageable pageable);
}
