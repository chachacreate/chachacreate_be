package com.create.chacha.domains.shared.repository;

import com.create.chacha.domains.seller.areas.reviews.dto.response.ReviewListItemDTO;
import com.create.chacha.domains.shared.entity.product.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MypageReviewRepository extends JpaRepository<ReviewEntity, Long> {

    @Query("""
        select new com.create.chacha.domains.seller.areas.reviews.dto.response.ReviewListItemDTO(
            r.id,                              
            r.product.id,                  
            r.createdAt,                   
            null,                             
            null,                             
            r.member.id,                 
            m.name,                       
            r.content,                      
            r.createdAt,                   
            concat(cast(r.rating as string), '/5.0'),
            cast(count(rl.id) as integer),  
            r.updatedAt                       
        )
        from ReviewEntity r
        left join r.member m
        left join com.create.chacha.domains.shared.entity.product.ReviewLikeEntity rl
               on rl.review = r and rl.isDeleted = false
        where r.member.id = :memberId and r.isDeleted = false
        group by r.id, r.product.id, r.createdAt, r.member.id, m.name, r.content, r.rating, r.updatedAt
        order by r.createdAt desc
        """)
    List<ReviewListItemDTO> findReviewsByMemberId(@Param("memberId") Long memberId);
}
