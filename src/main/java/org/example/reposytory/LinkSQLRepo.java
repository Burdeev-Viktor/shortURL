package org.example.reposytory;

import org.example.model.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface LinkSQLRepo extends JpaRepository<Link,String> {
    @Query(value = "SELECT * FROM links WHERE origin IS NULL LIMIT 1" ,nativeQuery = true)
    Link getFreeLink();
    @Transactional
    @Modifying
    @Query(value = "UPDATE  links SET origin = NULL, date_del = NULL WHERE links.generated = ?1",nativeQuery = true)
    void delLink(String generated);
}
