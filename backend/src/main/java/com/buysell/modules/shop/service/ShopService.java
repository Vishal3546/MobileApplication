package com.buysell.modules.shop.service;

import com.buysell.exception.BusinessException;
import com.buysell.modules.shop.dto.CreateShopRequest;
import com.buysell.modules.shop.dto.ShopResponse;
import com.buysell.modules.shop.dto.UpdateShopRequest;
import com.buysell.modules.shop.entity.Shop;
import com.buysell.modules.shop.entity.ShopMembership;
import com.buysell.modules.shop.entity.ShopMembershipRole;
import com.buysell.modules.shop.entity.ShopStatus;
import com.buysell.modules.shop.repository.ShopMembershipRepository;
import com.buysell.modules.shop.repository.ShopRepository;
import com.buysell.modules.user.entity.User;
import com.buysell.modules.user.repository.UserRepository;
import com.buysell.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;
    private final ShopMembershipRepository shopMembershipRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public ShopResponse createShop(CreateShopRequest request) {
        if (!currentUserService.hasPermission("CREATE_SHOP")) {
            throw new BusinessException("AUTH_FORBIDDEN", "User does not have permission to create shop", HttpStatus.FORBIDDEN);
        }

        String shopCode = generateShopCode();
        
        User owner = null;
        if (request.getOwnerUserId() != null) {
            owner = userRepository.findById(request.getOwnerUserId())
                    .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "Owner user not found", HttpStatus.NOT_FOUND));
        }

        Shop shop = Shop.builder()
                .shopCode(shopCode)
                .name(request.getName())
                .legalName(request.getLegalName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .status(ShopStatus.ACTIVE)
                .owner(owner)
                .build();

        shop = shopRepository.save(shop);

        if (owner != null) {
            ShopMembership membership = ShopMembership.builder()
                    .shop(shop)
                    .user(owner)
                    .role(ShopMembershipRole.OWNER)
                    .build();
            shopMembershipRepository.save(membership);
        }

        return mapToResponse(shop);
    }

    @Transactional(readOnly = true)
    public List<ShopResponse> getAllShops() {
        if (!currentUserService.hasPermission("VIEW_SHOPS")) {
            throw new BusinessException("AUTH_FORBIDDEN", "User does not have permission to view all shops", HttpStatus.FORBIDDEN);
        }
        return shopRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ShopResponse getShopById(UUID id) {
        if (!currentUserService.hasPermission("VIEW_SHOPS")) {
            Shop currentShop = currentUserService.getCurrentShop();
            if (currentShop == null || !currentShop.getId().equals(id)) {
                throw new BusinessException("AUTH_FORBIDDEN", "User does not have permission to view this shop", HttpStatus.FORBIDDEN);
            }
        }
        
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new BusinessException("SHOP_NOT_FOUND", "Shop not found", HttpStatus.NOT_FOUND));
                
        return mapToResponse(shop);
    }

    @Transactional
    public ShopResponse updateShop(UUID id, UpdateShopRequest request) {
        if (!currentUserService.hasPermission("UPDATE_SHOP")) {
            throw new BusinessException("AUTH_FORBIDDEN", "User does not have permission to update shop", HttpStatus.FORBIDDEN);
        }

        // Shop owners can only update their own shop
        if (!currentUserService.isSuperAdmin()) {
            Shop currentShop = currentUserService.getCurrentShop();
            if (currentShop == null || !currentShop.getId().equals(id)) {
                throw new BusinessException("AUTH_FORBIDDEN", "User can only update their own shop", HttpStatus.FORBIDDEN);
            }
        }

        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new BusinessException("SHOP_NOT_FOUND", "Shop not found", HttpStatus.NOT_FOUND));

        if (request.getName() != null) shop.setName(request.getName());
        if (request.getLegalName() != null) shop.setLegalName(request.getLegalName());
        if (request.getPhone() != null) shop.setPhone(request.getPhone());
        if (request.getEmail() != null) shop.setEmail(request.getEmail());
        if (request.getAddress() != null) shop.setAddress(request.getAddress());
        if (request.getCity() != null) shop.setCity(request.getCity());
        if (request.getState() != null) shop.setState(request.getState());
        if (request.getPostalCode() != null) shop.setPostalCode(request.getPostalCode());
        
        if (request.getStatus() != null && currentUserService.isSuperAdmin()) {
            shop.setStatus(request.getStatus());
        }

        shop = shopRepository.save(shop);
        return mapToResponse(shop);
    }

    private String generateShopCode() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        return String.format("SH-%06d", number);
    }

    private ShopResponse mapToResponse(Shop shop) {
        return ShopResponse.builder()
                .id(shop.getId())
                .shopCode(shop.getShopCode())
                .name(shop.getName())
                .legalName(shop.getLegalName())
                .phone(shop.getPhone())
                .email(shop.getEmail())
                .address(shop.getAddress())
                .city(shop.getCity())
                .state(shop.getState())
                .postalCode(shop.getPostalCode())
                .status(shop.getStatus())
                .ownerUserId(shop.getOwner() != null ? shop.getOwner().getId() : null)
                .ownerName(shop.getOwner() != null ? shop.getOwner().getUsername() : null)
                .createdAt(shop.getCreatedAt())
                .updatedAt(shop.getUpdatedAt())
                .build();
    }
}
