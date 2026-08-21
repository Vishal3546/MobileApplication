package com.buysell.modules.customer.service;

import com.buysell.modules.audit.service.AuditService;
import com.buysell.modules.customer.dto.CaptureConsentRequest;
import com.buysell.modules.customer.dto.ConsentResponse;
import com.buysell.modules.customer.entity.Customer;
import com.buysell.modules.customer.entity.CustomerConsent;
import com.buysell.modules.customer.repository.CustomerConsentRepository;
import com.buysell.modules.media.service.MediaService;
import com.buysell.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsentService {

    private final CustomerConsentRepository consentRepository;
    private final CustomerService customerService;
    private final MediaService mediaService;
    private final CurrentUserService currentUserService;
    private final AuditService auditService;

    @Transactional
    public ConsentResponse captureConsent(UUID customerId, CaptureConsentRequest request) {
        Customer customer = customerService.getAndValidateAccess(customerId);
        
        CustomerConsent consent = CustomerConsent.builder()
                .customer(customer)
                .consentType(request.getConsentType())
                .consentTextVersion(request.getConsentTextVersion())
                .ipAddress(request.getIpAddress())
                .deviceInfo(request.getDeviceInfo())
                .capturedBy(currentUserService.getCurrentUser())
                .build();
                
        if (request.getSignatureMediaId() != null) {
            consent.setSignatureMedia(mediaService.getMedia(request.getSignatureMediaId()));
        }
        if (request.getVideoMediaId() != null) {
            consent.setVideoMedia(mediaService.getMedia(request.getVideoMediaId()));
        }
        
        consent = consentRepository.save(consent);
        
        auditService.logAction(currentUserService.getCurrentUserId(), getCurrentBranchIdOrNull(), "CONSENT_CAPTURED", "CustomerConsent", consent.getId(), null, null, request.getIpAddress(), request.getDeviceInfo());
        
        return mapToResponse(consent);
    }

    @Transactional(readOnly = true)
    public List<ConsentResponse> getConsentsByCustomer(UUID customerId) {
        customerService.getAndValidateAccess(customerId);
        
        return consentRepository.findByCustomerId(customerId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ConsentResponse mapToResponse(CustomerConsent consent) {
        return ConsentResponse.builder()
                .id(consent.getId())
                .customerId(consent.getCustomer().getId())
                .consentType(consent.getConsentType())
                .consentTextVersion(consent.getConsentTextVersion())
                .signatureMediaId(consent.getSignatureMedia() != null ? consent.getSignatureMedia().getId() : null)
                .videoMediaId(consent.getVideoMedia() != null ? consent.getVideoMedia().getId() : null)
                .capturedAt(consent.getCapturedAt())
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
