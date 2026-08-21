package com.buysell.modules.purchase.service;

import com.buysell.modules.media.entity.MediaFile;
import com.buysell.modules.media.enums.MediaType;
import com.buysell.modules.media.service.MediaService;
import com.buysell.modules.purchase.entity.PurchaseReceipt;
import com.buysell.modules.purchase.entity.PurchaseTransaction;
import com.buysell.modules.purchase.repository.PurchaseReceiptRepository;
import com.buysell.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PurchaseReceiptService {

    private final PurchaseReceiptRepository receiptRepository;
    private final CurrentUserService currentUserService;
    private final MediaService mediaService;

    @Transactional
    public PurchaseReceipt generateReceipt(PurchaseTransaction purchase) {
        String receiptNumber = "PUR-RCP-" + purchase.getPurchaseNumber().substring(4);
        
        // Generate a text receipt
        String receiptContent = "RECEIPT NUMBER: " + receiptNumber + "\n"
                + "PURCHASE: " + purchase.getPurchaseNumber() + "\n"
                + "CUSTOMER: " + purchase.getCustomer().getId() + "\n"
                + "AMOUNT: " + purchase.getFinalPrice() + "\n";
                
        byte[] contentBytes = receiptContent.getBytes(StandardCharsets.UTF_8);
        
        MultipartFile multipartFile = new MultipartFile() {
            @Override public String getName() { return receiptNumber + ".txt"; }
            @Override public String getOriginalFilename() { return receiptNumber + ".txt"; }
            @Override public String getContentType() { return "text/plain"; }
            @Override public boolean isEmpty() { return false; }
            @Override public long getSize() { return contentBytes.length; }
            @Override public byte[] getBytes() throws IOException { return contentBytes; }
            @Override public InputStream getInputStream() throws IOException { return new ByteArrayInputStream(contentBytes); }
            @Override public void transferTo(File dest) throws IOException, IllegalStateException { }
        };

        MediaFile generatedMedia = mediaService.uploadMedia(multipartFile, MediaType.RECEIPT, "receipts");

        PurchaseReceipt receipt = PurchaseReceipt.builder()
                .purchaseTransaction(purchase)
                .receiptNumber(receiptNumber)
                .mediaFile(generatedMedia)
                .generatedBy(currentUserService.getCurrentUser())
                .build();
                
        return receiptRepository.save(receipt);
    }

    @Transactional(readOnly = true)
    public Optional<PurchaseReceipt> getReceiptForPurchase(UUID purchaseId) {
        return receiptRepository.findByPurchaseTransactionId(purchaseId);
    }
}
