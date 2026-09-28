package com.example.invitedate.service.impl;

import com.example.invitedate.dto.*;
import com.example.invitedate.entity.Invitation;
import com.example.invitedate.enums.InvitationStatus;
import com.example.invitedate.exception.*;
import com.example.invitedate.repository.InvitationRepository;
import com.example.invitedate.service.InvitationService;
import com.example.invitedate.util.QrCodeGenerator;
import com.example.invitedate.service.InvitationNotificationService;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InvitationServiceImpl implements InvitationService {
    private final InvitationRepository repository;
    private final QrCodeGenerator qrCodeGenerator;
    private final ObjectMapper objectMapper;
    private final InvitationNotificationService notificationService;
    @Value("${app.public-base-url}") private String publicBaseUrl;

    @Override @Transactional
    public InvitationResponse create(CreateInvitationRequest request) {
        Invitation invitation = repository.save(Invitation.builder().id(UUID.randomUUID())
                .senderName(request.senderName().trim()).senderEmail(request.senderEmail().trim().toLowerCase(Locale.ROOT)).receiverName(request.receiverName().trim())
                .dateType(request.dateType()).proposedDate(request.proposedDate()).customMessage(blankToNull(request.customMessage()))
                .experience(writeJson(request.experience())).ownerToken(UUID.randomUUID())
                .expiresAt(OffsetDateTime.now().plus(30, ChronoUnit.DAYS))
                .status(InvitationStatus.CREATED).viewCount(0).build());
        return ownerResponse(invitation, true);
    }

    @Override @Transactional
    public PublicInvitationResponse getPublic(UUID id) {
        if (repository.incrementViewCount(id) == 0) throw new InvitationNotFoundException();
        Invitation invitation = find(id);
        if (OffsetDateTime.now().isAfter(invitation.getExpiresAt())) throw new InvalidInvitationStateException("This invitation has expired");
        return new PublicInvitationResponse(invitation.getId(), invitation.getSenderName(), invitation.getReceiverName(),
                invitation.getDateType(), invitation.getProposedDate(), invitation.getCustomMessage(), invitation.getStatus(),
                readJson(invitation.getExperience()), invitation.getExpiresAt());
    }

    @Override @Transactional(readOnly = true)
    public byte[] getQrCode(UUID id) { return qrCodeGenerator.png(invitationUrl(find(id).getId())); }

    @Override @Transactional
    public TicketResponse respond(UUID id, RespondInvitationRequest request) {
        Invitation invitation = find(id);
        if (invitation.getStatus() != InvitationStatus.CREATED) {
            throw new InvalidInvitationStateException("This invitation has already been answered");
        }
        if (request.status() == InvitationStatus.CREATED) {
            throw new InvalidInvitationStateException("Response status must be ACCEPTED or DECLINED");
        }
        invitation.setStatus(request.status());
        invitation.setResponseDetails(writeJson(request.responseDetails()));
        String ticketCode = "DATE-" + invitation.getId().toString().substring(0, 8).toUpperCase(Locale.ROOT);
        String message = request.status() == InvitationStatus.ACCEPTED ? "Invitation accepted" : "Invitation declined";
        notificationService.sendResponse(invitation, request.status(), request.responseDetails(), ticketCode);
        return new TicketResponse(invitation.getId(), ticketCode, message, OffsetDateTime.now());
    }

    @Override @Transactional(readOnly = true)
    public InvitationStatusResponse getStatus(UUID id, UUID ownerToken) {
        Invitation invitation = find(id);
        if (!invitation.getOwnerToken().equals(ownerToken)) throw new InvitationNotFoundException();
        return new InvitationStatusResponse(invitation.getId(), invitation.getStatus(), readJson(invitation.getResponseDetails()),
                invitation.getViewCount(), invitation.getUpdatedAt());
    }

    private Invitation find(UUID id) { return repository.findById(id).orElseThrow(InvitationNotFoundException::new); }
    private String invitationUrl(UUID id) { return publicBaseUrl.replaceAll("/$", "") + "/invite/" + id; }
    private String blankToNull(String value) { return value == null || value.isBlank() ? null : value.trim(); }
    private String writeJson(Map<String, Object> details) {
        if (details == null || details.isEmpty()) return null;
        try { return objectMapper.writeValueAsString(details); }
        catch (JacksonException ex) { throw new IllegalArgumentException("responseDetails must be JSON serializable", ex); }
    }
    private Map<String, Object> readJson(String json) {
        if (json == null || json.isBlank()) return Map.of();
        try { return objectMapper.readValue(json, new TypeReference<>() {}); }
        catch (JacksonException ex) { throw new IllegalStateException("Stored response JSON is invalid", ex); }
    }
    private InvitationResponse ownerResponse(Invitation invitation, boolean includeQr) {
        String url = invitationUrl(invitation.getId());
        return new InvitationResponse(invitation.getId(), invitation.getSenderName(), invitation.getSenderEmail(), invitation.getReceiverName(), invitation.getDateType(),
                invitation.getProposedDate(), invitation.getCustomMessage(), invitation.getStatus(), readJson(invitation.getResponseDetails()),
                invitation.getViewCount(), invitation.getCreatedAt(), invitation.getUpdatedAt(), url,
                includeQr ? qrCodeGenerator.pngDataUrl(url) : null, readJson(invitation.getExperience()),
                ownerUrl(invitation), invitation.getExpiresAt());
    }
    private String ownerUrl(Invitation invitation) {
        return publicBaseUrl.replaceAll("/$", "") + "/owner/" + invitation.getId() + "?token=" + invitation.getOwnerToken();
    }
}
