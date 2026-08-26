package com.buysell.modules.shop.repository;

import com.buysell.modules.shop.entity.ShopMembership;
import com.buysell.modules.shop.entity.ShopMembershipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShopMembershipRepository extends JpaRepository<ShopMembership, UUID> {
    Optional<ShopMembership> findByUserIdAndStatus(UUID userId, ShopMembershipStatus status);
    List<ShopMembership> findByShopId(UUID shopId);
}
