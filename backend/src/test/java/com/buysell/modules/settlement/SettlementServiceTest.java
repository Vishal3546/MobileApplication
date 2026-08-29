package com.buysell.modules.settlement;

import com.buysell.exception.BusinessException;

import com.buysell.modules.inventory.entity.StockTransfer;
import com.buysell.modules.inventory.entity.StockTransferType;
import com.buysell.modules.inventory.repository.StockTransferRepository;
import com.buysell.modules.settlement.entity.ShopSettlement;
import com.buysell.modules.settlement.repository.ShopSettlementRepository;
import com.buysell.modules.settlement.service.SettlementService;
import com.buysell.modules.audit.service.AuditService;
import com.buysell.modules.branch.entity.Branch;
import com.buysell.modules.shop.entity.Shop;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SettlementServiceTest {

    @Mock
    private ShopSettlementRepository settlementRepository;

    @Mock
    private StockTransferRepository transferRepository;

    @SuppressWarnings("unused")
    @Mock
    private AuditService auditService;

    @InjectMocks
    private SettlementService settlementService;

    private Shop sourceShop;
    private Shop destinationShop;
    private StockTransfer transfer;

    @BeforeEach
    public void setup() {
        sourceShop = new Shop();
        sourceShop.setId(UUID.randomUUID());

        destinationShop = new Shop();
        destinationShop.setId(UUID.randomUUID());

        Branch sourceBranch = new Branch();
        sourceBranch.setShop(sourceShop);

        Branch destBranch = new Branch();
        destBranch.setShop(destinationShop);

        transfer = new StockTransfer();
        transfer.setId(UUID.randomUUID());
        transfer.setTransferType(StockTransferType.NETWORK);
        transfer.setFromBranch(sourceBranch);
        transfer.setToBranch(destBranch);
        
        com.buysell.modules.inventory.entity.InventoryItem item = new com.buysell.modules.inventory.entity.InventoryItem();
        item.setSellingPrice(BigDecimal.valueOf(10000));
        
        com.buysell.modules.inventory.entity.StockTransferItem transferItem = new com.buysell.modules.inventory.entity.StockTransferItem();
        transferItem.setInventoryItem(item);
        
        transfer.setItems(List.of(transferItem));
    }

    @Test
    void createSettlement_Success() {
        when(transferRepository.findById(transfer.getId())).thenReturn(Optional.of(transfer));
        when(settlementRepository.existsByTransferId(transfer.getId())).thenReturn(false);
        when(settlementRepository.getNextSettlementNumberSequence()).thenReturn(1L);

        settlementService.createSettlementFromTransfer(transfer.getId());

        verify(settlementRepository).save(any(ShopSettlement.class));
    }

    @Test
    void createSettlement_ThrowsIfAlreadyExists() {
        when(transferRepository.findById(transfer.getId())).thenReturn(Optional.of(transfer));
        when(settlementRepository.existsByTransferId(transfer.getId())).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> settlementService.createSettlementFromTransfer(transfer.getId()));
        assertNotNull(ex);
    }

    @Test
    void createSettlement_ThrowsIfSameShop() {
        transfer.getToBranch().setShop(sourceShop); // Same shop
        
        when(transferRepository.findById(transfer.getId())).thenReturn(Optional.of(transfer));
        when(settlementRepository.existsByTransferId(transfer.getId())).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class, () -> settlementService.createSettlementFromTransfer(transfer.getId()));
        assertNotNull(ex);
    }
}
