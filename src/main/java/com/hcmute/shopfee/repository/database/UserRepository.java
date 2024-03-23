package com.hcmute.shopfee.repository.database;

import com.hcmute.shopfee.entity.sql.database.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByEmail(String email);

    @Query(value = """
            select u.id, u.avatar_id, u.avatar_url, u.birth_date, u.coin, u.created_at, u.email, u.first_name, u.gender, u.last_name, u.password, u.phone_number, u.status, u.updated_at
            from `user` u\s
            where concat_ws(' ', u.first_name, u.last_name, u.id, u.email, u.phone_number) like concat('%', ?1, '%')\s
            and u.status like concat('%', ?2, '%')\s
            """, nativeQuery = true)
    Page<UserEntity> getUserWithFilterAndKey(String key, String status, Pageable pageable);
}
