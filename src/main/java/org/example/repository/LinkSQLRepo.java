package org.example.repository;

import org.example.model.Link;
import org.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface LinkSQLRepo extends JpaRepository<Link,String> {
    @Query(value = "SELECT * FROM links WHERE origin IS NULL LIMIT 1" ,nativeQuery = true)
    Link getFreeLink();
    @Query(value = "SELECT COUNT(*) FROM links WHERE origin IS NULL" ,nativeQuery = true)
    long getCountFreeLink();
    @Query(value = "SELECT COUNT(*) FROM links WHERE links.active IS true" ,nativeQuery = true)
    long getCountActiveLink();
    @Query(value = "SELECT * FROM links WHERE links.active IS true LIMIT ?1 OFFSET ?2" ,nativeQuery = true)
    List<Link> getActiveLinksByLimit(long lowerLimit,long upperLimit);
    List<Link> findByUser(User user);
    @Query(value = "SELECT origin FROM links WHERE links.id_link = ?1 and links.active = true" ,nativeQuery = true)
    String findOriginById(String key);
    @Query(value = "SELECT * FROM links WHERE date_del < ?1" ,nativeQuery = true)
    List<Link> getOldLinks(Date now);
}
