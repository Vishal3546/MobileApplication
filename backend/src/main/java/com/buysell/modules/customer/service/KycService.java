package com.buysell.modules.customer.service;

import com.buysell.modules.audit.service.AuditService;
import com.buysell.modules.customer.dto.KycDocumentResponse;
import com.buysell.modules.customer.dto.UploadKycRequest;
import com.buysell.modules.customer.entity.Customer;
import com.buysell.modules.customer.entity.CustomerDocument;
import com.buysell.modules.customer.enums.VerificationStatus;
import com.buysell.modules.customer.repository.CustomerDocumentRepository;
import com.buysell.modules.customer.repository.CustomerRepository;
import com.buysell.modules.media.service.MediaService;
import com.buysell.security.CurrentUserService;
import com.buysell.security.KycEncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KycService {

    private final CustomerDocumentRepository documentRepository;
    private final CustomerRepository customerRepository;
    private final CustomerService customerService; // For access validation
    private final MediaService mediaService;
    private final KycEncryptionUtil encryptionUtil;
    private final CurrentUserService currentUserService;
    private final AuditService auditService;

    @Transactional
    public KycDocumentResponse uploadKyc(UUID customerId, UploadKycRequest request) {
        Customer customer = customerService.getAndValidateAccess(customerId);
        
        CustomerDocument document = CustomerDocument.builder()
                .customer(customer)
                .idType(request.getIdType())
                .idNumberEncrypted(encryptionUtil.encrypt(request.getIdNumber()))
                .idNumberHash(encryptionUtil.hash(request.getIdNumber()))
                .idNumberMasked(encryptionUtil.mask(request.getIdNumber()))
                .verificationStatus(VerificationStatus.PENDING)
                .build();
                
        if (request.getFrontMediaId() != null) {
            document.setFrontMedia(mediaService.getMedia(request.getFrontMediaId()));
        }
        if (request.getBackMediaId() != null) {
            document.setBackMedia(mediaService.getMedia(request.getBackMediaId()));
        }
        if (request.getPhotoMediaId() != null) {
            document.setPhotoMedia(mediaService.getMedia(request.getPhotoMediaId()));
        }
        
        document = documentRepository.save(document);
        
        // Update customer verification status if needed
        customer.setVerificationStatus(VerificationStatus.PENDING);
        customerRepository.save(customer);
        
        auditService.logAction(currentUserService.getCurrentUserId(), getCurrentBranchIdOrNull(), "KYC_DOCUMENT_UPLOADED", "CustomerDocument", document.getId(), null, null, null, null);
        
        return mapToResponse(document);
    }

    @Transactional(readOnly = true)
    public List<KycDocumentResponse> getDocumentsByCustomer(UUID customerId) {
        // Validates branch access
        customerService.getAndValidateAccess(customerId);
        
        return documentRepository.findByCustomerId(customerId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public KycDocumentResponse approveDocument(UUID customerId, UUID documentId, String notes) {
        Customer customer = customerService.getAndValidateAccess(customerId);
        CustomerDocument document = getDocument(documentId, customerId);
        
        document.setVerificationStatus(VerificationStatus.VERIFIED);
        document.setVerificationNotes(notes);
        document.setVerifiedAt(LocalDateTime.now());
        document.setVerifiedBy(currentUserService.getCurrentUser());
        
        document = documentRepository.save(document);
        
        customer.setVerificationStatus(VerificationStatus.VERIFIED);
        customerRepository.save(customer);
        
        auditService.logAction(currentUserService.getCurrentUserId(), getCurrentBranchIdOrNull(), "KYC_DOCUMENT_APPROVED", "CustomerDocument", document.getId(), null, null, null, null);
        
        return mapToResponse(document);
    }
    
    @Transactional
    public KycDocumentResponse rejectDocument(UUID customerId, UUID documentId, String notes) {
        Customer customer = customerService.getAndValidateAccess(customerId);
        CustomerDocument document = getDocument(documentId, customerId);
        
        document.setVerificationStatus(VerificationStatus.REJECTED);
        document.setVerificationNotes(notes);
        document.setVerifiedAt(LocalDateTime.now());
        document.setVerifiedBy(currentUserService.getCurrentUser());
        
        document = documentRepository.save(document);
        
        customer.setVerificationStatus(VerificationStatus.REJECTED);
        customerRepository.save(customer);
        
        auditService.logAction(currentUserService.getCurrentUserId(), getCurrentBranchIdOrNull(), "KYC_DOCUMENT_REJECTED", "CustomerDocument", document.getId(), null, null, null, null);
        
        return mapToResponse(document);
    }
    
    public String getUnmaskedIdNumber(UUID documentId) {
        // Normally this would require a specific highly-restricted permission check.
        // Assuming VERIFY_KYC allows seeing the unmasked number.
        if (!currentUserService.hasPermission("VERIFY_KYC") && !currentUserService.isSuperAdmin()) {
            throw new RuntimeException("Not authorized to view unmasked KYC data");
        }
        
        CustomerDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        
        // Ensure access to customer
        customerService.getAndValidateAccess(document.getCustomer().getId());
        
        return encryptionUtil.decrypt(document.getIdNumberEncrypted());
    }

    private CustomerDocument getDocument(UUID documentId, UUID customerId) {
        CustomerDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));
        if (!document.getCustomer().getId().equals(customerId)) {
            throw new RuntimeException("Document does not belong to customer");
        }
        return document;
    }

    private KycDocumentResponse mapToResponse(CustomerDocument document) {
        return KycDocumentResponse.builder()
                .id(document.getId())
                .customerId(document.getCustomer().getId())
                .idType(document.getIdType())
                .idNumberMasked(document.getIdNumberMasked())
                .frontMediaId(document.getFrontMedia() != null ? document.getFrontMedia().getId() : null)
                .backMediaId(document.getBackMedia() != null ? document.getBackMedia().getId() : null)
                .photoMediaId(document.getPhotoMedia() != null ? document.getPhotoMedia().getId() : null)
                .verificationStatus(document.getVerificationStatus())
                .verificationNotes(document.getVerificationNotes())
                .verifiedAt(document.getVerifiedAt())
                .build();
    }

    private UUID getCurrentBranchIdOrNull() {
        try {
            return currentUserService.getCurrentBranch().getId();
        } catch (Exception e) {
            return null;
        }
    }
}
