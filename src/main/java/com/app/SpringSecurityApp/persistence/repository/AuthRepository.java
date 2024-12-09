package com.app.SpringSecurityApp.persistence.repository;

import com.app.SpringSecurityApp.persistence.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AuthRepository extends JpaRepository<TokenEntity, Long> {

    @Query(
        """
            select t from TokenEntity as t
            where t.user.id = :userId and t.expired = false and t.revoked = false
        """
    )
   Optional< List<TokenEntity> > findAllValidToken(@Param("userId") Long userId);

    Optional<TokenEntity> findByToken(String token);

}
