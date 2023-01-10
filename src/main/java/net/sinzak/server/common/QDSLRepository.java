package net.sinzak.server.common;

import net.sinzak.server.product.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

public interface QDSLRepository {

    Page<Product> findAllByCompletePopularityDesc(boolean complete, Pageable pageable);
}
