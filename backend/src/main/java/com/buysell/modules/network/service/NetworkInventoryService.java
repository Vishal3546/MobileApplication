package com.buysell.modules.network.service;

import com.buysell.exception.BusinessException;
import com.buysell.modules.inventory.entity.InventoryItem;
import com.buysell.modules.inventory.entity.InventoryVisibility;
import com.buysell.modules.inventory.enums.InventoryStatus;
import com.buysell.modules.inventory.repository.InventoryItemRepository;
import com.buysell.modules.network.dto.NetworkInventoryResponse;
import com.buysell.modules.shop.entity.Shop;
import com.buysell.security.CurrentUserService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NetworkInventoryService {

    private final InventoryItemRepository inventoryItemRepository;
    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public Page<NetworkInventoryResponse> getNetworkInventory(String brand, String model, String condition, Pageable pageable) {
        if (!currentUserService.hasPermission("VIEW_NETWORK_INVENTORY")) {
            throw new BusinessException("AUTH_FORBIDDEN", "User does not have permission to view network inventory", HttpStatus.FORBIDDEN);
        }

        Shop currentShop = currentUserService.getCurrentShop();
        if (currentShop == null && !currentUserService.isSuperAdmin()) {
            throw new BusinessException("SHOP_REQUIRED", "User must belong to a shop to access network inventory", HttpStatus.BAD_REQUEST);
        }

        Specification<InventoryItem> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Must be SHOP_NETWORK visibility
            predicates.add(cb.equal(root.get("visibility"), InventoryVisibility.SHOP_NETWORK));
            
            // Must be AVAILABLE
            predicates.add(cb.equal(root.get("status"), InventoryStatus.AVAILABLE));

            // Exclude current shop's own inventory unless SuperAdmin
            if (currentShop != null) {
                predicates.add(cb.notEqual(root.join("branch").join("shop").get("id"), currentShop.getId()));
            }

            // Only show inventory from ACTIVE shops
            predicates.add(cb.equal(root.join("branch").join("shop").get("status"), com.buysell.modules.shop.entity.ShopStatus.ACTIVE));

            if (brand != null && !brand.isEmpty()) {
                predicates.add(cb.equal(root.join("device").get("brand"), brand));
            }
            if (model != null && !model.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.join("device").get("model")), "%" + model.toLowerCase() + "%"));
            }
            if (condition != null && !condition.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("conditionSummary")), "%" + condition.toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<InventoryItem> items = inventoryItemRepository.findAll(spec, pageable);
        return items.map(NetworkInventoryResponse::fromEntity);
    }
}
