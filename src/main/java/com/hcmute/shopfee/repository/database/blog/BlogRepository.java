package com.hcmute.shopfee.repository.database.blog;

import com.hcmute.shopfee.entity.sql.database.blog.BlogEntity;
import com.hcmute.shopfee.enums.BlogStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlogRepository extends JpaRepository<BlogEntity, String> {
    Optional<BlogEntity> findByIdAndIsDeletedFalse(String blogId);
    Optional<BlogEntity> findByIdAndStatusAndIsDeletedFalse(String blogId, BlogStatus status);
    Page<BlogEntity> findByIsDeletedFalse(Pageable pageable);
    Page<BlogEntity> findByStatusAndIsDeletedFalse(BlogStatus status, Pageable pageable);
    @Query(value = """
            select b.*, b.id as fakeColumns
            from blog b
            where b.is_deleted = false
            and b.status = ?1
            """, nativeQuery = true)
    Page<BlogEntity> getByStatusAndIsDeletedFalse(String status, Pageable pageable);
}
