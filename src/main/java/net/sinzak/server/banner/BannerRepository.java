package net.sinzak.server.banner;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BannerRepository extends JpaRepository<Banner,Long> {
    @Query("select b from Banner b where b.title = 'user'")
    Banner findAuthorBanner();
}
