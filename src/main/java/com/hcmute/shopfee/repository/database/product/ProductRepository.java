package com.hcmute.shopfee.repository.database.product;

import com.hcmute.shopfee.entity.sql.database.product.BranchProductEntity;
import com.hcmute.shopfee.entity.sql.database.product.ProductEntity;
import com.hcmute.shopfee.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, String> {
    Optional<ProductEntity> findByName(String name);
    Optional<ProductEntity> findByIdAndStatus(String id, ProductStatus status);
    Optional<ProductEntity> findByIdAndStatusNot(String productId, ProductStatus status);
    Page<ProductEntity> findByCategory_IdAndStatusNot(String categoryId, ProductStatus status, Pageable pageable);
    Page<ProductEntity> findByStatusNot(ProductStatus status, Pageable pageable);
    @Query(value = """
            select *
            from product p
            where p.id = ?1
            """, nativeQuery = true)
    List<ProductEntity> getProductById(String productId);
    @Query(value = """
            select p.name
            from product p
            """, nativeQuery = true)
    List<String> getProductNameList();
    @Query(value = """
            select p.*
            from product p
            join branch_product bp on bp.product_id = p.id\s
            where p.category_id regexp ?2
               and p.status regexp ?3
               and p.name regexp ?1
               and p.id regexp ?1
            """, nativeQuery = true)
    Page<ProductEntity> getProductList(String keyRegex, String categoryIdRegex, String productStatusRegex, Pageable pageable);
    @Query(value = """
            select p.*
            from product p
            join branch_product bp on bp.product_id = p.id\s
            where bp.branch_id = ?1
                and bp.status regexp ?5
                and p.category_id regexp ?3
                and p.status regexp ?4
                and p.name regexp ?2
                and p.id regexp ?2
            """, nativeQuery = true)
    Page<ProductEntity> getProductListByBranch(String branchId, String keyRegex, String categoryIdRegex, String productStatusRegex,  String branchProductStatusRegex, Pageable pageable);

    @Query(value = """
            select count(*)
            from product p
            join order_item oi on p.id = oi.product_id
            where p.id = ?1
            """, nativeQuery = true)
    long countOrderItem(String productId);

    @Query(value = """
            select *
            from product p
            join (
            	select sum(id.quantity) as sold_quantity, oi.product_id
            	from order_item oi
            	join item_detail id ON oi.id = id.order_item_id
            	join product p on p.id = oi.product_id
            	join order_bill ob on ob.id = oi.order_bill_id
            	where p.status != 'INACTIVE'
            		and ob.created_at >= DATE_SUB(CURDATE(), INTERVAL 4 WEEK)
            		and DATE_FORMAT(ob.created_at, '%Y-%m-%d') <= CURDATE()
            	group by oi.product_id
            ) as top_product on top_product.product_id = p.id
            order by top_product.sold_quantity desc
            limit ?1
            """, nativeQuery = true)
    List<ProductEntity> getTopProductBySoldQuantity(int limit);

    @Query(value = """
            select p.id, p.created_at, p.description, p.name, p.price, p.status, p.`type`, p.updated_at, p.category_id, p.image_id
            from product p
            left join (
            	select p.id, avg(pr.star) as star, count(*) as quantity
            	from product p
            	join order_item oi on p.id = oi.product_id
            	join product_review pr on oi.product_review_id = pr.id
            	where p.status != 'INACTIVE'
            	group by p.id
            ) as top_product on p.id = top_product.id
            where p.status != 'INACTIVE'
            order by top_product.star desc
            limit ?1
            """, nativeQuery = true)
    List<ProductEntity> getTopRatingProduct(int limit);

    @Query(value = """
            select p.id, p.created_at, p.description, p.name, p.price, p.status, p.`type`, p.updated_at, p.category_id, p.image_id
            from product p\s
            where id not in ?1
            limit ?2
            """, nativeQuery = true)
    List<ProductEntity> getProductWithIdNotIn(List<String> productIdList, int limit);

    @Query(value = """
            select p.id, p.created_at, p.description, p.name, p.price, p.status, p.`type`, p.updated_at, p.category_id, p.image_id, prd.star
            from product p
            join (select product.id as product_id, COALESCE(avg(pr.star), 0) as star
            	  from (select *
            			from product p
            			where p.category_id = ?1
            			and (p.price between ?2 and ?3 or ?2 is null or ?3 is null)
            			and p.status != 'INACTIVE') as product
            			left join order_item oi on oi.product_id = product.id
            			left join product_review pr on oi.product_review_id = pr.id
            			group by product.id ) as prd on prd.product_id = p.id
            where prd.star >= ?4
            or ?4 is NULL
            """, nativeQuery = true)
    Page<ProductEntity> getProductByCategoryIdAndFilter(String categoryId, Long minPrice, Long maxPrice, Integer minStar, Pageable pageable);

    @Query(value = """
            select p.id, p.created_at, p.description, p.name, p.price, p.status, p.`type`, p.updated_at, p.category_id, p.image_id, prd.star
            from product p
            join (select product.id as product_id, COALESCE(avg(pr.star), 0) as star
                  from (select *
                        from product p
                        where (p.price between ?1 and ?2  or ?1 is null or ?2 is null) and p.status != 'INACTIVE') as product
                        left join order_item oi on oi.product_id = product.id
                        left join product_review pr on oi.product_review_id = pr.id
                        group by product.id ) as prd on prd.product_id = p.id
            where prd.star >= ?3 or ?3 is NULL
            """, nativeQuery = true)
    Page<ProductEntity> getAllProductAndFilter(Long minPrice, Long maxPrice, Integer minStar, Pageable pageable);
}
